package com.havas.newsbubble.domain.usecase

import androidx.paging.PagingData
import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.response.Article
import com.havas.newsbubble.domain.model.NewsCategory
import com.havas.newsbubble.domain.repository.NewsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface GetHeadlinesByCategoryUseCase {
     operator fun invoke(category: NewsCategory): Flow<PagingData<UIArticle>>
}

class GetHeadlinesByCategoryUseCaseImpl @Inject constructor(
    private val repository: NewsRepository,
) : GetHeadlinesByCategoryUseCase {
    override fun invoke(category: NewsCategory,): Flow<PagingData<UIArticle>> =
        repository.getPagingTopHeadlines(category)
}
