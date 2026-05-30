package com.shahadat.streakhabittracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shahadat.streakhabittracker.data.db.entities.HabitGroup
import com.shahadat.streakhabittracker.ui.theme.AppColors
import com.shahadat.streakhabittracker.ui.theme.toComposeColor

/**
 * Horizontal scrollable row of group filter pills.
 * Includes an "All" pill that shows all habits.
 */
@Composable
fun GroupFilterRow(
    groups: List<HabitGroup>,
    selectedGroupId: Long?,  // null = "All" selected
    onGroupSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // "All" pill
        item {
            GroupPill(
                text = "All",
                color = AppColors.AccentPurple,
                isSelected = selectedGroupId == null,
                onClick = { onGroupSelected(null) }
            )
        }

        items(groups, key = { it.id }) { group ->
            GroupPill(
                text = group.name,
                color = group.colorHex.toComposeColor(),
                isSelected = selectedGroupId == group.id,
                onClick = { onGroupSelected(group.id) }
            )
        }
    }
}

@Composable
private fun GroupPill(
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent,
        animationSpec = tween(300),
        label = "pill_bg"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) color else Color.White.copy(alpha = 0.15f),
        animationSpec = tween(300),
        label = "pill_border"
    )

    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(animatedBgColor)
            .border(1.dp, animatedBorderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) color else AppColors.TextSecondary
        )
    }
}
