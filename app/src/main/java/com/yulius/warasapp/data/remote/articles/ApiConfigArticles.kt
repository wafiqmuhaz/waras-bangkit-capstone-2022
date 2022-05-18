package com.yulius.warasapp.data.remote.articles

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfigArticles {
    companion object{
        fun getApiService(): ApiServiceArticles {
            val client = OkHttpClient.Builder()
                .addInterceptor{
                    val original = it.request()
                    val requestBuilder = original.newBuilder()
                    val request = requestBuilder.build()
                    it.proceed(request)
                }
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiServiceArticles::class.java)
        }
    }
}