package com.havas.newsbubble.domain.repository

import androidx.paging.PagingData
import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.NewsCategory
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getPagingTopHeadlines(category: NewsCategory): Flow<PagingData<UIArticle>>
}
