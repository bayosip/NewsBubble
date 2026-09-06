package com.havas.newsbubble.presentation.category

import androidx.paging.PagingData
import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.NewsCategory
import kotlinx.coroutines.flow.Flow

data class CategoryState(
    val category: NewsCategory? = null,
    val isLoading: Boolean = true,
    val articles: Flow<PagingData<UIArticle>>? = null,
)

sealed interface CategoryIntent {
    data class Load(val category: NewsCategory) : CategoryIntent
    data class OnArticleClick(val url: String) : CategoryIntent
}

sealed interface CategoryEffect {
    data class OpenArticle(val url: String) : CategoryEffect
}
