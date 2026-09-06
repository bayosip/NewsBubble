package com.havas.newsbubble.data.remote

import com.havas.newsbubble.domain.model.response.HeadlineData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String,
        @Query("country") country: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
    ): Response<HeadlineData?>

    companion object {
        const val BASE_URL = "https://newsapi.org/"
    }
}
