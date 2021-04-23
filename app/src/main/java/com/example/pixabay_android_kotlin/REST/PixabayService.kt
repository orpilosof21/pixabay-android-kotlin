package com.example.myapp.rest

import com.example.myapp.model.PixabayResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayService {
    @GET("/api/")
    fun getImages(
        @Query("key") api_key: String?,
        @Query("q") query: String?,
        @Query("image_type") type: String?,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Call<PixabayResponse>
}