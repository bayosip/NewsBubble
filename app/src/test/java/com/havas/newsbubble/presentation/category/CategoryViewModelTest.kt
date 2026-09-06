package com.havas.newsbubble.presentation.category

import androidx.paging.PagingData
import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.NewsCategory
import com.havas.newsbubble.domain.usecase.GetHeadlinesByCategoryUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    private val useCase = mockk<GetHeadlinesByCategoryUseCase>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loading a category updates state with its paged articles`() = runTest(UnconfinedTestDispatcher()) {
        every { useCase(NewsCategory.TECHNOLOGY) } returns flowOf(PagingData.empty<UIArticle>())
        val viewModel = CategoryViewModel(useCase)

        viewModel.onIntent(CategoryIntent.Load(NewsCategory.TECHNOLOGY))

        assertEquals(NewsCategory.TECHNOLOGY, viewModel.state.value.category)
        assertNotNull(viewModel.state.value.articles)
        verify(exactly = 1) { useCase(NewsCategory.TECHNOLOGY) }
    }

    @Test
    fun `loading the same category twice only fetches once`() = runTest(UnconfinedTestDispatcher()) {
        every { useCase(NewsCategory.TECHNOLOGY) } returns flowOf(PagingData.empty<UIArticle>())
        val viewModel = CategoryViewModel(useCase)

        viewModel.onIntent(CategoryIntent.Load(NewsCategory.TECHNOLOGY))
        viewModel.onIntent(CategoryIntent.Load(NewsCategory.TECHNOLOGY))

        verify(exactly = 1) { useCase(NewsCategory.TECHNOLOGY) }
    }

    @Test
    fun `switching category triggers a new load`() = runTest(UnconfinedTestDispatcher()) {
        every { useCase(NewsCategory.TECHNOLOGY) } returns flowOf(PagingData.empty<UIArticle>())
        every { useCase(NewsCategory.SPORTS) } returns flowOf(PagingData.empty<UIArticle>())
        val viewModel = CategoryViewModel(useCase)

        viewModel.onIntent(CategoryIntent.Load(NewsCategory.TECHNOLOGY))
        viewModel.onIntent(CategoryIntent.Load(NewsCategory.SPORTS))

        assertEquals(NewsCategory.SPORTS, viewModel.state.value.category)
        verify(exactly = 1) { useCase(NewsCategory.TECHNOLOGY) }
        verify(exactly = 1) { useCase(NewsCategory.SPORTS) }
    }

    @Test
    fun `clicking an article emits an open article effect`() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = CategoryViewModel(useCase)
        val effects = mutableListOf<CategoryEffect>()
        val job = launch { viewModel.effect.toList(effects) }

        viewModel.onIntent(CategoryIntent.OnArticleClick("https://example.com/article"))

        job.cancel()
        assertEquals(listOf(CategoryEffect.OpenArticle("https://example.com/article")), effects)
    }
}
