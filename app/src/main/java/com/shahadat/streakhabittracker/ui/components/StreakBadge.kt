package com.shahadat.streakhabittracker.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shahadat.streakhabittracker.ui.theme.AppColors

/**
 * Animated streak badge showing the current streak count with a fire emoji.
 * The number animates smoothly when the streak changes.
 */
@Composable
fun StreakBadge(
    streak: Int,
    modifier: Modifier = Modifier,
    showLabel: Boolean = false
) {
    val animatedStreak by animateIntAsState(
        targetValue = streak,
        animationSpec = tween(500),
        label = "streak_anim"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "🔥",
            fontSize = 16.sp,
        )
        Text(
            text = if (showLabel) "$animatedStreak days" else "$animatedStreak",
            style = MaterialTheme.typography.labelLarge,
            color = if (streak > 0) AppColors.AccentAmber else AppColors.TextTertiary
        )
    }
}

/**
 * Large streak display for the habit calendar detail view.
 */
@Composable
fun StreakBadgeLarge(
    streak: Int,
    totalCompletions: Int,
    modifier: Modifier = Modifier
) {
    val animatedStreak by animateIntAsState(
        targetValue = streak,
        animationSpec = tween(600),
        label = "streak_large_anim"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🔥",
                fontSize = 28.sp,
            )
            Text(
                text = "$animatedStreak",
                style = MaterialTheme.typography.headlineLarge,
                color = AppColors.AccentAmber
            )
            Text(
                text = "Current Streak",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextSecondary
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "✅",
                fontSize = 28.sp,
            )
            Text(
                text = "$totalCompletions",
                style = MaterialTheme.typography.headlineLarge,
                color = AppColors.AccentGreen
            )
            Text(
                text = "Total Done",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextSecondary
            )
        }
    }
}
