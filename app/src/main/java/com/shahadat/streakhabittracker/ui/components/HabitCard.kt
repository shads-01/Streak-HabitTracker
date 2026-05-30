package com.shahadat.streakhabittracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shahadat.streakhabittracker.ui.theme.AppColors
import com.shahadat.streakhabittracker.ui.theme.toComposeColor

/**
 * Data class representing a habit card's display state.
 */
data class HabitCardState(
    val id: Long,
    val name: String,
    val groupName: String,
    val groupColorHex: String,
    val streak: Int,
    val isCompletedToday: Boolean,
    val reminderTime: String? = null
)

/**
 * Glass-styled habit card with checkmark, streak badge, and group color accent.
 */
@Composable
fun HabitCard(
    state: HabitCardState,
    onComplete: () -> Unit,
    onUncomplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val groupColor = state.groupColorHex.toComposeColor()
    var showMenu by remember { mutableStateOf(false) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Group color accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(groupColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Habit info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Group name pill
                    Text(
                        text = state.groupName,
                        style = MaterialTheme.typography.labelSmall,
                        color = groupColor.copy(alpha = 0.8f)
                    )

                    // Reminder time
                    if (state.reminderTime != null) {
                        Text(
                            text = "⏰ ${state.reminderTime}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextTertiary
                        )
                    }

                    // Streak badge
                    if (state.streak > 0) {
                        StreakBadge(streak = state.streak)
                    }
                }
            }

            // More menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = AppColors.TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor = AppColors.SurfaceElevated
                ) {
                    DropdownMenuItem(
                        text = { Text("View Calendar", color = AppColors.TextPrimary) },
                        onClick = { showMenu = false; onViewCalendar() }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit", color = AppColors.TextPrimary) },
                        onClick = { showMenu = false; onEdit() }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = AppColors.AccentRed) },
                        onClick = { showMenu = false; onDelete() }
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Checkmark button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (state.isCompletedToday) AppColors.AccentGreen
                        else Color.White.copy(alpha = 0.1f)
                    )
                    .clickable {
                        if (state.isCompletedToday) onUncomplete() else onComplete()
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = state.isCompletedToday,
                    enter = scaleIn(tween(200)),
                    exit = scaleOut(tween(200))
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Complete habit",
                        tint = AppColors.TextOnAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
