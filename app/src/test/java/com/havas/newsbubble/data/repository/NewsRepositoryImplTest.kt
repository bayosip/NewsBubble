package com.havas.newsbubble.data.repository

import com.havas.newsbubble.data.remote.NewsApiService
import com.havas.newsbubble.domain.model.NewsCategory
import com.havas.newsbubble.domain.model.response.Article
import com.havas.newsbubble.domain.model.response.HeadlineData
import com.havas.newsbubble.domain.model.response.Source
import com.havas.newsbubble.domain.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import retrofit2.Response

class NewsRepositoryImplTest {

    private val api = mockk<NewsApiService>()
    private val repository = NewsRepositoryImpl(api)

    private fun article(title: String = "Headline") = Article(
        source = Source(id = "src", name = "Source"),
        author = null,
        title = title,
        description = "desc",
        url = "https://example.com",
        urlToImage = null,
        publishedAt = "2026-09-05T00:00:00Z",
        content = null,
    )

    @Test
    fun `emits loading then success with mapped articles`() = runTest {
        coEvery {
            api.getTopHeadlines(category = "technology", country = "us", pageSize = LIST_LIMIT, page = 1)
        } returns Response.success(HeadlineData(status = "ok", totalResults = 1, articles = listOf(article())))

        val emissions = repository.getTopHeadlines(NewsCategory.TECHNOLOGY, page = 1).toList()

        assertEquals(2, emissions.size)
        assertEquals(Resource.STATUS.LOADING, emissions[0].status)
        assertEquals(Resource.STATUS.SUCCESS, emissions[1].status)
        assertEquals(1, emissions[1].data?.size)
        assertEquals("Headline", emissions[1].data?.first()?.title)
    }

    @Test
    fun `drops null articles from the response`() = runTest {
        @Suppress("UNCHECKED_CAST")
        val articlesWithNull = listOf(article(), null) as List<Article>
        coEvery {
            api.getTopHeadlines(category = "general", country = "us", pageSize = LIST_LIMIT, page = 1)
        } returns Response.success(HeadlineData(status = "ok", totalResults = 2, articles = articlesWithNull))

        val emissions = repository.getTopHeadlines(NewsCategory.GENERAL, page = 1).toList()

        assertEquals(1, emissions.last().data?.size)
    }

    @Test
    fun `emits error when the response is unsuccessful`() = runTest {
        coEvery {
            api.getTopHeadlines(category = "business", country = "us", pageSize = LIST_LIMIT, page = 1)
        } returns Response.error(500, "server error".toResponseBody("text/plain".toMediaType()))

        val emissions = repository.getTopHeadlines(NewsCategory.BUSINESS, page = 1).toList()

        assertEquals(Resource.STATUS.LOADING, emissions[0].status)
        assertEquals(Resource.STATUS.ERROR, emissions[1].status)
        assertNull(emissions[1].data)
    }

    @Test
    fun `emits error when the api call throws`() = runTest {
        coEvery {
            api.getTopHeadlines(category = "sports", country = "us", pageSize = LIST_LIMIT, page = 1)
        } throws IOException("no network")

        val emissions = repository.getTopHeadlines(NewsCategory.SPORTS, page = 1).toList()

        assertEquals(Resource.STATUS.LOADING, emissions[0].status)
        assertEquals(Resource.STATUS.ERROR, emissions[1].status)
        assertEquals("no network", emissions[1].message)
    }
}
