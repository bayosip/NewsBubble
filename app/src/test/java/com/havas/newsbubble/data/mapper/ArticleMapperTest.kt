package com.havas.newsbubble.data.mapper

import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.response.Article
import com.havas.newsbubble.domain.model.response.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ArticleMapperTest {

    private fun article(
        title: String? = "Headline",
        url: String? = "https://example.com/article",
        description: String? = "A description",
        sourceName: String? = "Reuters",
        urlToImage: String? = "https://example.com/image.jpg",
        publishedAt: String? = "2026-09-05T00:00:00Z",
    ) = Article(
        source = Source(id = "reuters", name = sourceName),
        author = "Jane Doe",
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = "content",
    )

    @Test
    fun `maps a full article to a UI model`() {
        val result = article().toUIModel(page = 2, index = 3)

        assertEquals(203L, result.id)
        assertEquals("Reuters", result.sourceName)
        assertEquals("Headline", result.title)
        assertEquals("A description", result.description)
        assertEquals("https://example.com/article", result.url)
        assertEquals("https://example.com/image.jpg", result.imageUrl)
        assertEquals("2026-09-05T00:00:00Z", result.publishedAt)
    }

    @Test
    fun `derives id from page and index`() {
        val result = article().toUIModel(page = 1, index = 0)
        assertEquals(100L, result.id)
    }

    @Test
    fun `returns a placeholder article when title is null`() {
        val result = article(title = null).toUIModel(page = 1, index = 0)
        assertEquals(UIArticle(), result)
    }

    @Test
    fun `returns a placeholder article when title is blank`() {
        val result = article(title = "   ").toUIModel(page = 1, index = 0)
        assertEquals(UIArticle(), result)
    }

    @Test
    fun `returns a placeholder article when title is the removed marker`() {
        val result = article(title = "[Removed]").toUIModel(page = 1, index = 0)
        assertEquals(UIArticle(), result)
    }

    @Test
    fun `returns a placeholder article when url is null`() {
        val result = article(url = null).toUIModel(page = 1, index = 0)
        assertEquals(UIArticle(), result)
    }

    @Test
    fun `returns a placeholder article when url is blank`() {
        val result = article(url = "   ").toUIModel(page = 1, index = 0)
        assertEquals(UIArticle(), result)
    }

    @Test
    fun `falls back to unknown source when the source name is missing`() {
        val result = article(sourceName = null).toUIModel(page = 1, index = 0)
        assertEquals("Unknown source", result.sourceName)
    }

    @Test
    fun `falls back to unknown source when the source name is blank`() {
        val result = article(sourceName = "   ").toUIModel(page = 1, index = 0)
        assertEquals("Unknown source", result.sourceName)
    }

    @Test
    fun `clears the description when it is blank`() {
        val result = article(description = "   ").toUIModel(page = 1, index = 0)
        assertNull(result.description)
    }

    @Test
    fun `clears the description when it is the removed marker`() {
        val result = article(description = "[Removed]").toUIModel(page = 1, index = 0)
        assertNull(result.description)
    }

    @Test
    fun `defaults publishedAt to an empty string when null`() {
        val result = article(publishedAt = null).toUIModel(page = 1, index = 0)
        assertEquals("", result.publishedAt)
    }
}
