package com.havas.newsbubble.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.havas.newsbubble.presentation.category.CategoryScreen
import com.havas.newsbubble.presentation.home.HomeScreen
import com.havas.newsbubble.presentation.webview.ArticleWebView as ArticleWebViewScreen

@Composable
fun NewsNavGraph(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Home)

    fun openArticle(url: String) {
        backStack.add(ArticleWebView(url))
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(
                    onNavigateToCategory = { category -> backStack.add(CategoryDetail(category)) }
                )
            }
            entry<CategoryDetail> { route ->
                CategoryScreen(
                    category = route.category,
                    onOpenArticle = ::openArticle,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<ArticleWebView> { route ->
                ArticleWebViewScreen(
                    url = route.url,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
