package com.havas.newsbubble.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.havas.newsbubble.domain.model.NewsCategory
import kotlinx.serialization.Serializable

sealed interface Route : NavKey

@Serializable
data object Home : Route

@Serializable
data class CategoryDetail(val category: NewsCategory) : Route

@Serializable
data class ArticleWebView(val url: String) : Route
