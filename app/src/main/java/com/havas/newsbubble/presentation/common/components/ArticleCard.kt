package com.havas.newsbubble.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.response.Article
import com.havas.newsbubble.ui.theme.NewsBubbleTheme

@Composable
fun ArticleBubbleCard(
    article: UIArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(120.dp),
            )
        }
        Text(
            text = article.sourceName,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
fun ArticleRowItem(
    article: UIArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (!article.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp)),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!article.description.isNullOrBlank()) {
                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Text(
                text = article.sourceName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 6.dp)
                    .clickable(onClick = onClick),
            )
        }
    }
}

@Composable
fun ArticleBubbleCardSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.width(220.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect(),
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(80.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect(),
        )
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect(),
        )
    }
}

@Composable
fun ArticleRowItemSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect(),
        )
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(80.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleBubbleCardPreview() {
    NewsBubbleTheme {
        ArticleBubbleCard(
            article = UIArticle(
                title = "Sample headline that wraps across a couple of lines",
                description = null,
                imageUrl = null,
                sourceName = "Example News",
                publishedAt = "",
            ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleRowItemPreview() {
    NewsBubbleTheme {
        Column {
            ArticleRowItem(
                article = UIArticle(
                    title = "Markets rally as inflation data cools for third month",
                    description = "Investors welcomed the latest report, pushing major indexes to new highs.",
                    imageUrl = "https://example.com/image.jpg",
                    sourceName = "Reuters",
                ),
                onClick = {},
            )
            ArticleRowItem(
                article = UIArticle(
                    title = "Central bank holds rates steady, signals cautious outlook ahead",
                    description = "Officials cited steady employment and easing prices.",
                    imageUrl = null,
                    sourceName = "Bloomberg",
                ),
                onClick = {},
            )
        }
    }
}
