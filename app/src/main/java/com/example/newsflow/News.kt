package com.example.newsflow

data class News(
    val totalResults: Int,
    val articles: List<Article>
)