package com.havas.newsbubble.presentation.home

import com.havas.newsbubble.domain.model.response.CategoryHeadlines
import com.havas.newsbubble.domain.model.NewsCategory

data class HomeState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val sections: List<CategoryHeadlines> = emptyList(),
    val errorMessage: String? = null,
)

sealed interface HomeIntent {
    data object LoadHeadlines : HomeIntent
    data object Refresh : HomeIntent
    data class OnArticleClick(val url: String) : HomeIntent
    data class OnSeeAllClick(val category: NewsCategory) : HomeIntent
}

sealed interface HomeEffect {
    data class OpenArticle(val url: String) : HomeEffect
    data class NavigateToCategory(val category: NewsCategory) : HomeEffect
}
