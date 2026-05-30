package com.shahadat.streakhabittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shahadat.streakhabittracker.ui.theme.AppColors

/**
 * A glassmorphism-style card composable.
 * Creates a semi-transparent frosted glass effect with subtle border gradient.
 *
 * @param modifier Modifier for the card
 * @param cornerRadius Corner radius for the card shape
 * @param backgroundAlpha Alpha value for the white background tint
 * @param content Composable content to display inside the card
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    backgroundAlpha: Float = 0.08f,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = backgroundAlpha))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = shape
            ),
        content = content
    )
}

/**
 * A full-width glass card with padding, commonly used for list items.
 */
@Composable
fun GlassListCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        content = content
    )
}

/**
 * A glass surface with slightly higher opacity, used for elevated sections.
 */
@Composable
fun GlassElevatedCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    GlassCard(
        modifier = modifier,
        cornerRadius = cornerRadius,
        backgroundAlpha = 0.12f,
        content = content
    )
}
