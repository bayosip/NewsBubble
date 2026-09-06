package com.havas.newsbubble.domain.model

import androidx.annotation.DrawableRes
import com.havas.newsbubble.R

enum class NewsCategory(
    val apiValue: String,
    val displayName: String, @DrawableRes val iconRes: Int
) {
    BUSINESS("business", "Business", R.drawable.ic_business),
    ENTERTAINMENT("entertainment", "Entertainment", R.drawable.ic_entertainment),
    GENERAL("general", "Top Stories", R.drawable.ic_general),
    HEALTH("health", "Health", R.drawable.ic_health),
    SCIENCE("science", "Science", R.drawable.ic_science),
    SPORTS("sports", "Sports", R.drawable.ic_sports),
    TECHNOLOGY("technology", "Technology", R.drawable.ic_technology),
}
