package com.havas.newsbubble.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.havas.newsbubble.domain.model.NewsCategory
import com.havas.newsbubble.domain.usecase.GetHeadlinesByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getHeadlinesByCategory: GetHeadlinesByCategoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CategoryEffect>()
    val effect: SharedFlow<CategoryEffect> = _effect

    fun onIntent(intent: CategoryIntent) {
        when (intent) {
            is CategoryIntent.Load -> loadIfNeeded(intent.category)
            is CategoryIntent.OnArticleClick -> viewModelScope.launch {
                _effect.emit(CategoryEffect.OpenArticle(intent.url))
            }
        }
    }

    private fun loadIfNeeded(category: NewsCategory) {
        if (_state.value.category == category) return
        _state.update { it.copy(category = category) }
        load(category)
    }

    private fun load(category: NewsCategory) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    articles = getHeadlinesByCategory(category).cachedIn(viewModelScope)
                )
            }
        }
    }
}
