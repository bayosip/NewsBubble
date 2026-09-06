package com.havas.newsbubble

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.havas.newsbubble.presentation.navigation.NewsNavGraph
import com.havas.newsbubble.ui.theme.NewsBubbleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsBubbleTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NewsNavGraph()
                }
            }
        }
    }
}
