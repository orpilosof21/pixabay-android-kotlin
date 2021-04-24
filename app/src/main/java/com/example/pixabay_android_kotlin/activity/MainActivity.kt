package com.example.pixabay_android_kotlin.activity

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.adapter.PixabayImageAdapter
import com.example.myapp.model.PixabayImage
import com.example.myapp.model.PixabayResponse
import com.example.myapp.rest.PixabayService
import com.example.pixabay_android_kotlin.R
import com.example.pixabay_android_kotlin.REST.API_KEY
import com.example.pixabay_android_kotlin.REST.ApiClient
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity() {
    val INIT_PAGE = 0
    val PER_PAGE = 6

    //const
    private var retrofit: Retrofit? = null
    private lateinit var recycler_view: RecyclerView;
    private val cur_image_list: ArrayList<PixabayImage> = ArrayList<PixabayImage>()
    private val fav_image_list: ArrayList<PixabayImage> = ArrayList<PixabayImage>()
    private val fav_ids: MutableSet<Int> = HashSet<Int>()

    private lateinit var input_text: EditText
    private var query = ""
    private var cur_page: Int = INIT_PAGE
    private var visibleImageCount = 0
    private  var totalImageCount:Int = 0
    private  var lastImageCount:Int = 0
    private var recyclerViewLoading = true
    private lateinit var tabs: TabLayout;
    private var current_shown_tab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // init fields
        // init fields
        initInputText()

        initRecyclerView()

        initTabsView()
    }

    private fun initInputText() {
        input_text = findViewById<EditText>(R.id.inputQueryText);
        input_text.setOnKeyListener(View.OnKeyListener setOnKeyListener@{ v: View?, keyCode: Int, event: KeyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                query = input_text.getText().toString()
                cur_page = INIT_PAGE
                cur_image_list.clear()
                connectAndGetApiData()
                return@setOnKeyListener true
            }
            false
        })
    }

    private fun initRecyclerView() {
        recycler_view = findViewById(R.id.recycler_view)
        recycler_view.setHasFixedSize(true)
        recycler_view.setLayoutManager(GridLayoutManager(this, 2))
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleImageCount = recyclerView.childCount
                totalImageCount = recyclerView.layoutManager!!.itemCount
                lastImageCount =
                    (recyclerView.layoutManager as GridLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                if (tabs!!.selectedTabPosition == 0 && recyclerViewLoading) {
                    if (lastImageCount + visibleImageCount >= totalImageCount) {
                        connectAndGetApiData()
                        recyclerViewLoading = false
                    }
                } else {
                    recyclerViewLoading = true
                }
            }
        })
    }
    fun connectAndGetApiData() {
        if (tabs.selectedTabPosition == 1) {
            return
        }
        if (retrofit == null) {
            retrofit = ApiClient.getApiClient()
        }
        val pixabayService: PixabayService = retrofit!!.create(
            PixabayService::class.java
        )
        cur_page++
        pixabayService?.getImages(API_KEY,
            query,
            "photo",
            cur_page,
            PER_PAGE)?.enqueue(object : Callback<PixabayResponse>{
            override fun onResponse(
                call: Call<PixabayResponse>,
                response: Response<PixabayResponse>
            ) {
                val images = response.body().getHits()
                addAndRenderDataCurList(images)
            }

            override fun onFailure(call: Call<PixabayResponse>?, t: Throwable?) {
                Log.println(
                    Log.ERROR,
                    "connectAndGetApiData",
                    t.toString()
                )
            }
        })
    }

    private fun addAndRenderDataCurList(images: List<PixabayImage>) {
        cur_image_list.addAll(images)
        renderSearchTab()
    }

    private fun initTabsView() {
        tabs = findViewById(R.id.tabs)
        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> renderSearchTab()
                    1 -> renderFavTab()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun renderImages(images: List<PixabayImage>) {
        val pixabayImageAdapter =
            PixabayImageAdapter(images, R.layout.image_item, applicationContext)
            pixabayImageAdapter.setOnItemClickListener(object :
            PixabayImageAdapter.OnItemClickListener {
            override fun onImageClicked(
                v: View?,
                position: Int,
                pixabayImage: PixabayImage?
            ) {
                if (pixabayImage != null) {
                    manageFavList(pixabayImage)
                }
            }
        })
        recycler_view.adapter = pixabayImageAdapter
    }

    private fun manageFavList(pixabayImage: PixabayImage) {
        if (current_shown_tab==1 && fav_ids.contains(pixabayImage.id)) {
            fav_image_list.remove(pixabayImage)
            fav_ids.remove(pixabayImage.id)
            return
        }
        if(current_shown_tab==0 && !(fav_ids.contains(pixabayImage.id))){
            fav_image_list.add(pixabayImage)
            fav_ids.add(pixabayImage.id)
        }
    }

    fun renderSearchTab() {
        current_shown_tab = 0
        renderImages(cur_image_list)
    }

    fun renderFavTab() {
        current_shown_tab = 1
        renderImages(fav_image_list)
    }
}