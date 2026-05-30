package com.shahadat.streakhabittracker.ui.screens.calendar

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shahadat.streakhabittracker.ui.components.GlassCard
import com.shahadat.streakhabittracker.ui.components.StreakBadge
import com.shahadat.streakhabittracker.ui.components.StreakBadgeLarge
import com.shahadat.streakhabittracker.ui.theme.AppColors
import com.shahadat.streakhabittracker.ui.theme.toComposeColor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * All-habits calendar overview. Shows habits grouped with mini heatmaps.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHabitCalendar: (Long) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Calendar",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AppColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background)
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColors.AccentPurple)
            }
        } else if (uiState.habitsByGroup.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(
                    "No habits to show.\nAdd some habits first!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                uiState.habitsByGroup.forEach { (group, habits) ->
                    item(key = "group_${group.id}") {
                        var expanded by remember { mutableStateOf(true) }
                        val groupColor = group.colorHex.toComposeColor()

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Group header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expanded = !expanded },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(groupColor)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            group.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = groupColor
                                        )
                                    }
                                    Icon(
                                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Toggle",
                                        tint = AppColors.TextTertiary
                                    )
                                }

                                // Habits with mini heatmaps
                                AnimatedVisibility(visible = expanded) {
                                    Column(
                                        modifier = Modifier.padding(top = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        habits.forEach { habitWithStreak ->
                                            HabitHeatmapRow(
                                                habitWithStreak = habitWithStreak,
                                                groupColor = groupColor,
                                                onClick = {
                                                    onNavigateToHabitCalendar(habitWithStreak.habit.id)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitHeatmapRow(
    habitWithStreak: HabitWithStreak,
    groupColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                habitWithStreak.habit.name,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextPrimary
            )
            StreakBadge(streak = habitWithStreak.streak)
        }

        Spacer(Modifier.height(6.dp))

        // Mini heatmap (last 90 days)
        MiniHeatmap(
            completedDates = habitWithStreak.completedDates,
            accentColor = groupColor,
            days = 90
        )
    }
}

@Composable
fun MiniHeatmap(
    completedDates: Set<String>,
    accentColor: Color,
    days: Int = 90,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val startDate = today.minusDays(days.toLong() - 1)
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        reverseLayout = false
    ) {
        items(days) { dayOffset ->
            val date = startDate.plusDays(dayOffset.toLong())
            val isCompleted = completedDates.contains(date.format(dateFormatter))

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isCompleted) accentColor.copy(alpha = 0.8f)
                        else Color.White.copy(alpha = 0.06f)
                    )
            )
        }
    }
}

/**
 * Per-habit monthly calendar with colored completion cells.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCalendarScreen(
    habitId: Long,
    onNavigateBack: () -> Unit,
    viewModel: HabitCalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(habitId) {
        viewModel.loadHabit(habitId)
    }

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.habit?.name ?: "Habit Calendar",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AppColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background)
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColors.AccentPurple)
            }
        } else {
            val groupColor = uiState.group?.colorHex?.toComposeColor() ?: AppColors.AccentPurple

            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Streak stats
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        StreakBadgeLarge(
                            streak = uiState.streak,
                            totalCompletions = uiState.totalCompletions,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                // Monthly calendar
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Month navigation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    viewModel.changeMonth(uiState.currentMonth.minusMonths(1))
                                }) {
                                    Icon(
                                        Icons.Default.ChevronLeft,
                                        contentDescription = "Previous month",
                                        tint = AppColors.TextSecondary
                                    )
                                }

                                Text(
                                    text = uiState.currentMonth.format(
                                        DateTimeFormatter.ofPattern("MMMM yyyy")
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AppColors.TextPrimary
                                )

                                IconButton(onClick = {
                                    viewModel.changeMonth(uiState.currentMonth.plusMonths(1))
                                }) {
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = "Next month",
                                        tint = AppColors.TextSecondary
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Day of week headers
                            Row(modifier = Modifier.fillMaxWidth()) {
                                DayOfWeek.values().forEach { day ->
                                    Text(
                                        text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppColors.TextTertiary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // Calendar grid
                            val firstDayOfMonth = uiState.currentMonth.atDay(1)
                            val startOffset = (firstDayOfMonth.dayOfWeek.value - 1) % 7
                            val daysInMonth = uiState.currentMonth.lengthOfMonth()
                            val totalCells = startOffset + daysInMonth
                            val rows = (totalCells + 6) / 7

                            for (row in 0 until rows) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    for (col in 0..6) {
                                        val dayIndex = row * 7 + col - startOffset + 1
                                        if (dayIndex in 1..daysInMonth) {
                                            val date = uiState.currentMonth.atDay(dayIndex)
                                            val isCompleted = uiState.completedDatesInMonth.contains(date)
                                            val isToday = date == LocalDate.now()

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .padding(2.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(
                                                        when {
                                                            isCompleted -> groupColor.copy(alpha = 0.7f)
                                                            isToday -> Color.White.copy(alpha = 0.08f)
                                                            else -> Color.Transparent
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$dayIndex",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = when {
                                                        isCompleted -> Color.White
                                                        isToday -> AppColors.AccentPurple
                                                        else -> AppColors.TextSecondary
                                                    },
                                                    fontSize = 12.sp
                                                )
                                            }
                                        } else {
                                            Spacer(Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Year heatmap
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Year Overview",
                                style = MaterialTheme.typography.titleMedium,
                                color = AppColors.TextPrimary
                            )
                            Spacer(Modifier.height(12.dp))

                            MiniHeatmap(
                                completedDates = uiState.allCompletedDates.map {
                                    it.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                }.toSet(),
                                accentColor = groupColor,
                                days = 365
                            )
                        }
                    }
                }
            }
        }
    }
}
