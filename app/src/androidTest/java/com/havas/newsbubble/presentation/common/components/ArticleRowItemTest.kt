package com.havas.newsbubble.presentation.common.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.ui.theme.NewsBubbleTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleRowItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val article = UIArticle(
        title = "Composable webviews land in NewsBubble",
        description = "A quick look at the new in-app browser.",
        sourceName = "NewsBubble",
        url = "https://example.com/article",
    )

    @Test
    fun tappingTheRowInvokesOnClickWithNoArguments() {
        var clickCount = 0

        composeTestRule.setContent {
            NewsBubbleTheme {
                ArticleRowItem(article = article, onClick = { clickCount++ })
            }
        }

        composeTestRule.onNodeWithText(article.title).performClick()

        assert(clickCount == 1) { "Expected exactly one click, got $clickCount" }
    }

    @Test
    fun rendersTheArticlesTitleDescriptionAndSource() {
        composeTestRule.setContent {
            NewsBubbleTheme {
                ArticleRowItem(article = article, onClick = {})
            }
        }

        composeTestRule.onNodeWithText(article.title).assertExists()
        composeTestRule.onNodeWithText(article.description!!).assertExists()
        composeTestRule.onNodeWithText(article.sourceName).assertExists()
    }
}
