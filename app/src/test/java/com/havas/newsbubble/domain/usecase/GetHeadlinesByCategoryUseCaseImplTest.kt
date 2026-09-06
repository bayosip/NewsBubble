package com.havas.newsbubble.domain.usecase

import androidx.paging.PagingData
import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.NewsCategory
import com.havas.newsbubble.domain.repository.NewsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class GetHeadlinesByCategoryUseCaseImplTest {

    private val repository = mockk<NewsRepository>()
    private val useCase = GetHeadlinesByCategoryUseCaseImpl(repository)

    @Test
    fun `delegates to the repository for the requested category`() {
        val expected: Flow<PagingData<UIArticle>> = flowOf(PagingData.empty())
        every { repository.getPagingTopHeadlines(NewsCategory.TECHNOLOGY) } returns expected

        val result = useCase(NewsCategory.TECHNOLOGY)

        assertSame(expected, result)
        verify(exactly = 1) { repository.getPagingTopHeadlines(NewsCategory.TECHNOLOGY) }
    }

    @Test
    fun `does not call the repository for a category it was not asked for`() {
        every { repository.getPagingTopHeadlines(any()) } returns flowOf(PagingData.empty())

        useCase(NewsCategory.SPORTS)

        verify(exactly = 0) { repository.getPagingTopHeadlines(NewsCategory.BUSINESS) }
    }
}
