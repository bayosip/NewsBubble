package com.havas.newsbubble.presentation.category

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.havas.newsbubble.domain.model.NewsCategory
import com.havas.newsbubble.presentation.category.components.CategoryLoadingSkeleton
import com.havas.newsbubble.presentation.common.components.ArticleRowItem
import com.havas.newsbubble.presentation.common.components.EmptyState

@Composable
fun CategoryScreen(
    category: NewsCategory,
    onOpenArticle: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(category) {
        viewModel.onIntent(CategoryIntent.Load(category))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CategoryEffect.OpenArticle -> onOpenArticle(effect.url)
            }
        }
    }

    CategoryContent(
        title = category.displayName,
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryContent(
    title: String,
    state: CategoryState,
    onIntent: (CategoryIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val data = state.articles?.collectAsLazyPagingItems()
    val refreshLoadState = data?.loadState?.refresh
    val appendLoadState = data?.loadState?.append
    val prependLoadState = data?.loadState?.prepend
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {

            var isFirstLoadComplete by remember { mutableStateOf(false) }
            val listState = rememberLazyListState()
            LaunchedEffect(refreshLoadState, data?.itemCount) {
                if (refreshLoadState is LoadState.NotLoading && data.itemCount > 0) {
                    isFirstLoadComplete = true
                    listState.scrollToItem(0)
                }
            }

            val showInitialLoadingScreen =
                (!isFirstLoadComplete && refreshLoadState is LoadState.Loading) ||
                        (refreshLoadState is LoadState.Loading)

            val showPullRefreshIndicator =
                remember(refreshLoadState, appendLoadState, isFirstLoadComplete) {
                    refreshLoadState is LoadState.Loading && isFirstLoadComplete
                }


            val pullRefreshState = rememberPullToRefreshState()

            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
                isRefreshing = !showInitialLoadingScreen && (data?.itemCount ?: 0) > 0 ||
                        refreshLoadState is LoadState.Error ||
                        appendLoadState is LoadState.Error ||
                        prependLoadState is LoadState.Error,
                onRefresh = {
                    data?.refresh()
                },
                indicator = {
                    Column (horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedVisibility(
                            visible = showPullRefreshIndicator,
                        ) {
                            Indicator(
                                isRefreshing = showPullRefreshIndicator,
                                state = pullRefreshState,
                            )
                        }
                    }
                }
            ) {
                when {
                    showInitialLoadingScreen -> {
                        CategoryLoadingSkeleton(modifier = Modifier.fillMaxSize())
                    }

                    refreshLoadState is LoadState.Error && data.itemCount == 0 -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Error loading headlines",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                            )
                            TextButton(onClick = { data.retry() }) {
                                Text("Retry")
                            }
                        }
                    }

                    data?.itemCount == 0 && refreshLoadState is LoadState.NotLoading -> {

                        EmptyState(
                            message = "No headlines found for this category yet.",
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    else -> {
                        data?.let {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                state = listState,
                            ) {
                                val appendLoadState = data.loadState.append
                                if (appendLoadState is LoadState.Loading) {
                                    item {
                                        LoadingMoreItemsIndicator()
                                    }
                                } else if (appendLoadState is LoadState.Error) {
                                    item {
                                        ErrorLoadingMoreItems(
                                            "Failed to load more headlines"
                                        ) { data.retry() }
                                    }
                                }
                                items(
                                    count = data.itemCount,
                                    key = data.itemKey { item -> item.id }
                                ) { index ->
                                    val article = data[index]
                                    article?.let {
                                        ArticleRowItem(
                                            article = it,
                                            onClick = {
                                                onIntent(
                                                    CategoryIntent.OnArticleClick(
                                                        article.url
                                                    )
                                                )
                                            },
                                        )
                                    }

                                }

                                val prependLoadState = data.loadState.prepend
                                if (prependLoadState is LoadState.Loading) {
                                    item { LoadingMoreItemsIndicator() }
                                } else if (prependLoadState is LoadState.Error) {
                                    item {
                                        ErrorLoadingMoreItems(
                                            prependLoadState.error.message ?: "Unknown error"
                                        ) { data.retry() }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}


@Composable
fun ErrorLoadingMoreItems(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 8.dp)
                .clickable(onClick = onRetry)
        )
    }
}

@Composable
fun LoadingMoreItemsIndicator(){
    CircularProgressIndicator(
        modifier = Modifier
            .size(30.dp)
            .background(color = Color.Gray, shape = CircleShape)
            .padding(4.dp),
        color = White,
        trackColor = Color.LightGray
    )
}