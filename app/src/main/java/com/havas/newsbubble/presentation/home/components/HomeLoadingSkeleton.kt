package com.havas.newsbubble.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.havas.newsbubble.presentation.common.components.ArticleBubbleCardSkeleton
import com.havas.newsbubble.presentation.common.components.shimmerEffect

@Composable
fun HomeLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        repeat(3) {
            CategoryRowSkeleton()
        }
    }
}

@Composable
private fun CategoryRowSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .width(140.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect(),
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(2) {
                ArticleBubbleCardSkeleton()
            }
        }
    }
}
