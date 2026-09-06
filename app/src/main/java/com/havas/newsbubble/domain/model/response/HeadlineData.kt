package com.havas.newsbubble.domain.model.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HeadlineData(
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("articles")
    val articles: List<Article>?
)