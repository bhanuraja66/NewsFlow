package com.example.newsflow

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://newsapi.org/"
const val API_KEY = "8ee1cae3e7464caa9b34ab9a8f69a1b3"

interface NewsInterface {

    @GET("v2/everything?apiKey=$API_KEY")
    fun getHeadlines(@Query("page")page : Int, @Query("q")q:String) : Call<News>
}

object NewsService{
    private var newsInstance : NewsInterface? = null
    fun getInstance() : NewsInterface {
        if (newsInstance == null){
            newsInstance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsInterface::class.java)
        }
        return newsInstance!!
    }
}