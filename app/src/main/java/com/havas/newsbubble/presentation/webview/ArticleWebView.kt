package com.havas.newsbubble.presentation.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Composable, reusable in-app browser. Give it any [url] (e.g. an article's
 * link from [com.havas.newsbubble.presentation.common.components.ArticleRowItem])
 * and it renders it in a WebView with its own loading/error state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleWebView(
    url: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pageTitle by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var hasError by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pageTitle ?: url,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (hasError) {
                Text(
                    text = "Couldn't load this article.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                )
            } else {
                WebViewContent(
                    url = url,
                    onPageTitleChanged = { pageTitle = it },
                    onLoadingChanged = { isLoading = it },
                    onError = { hasError = true },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (isLoading && !hasError) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewContent(
    url: String,
    onPageTitleChanged: (String?) -> Unit,
    onLoadingChanged: (Boolean) -> Unit,
    onError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        onLoadingChanged(true)
                    }

                    override fun onPageFinished(view: WebView, url: String?) {
                        onLoadingChanged(false)
                        onPageTitleChanged(view.title)
                    }

                    override fun onReceivedError(
                        view: WebView,
                        errorCode: Int,
                        description: String?,
                        failingUrl: String?,
                    ) {
                        onLoadingChanged(false)
                        onError()
                    }
                }
                loadUrl(url)
            }
        },
    )
}
