package com.example.myapp.model

import com.google.gson.annotations.SerializedName

class PixabayImage(
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("pageURL") val page_url: String,
    @field:SerializedName("type") val type: String,
    @field:SerializedName("tags") val tags: String,
    @field:SerializedName("previewURL") val preview_url: String,
    @field:SerializedName("previewWidth") val preview_width: Int,
    @field:SerializedName("previewHeight") val preview_height: Int,
    @field:SerializedName("webformatURL") val webformat_url: String,
    @field:SerializedName("webformatWidth") val webformat_width: Int,
    @field:SerializedName("webformatHeight") val webformat_height: Int,
    @field:SerializedName("largeImageURL") val image_url: String,
    @field:SerializedName("imageWidth") val image_width: Int,
    @field:SerializedName("imageHeight") val image_height: Int,
    @field:SerializedName("imageSize") val image_size: Int,
    @field:SerializedName("views") val views: Int,
    @field:SerializedName("downloads") val downloads: Int,
    @field:SerializedName("favorites") val favorites: Int,
    @field:SerializedName("likes") val likes: Int,
    @field:SerializedName("comments") val comments: Int,
    @field:SerializedName("user_id") val user_id: Int,
    @field:SerializedName("user") val user: String,
    @field:SerializedName("userImageURL") val user_image_url: String
)