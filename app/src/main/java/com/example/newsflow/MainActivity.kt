package com.example.newsflow

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NewsAdapter
    private lateinit var recyclerView: RecyclerView
    private var articlesList = mutableListOf<Article>()
    private var mInterstitialAd: InterstitialAd? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = NewsAdapter(articlesList,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        MobileAds.initialize(this)
        scheduleNextAd()
        getNews()
    }

    private fun scheduleNextAd() {
        val delayMillis = if(Random.nextBoolean()) 10 * 1000L else 30 * 1000L
        handler.postDelayed({loadAndShowAd()}, delayMillis)
    }

    private fun loadAndShowAd(){
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError.toString().let { Log.d("Load Failed", it) }
                mInterstitialAd = null
                scheduleNextAd()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("Load Success", "Ad was loaded")
                mInterstitialAd = interstitialAd
                mInterstitialAd?.show(this@MainActivity)
                scheduleNextAd()
            }
        })
    }

    private fun getNews() {
        val news:Call<News> = NewsService.getInstance().getHeadlines(1,"india" )
        news.enqueue(object : Callback<News>{
            override fun onResponse(p0: Call<News>, p1: Response<News>) {
                val news: News? = p1.body()
                if(news!=null){
                    Log.d("Success", news.toString())
                    articlesList.addAll(news.articles)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(p0: Call<News>, p1: Throwable) {
                Log.d("Failure","Error in fetching news",p1)
            }

        })
    }
}