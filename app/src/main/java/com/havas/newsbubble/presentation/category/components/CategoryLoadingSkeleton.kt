package com.havas.newsbubble.presentation.category.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.havas.newsbubble.presentation.common.components.ArticleRowItemSkeleton

@Composable
fun CategoryLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        repeat(6) {
            ArticleRowItemSkeleton()
        }
    }
}
