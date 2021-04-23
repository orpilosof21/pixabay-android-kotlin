package com.example.pixabay_android_kotlin.REST

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


const val BASE_URL = "http://pixabay.com/"
const val API_KEY = "12175339-7048b7105116d7fa1da74220c"

class ApiClient {
    companion object{
        private var retrofit: Retrofit?=null
        fun getApiClient(): Retrofit{
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(100, TimeUnit.SECONDS)
                .connectTimeout(100,TimeUnit.SECONDS)
                .build()
            if (retrofit==null){
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit!!
        }
    }
}