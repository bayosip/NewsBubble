package com.havas.newsbubble.domain.model.response

import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.NewsCategory

data class CategoryHeadlines(
    val category: NewsCategory,
    val articles: List<UIArticle>,
)
