package com.example.pixabay_android_kotlin.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
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
    private val INIT_PAGE = 0
    private val DISPLAY_ROWS = 3
    private val PER_PAGE = DISPLAY_ROWS*3

    //const
    private var retrofit: Retrofit? = null
    private lateinit var recyclerView: RecyclerView;
    private val curImageList: ArrayList<PixabayImage> = ArrayList()
    private val favImageList: ArrayList<PixabayImage> = ArrayList()
    private val favIds: MutableSet<Int> = HashSet()

    private lateinit var inputText: EditText
    private var query = ""
    private var curPage: Int = INIT_PAGE
    private var visibleImageCount = 0
    private  var totalImageCount:Int = 0
    private  var lastImageCount:Int = 0
    private var recyclerViewLoading = true
    private lateinit var tabs: TabLayout;
    private var currentShownTab = 0

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
        inputText = findViewById(R.id.inputQueryText);
        inputText.setOnKeyListener(View.OnKeyListener setOnKeyListener@{ v: View?, keyCode: Int, event: KeyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                query = inputText.getText().toString()
                curPage = INIT_PAGE
                curImageList.clear()
                connectAndGetApiData()
                v?.hideKeyboard()
                return@setOnKeyListener true
            }
            false
        })
    }

    private fun View.hideKeyboard(){
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(GridLayoutManager(this, DISPLAY_ROWS))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleImageCount = recyclerView.childCount
                totalImageCount = recyclerView.layoutManager!!.itemCount
                lastImageCount =
                    (recyclerView.layoutManager as GridLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                if (tabs.selectedTabPosition == 0 && recyclerViewLoading) {
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
        curPage++
        pixabayService.getImages(API_KEY,
            query,
            "photo",
            curPage,
            PER_PAGE).enqueue(object : Callback<PixabayResponse>{
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
        curImageList.addAll(images)
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
        recyclerView.adapter = pixabayImageAdapter
    }

    private fun manageFavList(pixabayImage: PixabayImage) {
        if (currentShownTab==1 && favIds.contains(pixabayImage.id)) {
            favImageList.remove(pixabayImage)
            favIds.remove(pixabayImage.id)
            renderFavTab()
            return
        }
        if(currentShownTab==0 && !(favIds.contains(pixabayImage.id))){
            favImageList.add(pixabayImage)
            favIds.add(pixabayImage.id)
        }
    }

    fun renderSearchTab() {
        currentShownTab = 0
        renderImages(curImageList)
    }

    fun renderFavTab() {
        currentShownTab = 1
        renderImages(favImageList)
    }
}