package com.havas.newsbubble.data.mapper

import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.response.Article

private const val REMOVED_MARKER = "[Removed]"

fun Article.toUIModel(page: Int, index: Int): UIArticle {
    val safeTitle = title?.takeIf { it.isNotBlank() && it != REMOVED_MARKER } ?: return UIArticle()
    val safeUrl = url?.takeIf { it.isNotBlank() } ?: return UIArticle()

    return UIArticle(
        id = (page * 100) + index.toLong(),
        sourceName = source?.name?.takeIf { it.isNotBlank() } ?: "Unknown source",
        title = safeTitle,
        description = description?.takeIf { it.isNotBlank() && it != REMOVED_MARKER },
        url = safeUrl,
        imageUrl = urlToImage,
        publishedAt = publishedAt.orEmpty(),
    )
}
