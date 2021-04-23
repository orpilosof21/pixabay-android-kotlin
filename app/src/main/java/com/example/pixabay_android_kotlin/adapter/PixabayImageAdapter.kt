package com.example.myapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.model.PixabayImage
import com.example.pixabay_android_kotlin.R
import com.squareup.picasso.Picasso

class PixabayImageAdapter(
    private val imageList: List<PixabayImage>,
    private val rowLayout: Int,
    private val context: Context
) :
    RecyclerView.Adapter<PixabayImageAdapter.PixabayImageViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PixabayImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return PixabayImageViewHolder(
            view,
            mOnItemClickListener!!
        )
    }

    override fun onBindViewHolder(
        holder: PixabayImageViewHolder,
        position: Int
    ) {
        val image_url = imageList[position].webformat_url
        Picasso.with(context).load(image_url).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    interface OnItemClickListener {
        fun onImageClicked(
            v: View?,
            position: Int,
            pixabayImage: PixabayImage?
        )
    }

    fun setmOnItemClickListener(clickListener: OnItemClickListener?) {
        mOnItemClickListener = clickListener
    }

    inner class PixabayImageViewHolder(
        v: View,
        onItemClickListener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(v) {
        var imageLayout: LinearLayout
        var imageView: ImageView

        init {
            imageLayout = v.findViewById(R.id.image_layout)
            imageView = v.findViewById(R.id.pixabay_image)
            imageView.setOnClickListener { v1: View? ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClickListener.onImageClicked(v1, pos, imageList[pos])
                }
            }
        }
    }

}