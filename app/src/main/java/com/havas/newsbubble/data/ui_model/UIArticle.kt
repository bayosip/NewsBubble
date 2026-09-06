package com.havas.newsbubble.data.ui_model

data class UIArticle(
    val id: Long = -1,
    val sourceName: String ="",
    val title: String = "",
    val description: String? = null,
    val url: String = "",
    val imageUrl: String? = null,
    val publishedAt: String = "",
)
