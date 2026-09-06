package com.havas.newsbubble.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.havas.newsbubble.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class GenericPagingSource<T : Any>(
    private val pageCallBack: (Int, Int) -> Flow<Resource<List<T>>>,
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        try {
            val nextPage = params.key ?: 1
            var response: List<T>? = emptyList()

            pageCallBack(nextPage, params.loadSize).collect { resource ->
                when (resource.status) {
                    Resource.STATUS.SUCCESS -> {
                        response = resource.data
                    }

                    Resource.STATUS.LOADING -> {
                        // Handle loading
                        params.placeholdersEnabled
                    }

                    Resource.STATUS.ERROR -> {
                        throw IOException(resource.message)
                    }
                }
            }

            return LoadResult.Page(
                data = response ?: emptyList(),
                prevKey = (nextPage - 1).takeIf { nextPage > 1 }, //if (nextPage == 1) null else nextPage - 1,
                nextKey = (nextPage + 1).takeIf { !response.isNullOrEmpty() }//if (response.isNullOrEmpty()) null else nextPage + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}