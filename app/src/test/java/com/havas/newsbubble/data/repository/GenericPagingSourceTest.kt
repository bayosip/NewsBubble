package com.havas.newsbubble.data.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.havas.newsbubble.domain.util.Resource
import java.io.IOException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GenericPagingSourceTest {

    @Test
    fun `first page returns data with only a next key`() = runTest {
        val source = GenericPagingSource<String> { page, _ ->
            flow {
                emit(Resource.loading())
                emit(Resource.success(listOf("item-$page-1", "item-$page-2")))
            }
        }

        val result = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 2, placeholdersEnabled = false),
        )

        check(result is PagingSource.LoadResult.Page)
        assertEquals(listOf("item-1-1", "item-1-2"), result.data)
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `a page beyond the first has a prev key`() = runTest {
        val source = GenericPagingSource<String> { page, _ ->
            flow { emit(Resource.success(listOf("item-$page"))) }
        }

        val result = source.load(
            PagingSource.LoadParams.Append(key = 3, loadSize = 1, placeholdersEnabled = false),
        )

        check(result is PagingSource.LoadResult.Page)
        assertEquals(2, result.prevKey)
        assertEquals(4, result.nextKey)
    }

    @Test
    fun `an empty page has no next key`() = runTest {
        val source = GenericPagingSource<String> { _, _ ->
            flow { emit(Resource.success(emptyList())) }
        }

        val result = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        )

        check(result is PagingSource.LoadResult.Page)
        assertNull(result.nextKey)
    }

    @Test
    fun `an error resource surfaces as a load error`() = runTest {
        val source = GenericPagingSource<String> { _, _ ->
            flow { emit(Resource.error("boom")) }
        }

        val result = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        )

        check(result is PagingSource.LoadResult.Error)
        assertTrue(result.throwable is IOException)
        assertEquals("boom", result.throwable.message)
    }

    @Test
    fun `refresh key falls back to the next page when there is no previous page`() {
        val source = GenericPagingSource<String> { _, _ -> flow { } }
        val page = PagingSource.LoadResult.Page(
            data = listOf("a"),
            prevKey = null,
            nextKey = 2,
        )
        val state = PagingState(
            pages = listOf(page),
            anchorPosition = 0,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0,
        )

        assertEquals(1, source.getRefreshKey(state))
    }
}
