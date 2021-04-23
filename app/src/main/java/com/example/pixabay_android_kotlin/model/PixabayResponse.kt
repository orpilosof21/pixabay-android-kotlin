package com.example.myapp.model

import com.google.gson.annotations.SerializedName

class PixabayResponse(
    @field:SerializedName("total") val total: Int,
    @field:SerializedName("totalHits") val total_hits: Int,
    hits: List<PixabayImage>
) {

    @SerializedName("hits")
    private val hits: List<PixabayImage> = hits

    fun getHits(): List<PixabayImage> {
        return hits
    }

}