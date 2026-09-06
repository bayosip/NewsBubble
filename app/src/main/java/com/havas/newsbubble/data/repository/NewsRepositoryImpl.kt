package com.havas.newsbubble.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.havas.newsbubble.data.mapper.toUIModel
import com.havas.newsbubble.domain.model.response.Article
import com.havas.newsbubble.data.remote.NewsApiService
import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.NewsCategory
import com.havas.newsbubble.domain.repository.NewsRepository
import com.havas.newsbubble.domain.util.BaseDataSource
import com.havas.newsbubble.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.Locale

internal const val LIST_LIMIT = 10

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApiService,
) : NewsRepository, BaseDataSource() {

    internal fun getTopHeadlines(
        category: NewsCategory,
        countryCode: String = "us",
        page: Int
    ): Flow<Resource<List<Article>>> = flow {
        emit(Resource.loading())
        val response = getResult {
            api.getTopHeadlines(category = category.name.lowercase(), country = countryCode, page = page, pageSize = LIST_LIMIT)
        }
        when (response.status) {
            Resource.STATUS.SUCCESS -> {
                emit(Resource.success(response.data?.articles?.mapNotNull { it } ?: emptyList()))
            }

            Resource.STATUS.ERROR -> {
                emit(Resource.error(response.message))
            }

            Resource.STATUS.LOADING -> {}
        }
    }

    override fun getPagingTopHeadlines(category: NewsCategory): Flow<PagingData<UIArticle>> {
        var apiPage = 1
        var itemIndex = -1
        return Pager(
            PagingConfig(
                pageSize = LIST_LIMIT,
                prefetchDistance = LIST_LIMIT - 1,
                initialLoadSize = LIST_LIMIT
            )
        ) {
            GenericPagingSource(
                pageCallBack = { page, _ ->
                    apiPage = page
                    getTopHeadlines(
                        category = category,
                        page = page
                    )
                }
            )
        }.flow.map { pagingData ->
            pagingData.map { article ->
                article.toUIModel(page = apiPage, index = ++itemIndex)
            }
        }.flowOn(Dispatchers.IO)
    }
}
