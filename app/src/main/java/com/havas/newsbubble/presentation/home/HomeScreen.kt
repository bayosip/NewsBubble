package com.havas.newsbubble.presentation.home

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.havas.newsbubble.R
import com.havas.newsbubble.data.ui_model.UIArticle
import com.havas.newsbubble.domain.model.NewsCategory
import com.havas.newsbubble.domain.model.response.CategoryHeadlines
import com.havas.newsbubble.ui.theme.NewsBubbleTheme
import kotlin.math.hypot
import kotlin.math.max
import kotlin.random.Random

data class Ball18(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val r: Float,
    val colorIndex: Int,
)

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    onNavigateToCategory: (NewsCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeContent(modifier = modifier, onCategoryClick = onNavigateToCategory)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    onCategoryClick: (NewsCategory) -> Unit = {},
) {
    val ballRadiusMaxDp = 62f // big balls, variation matters
    val ballCount = NewsCategory.entries.size
    val maxSpeedDpPerSec = 130f // slow lava lamp ↔ frantic
    val bgColor = Color(0xFF080812) // dark shows glow best
    val glowRadiusMult = 2.5f // 1.2 (crisp) ↔ 4.0 (heavy fusion)
    val palette = listOf(
        Color(0xFF0288D1),
        Color(0xFFEC407A),
        Color(0xFF90CAF9),
        Color(0xFFEF5350),
        Color(0xFFAB47BC),
        Color(0xFFFF7043),
        Color(0xFF00695C),
    )

    val gradientMidStop = 0.2f // 0.2 (sharp) ↔ 0.8 (very soft halo)
    val gradientMidAlpha = 0.50f // alpha at the mid stop
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Welcome to NewsBubble", color = Color.Blue,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Blue,
                )
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {

            Text(
                text = "Select a category by tapping on it",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp),
            )
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            ) {
                val density = LocalDensity.current
                val widthPx = with(density) { maxWidth.toPx()  }
                val heightPx = with(density) { maxHeight.toPx()  }
                val rMaxPx = with(density) { ballRadiusMaxDp.dp.toPx()  }
                val maxSpeedPx = with(density) { maxSpeedDpPerSec.dp.toPx() }

                val balls = remember(widthPx, heightPx, NewsCategory.entries, rMaxPx) {
                    val rng = Random(0xB10B5L)
                    List(NewsCategory.entries.size) { index ->
                        Ball18(
                            x = rng.nextFloat() * max(1f, widthPx - 2f * rMaxPx) + rMaxPx,
                            y = rng.nextFloat() * max(1f, heightPx - 2f * rMaxPx) + rMaxPx,
                            vx = (rng.nextFloat() * 2f - 1f) * maxSpeedPx * 0.6f,
                            vy = (rng.nextFloat() * 2f - 1f) * maxSpeedPx * 0.6f,
                            r = rMaxPx,
                            colorIndex = index,
                        )
                    }
                }

                var tick by remember { mutableLongStateOf(0L) }

                LaunchedEffect(ballCount, ballRadiusMaxDp, maxSpeedPx) {
                    var lastNanos = 0L
                    val rng = Random(0xF1A5C0L)
                    while (true) {
                        withFrameNanos { now ->
                            val dt = if (lastNanos ==
                                0L
                            ) {
                                0f
                            } else {
                                ((now - lastNanos) / 1_000_000_000f).coerceAtMost(0.05f)
                            }
                            lastNanos = now
                            if (dt > 0f) {
                                for (b in balls) {
                                    b.vx += (rng.nextFloat() * 1f) * dt
                                    b.vy += (rng.nextFloat() * 1f) * dt
                                    val speed = hypot(b.vx, b.vy)
                                    if (speed >= maxSpeedPx) {
                                        val s = maxSpeedPx / speed
                                        b.vx *= s
                                        b.vy *= s
                                    }
                                    b.x += (b.vx * dt)
                                    b.y += (b.vy * dt)
                                    if ((b.x - b.r) < 0f) {
                                        b.x = b.r
                                        Log.d(TAG, "HomeContent: Ball hit left wall")
                                        b.vx = -b.vx
                                    }
                                    if (b.x + b.r >= widthPx) {
                                        b.x = widthPx  - b.r
                                        b.vx = -b.vx
                                    }
                                    if (b.y - b.r <= 0f) {
                                        b.y = b.r
                                        Log.d(TAG, "HomeContent: Ball hit top wall")
                                        b.vy = -b.vy
                                    }
                                    if (b.y + b.r >= heightPx) {
                                        b.y = heightPx - b.r
                                        b.vy = -b.vy
                                    }
                                }
                                tick++
                            }
                        }
                    }
                }

                ClickableBalls(
                    balls = balls,
                    palette = palette,
                    glowRadiusMult = glowRadiusMult,
                    gradientMidStop = gradientMidStop,
                    gradientMidAlpha = gradientMidAlpha,
                    tick = tick,
                ) {
                    onCategoryClick(NewsCategory.entries[it])
                }
//                Canvas(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                ) {
//                    val touch = tick
//                    drawRect(color = bgColor, size = size)
//                    for (b in balls) {
//                        val glowRadius = b.r * glowRadiusMult
//                        val center = Offset(b.x, b.y)
//                        val core = palette[b.colorIndex % palette.size]
//                        val brush = Brush.radialGradient(
//                            colorStops = arrayOf(
//                                0f to core,
//                                gradientMidStop to core.copy(alpha = gradientMidAlpha),
//                                1f to Color.Transparent,
//                            ),
//                            center = center,
//                            radius = glowRadius,
//                        )
//                        drawCircle(
//                            brush = brush,
//                            radius = glowRadius,
//                            center = center,
//                            blendMode = BlendMode.Plus,
//                        )
//                    }
//                    for (b in balls) {
//                        val coreRadius = b.r * .9f
//                        val ballColor = palette[b.colorIndex % palette.size]
//                        drawCircle(
//                            color = ballColor.copy(alpha = 0.95f),
//                            radius = max(1f, coreRadius),
//                            center = Offset(b.x, b.y),
//                            blendMode = BlendMode.Plus,
//                        )
//                        drawCircle(
//                            color = Color.White.copy(alpha = 0.55f),
//                            radius = max(1f, coreRadius * 0.45f),
//                            center = Offset(
//                                b.x - coreRadius * 0.25f,
//                                b.y - coreRadius * 0.25f,
//                            ),
//                            blendMode = BlendMode.Plus,
//                        )
//                    }
//                }
            }
        }
    }
}

@Composable
fun ClickableBalls(
    balls: List<Ball18>,
    palette: List<Color>,
    glowRadiusMult: Float = 1.2f,
    gradientMidStop: Float = 0.2f,
    gradientMidAlpha: Float = 0.50f,
    tick: Long,
    onCategoryClick: (Int) -> Unit = {},
) {

    val density = LocalDensity.current
    balls.forEachIndexed { index, b ->

        val touch = tick
        val glowRadius = b.r * glowRadiusMult
        val center = Offset(b.x, b.y)
        val core = palette[b.colorIndex % palette.size]
        val brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to core,
                gradientMidStop to core.copy(alpha = gradientMidAlpha),
                1f to Color.Transparent,
            ),
            center = center,
            radius = glowRadius,
        )
        val offsetXDp: Dp
        val offsetYDp: Dp
        val radius: Dp

        with(density) {
            offsetXDp = (b.x - glowRadius / 2).toDp()
            offsetYDp = (b.y - glowRadius / 2).toDp()
            radius = glowRadius.toDp()
        }

        Canvas(
            modifier = Modifier
                .size(radius)
                .clickable(onClick = {
                    onCategoryClick(b.colorIndex)
                    Log.d(TAG, "BouncingBalls: $index")
                }),
        ) {

            val coreRadius = b.r * 1.2f
            val ballColor = palette[b.colorIndex % palette.size]

            drawCircle(
                color = ballColor.copy(alpha = 0.95f),
                radius = coreRadius,
                center = Offset(b.x, b.y),
                blendMode = BlendMode.Plus,
            )
        }
        Box(
            modifier = Modifier
                .size(radius)
                .offset(offsetXDp, offsetYDp)
                .border(
                    width = 1.dp,
                    color = core,
                    shape = CircleShape
                )
                .background(
                    brush = brush,
                    shape = CircleShape,
                    alpha = 1f
                )
                .clickable(onClick = {
                    onCategoryClick(b.colorIndex)
                })
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .padding(16.dp)
            ){
                Image(
                    painter = painterResource(NewsCategory.entries[b.colorIndex].iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(8.dp)
                )
                Text(
                    text = NewsCategory.entries[b.colorIndex].name,
                    modifier = Modifier,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium

                )
            }

        }
    }
}

@Composable
fun BouncingBalls(
    balls: List<Ball18>,
    palette: List<Color>,
    glowRadiusMult: Float = 1.2f,
    gradientMidStop: Float = 0.2f,
    gradientMidAlpha: Float = 0.50f,
    tick: Long,
    onCategoryClick: (Int) -> Unit = {},
) {
    balls.forEachIndexed { index, b ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = {
                    onCategoryClick(b.colorIndex)
                    Log.d(TAG, "BouncingBalls: $index")
                }),
        ) {
            val touch = tick
            val glowRadius = b.r * glowRadiusMult
            val center = Offset(b.x, b.y)
            val core = palette[b.colorIndex % palette.size]
            val brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0f to core,
                    gradientMidStop to core.copy(alpha = gradientMidAlpha),
                    1f to Color.Transparent,
                ),
                center = center,
                radius = glowRadius,
            )
            drawCircle(
                brush = brush,
                radius = glowRadius,
                center = center,
                blendMode = BlendMode.Plus,
            )

            val coreRadius = b.r * 1.2f
            val ballColor = palette[b.colorIndex % palette.size]
            val main = Path().apply {
                drawCircle(
                    color = ballColor.copy(alpha = 0.95f),
                    radius = max(1f, coreRadius),
                    center = Offset(b.x, b.y),
                    blendMode = BlendMode.Plus,
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = max(1f, coreRadius * 0.45f),
                    center = Offset(
                        b.x - coreRadius * 0.25f,
                        b.y - coreRadius * 0.25f,
                    ),
                    blendMode = BlendMode.Plus,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    NewsBubbleTheme {
        HomeContent(
            onCategoryClick = {},
        )
    }
}
