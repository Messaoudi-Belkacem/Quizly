package com.devden.quizly.ui.stats

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devden.quizly.ui.stats.components.*
import com.devden.quizly.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistics",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Refresh button
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            isRefreshing = true
                            viewModel.refreshStats()
                        }
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            // TODO: Implement share functionality
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingState()
            } else {
                StatsContent(
                    uiState = uiState
                )
            }
        }

        // Reset refreshing state when loading completes
        LaunchedEffect(uiState.isLoading) {
            if (!uiState.isLoading && isRefreshing) {
                delay(500)
                isRefreshing = false
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading statistics...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun StatsContent(
    uiState: StatsUiState
) {
    var visibleSections by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(6) { index ->
            delay(150)
            visibleSections = index + 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overview Cards
        AnimatedVisibility(
            visible = visibleSections >= 1,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            OverviewCards(
                totalScore = uiState.totalScore,
                totalQuizzes = uiState.totalQuizzes,
                averageScore = uiState.averageScore,
                unlockedBadges = uiState.unlockedBadges.size,
                totalBadges = uiState.badges.size
            )
        }

        // Progress Line Chart
        AnimatedVisibility(
            visible = visibleSections >= 2,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            // Generate sample progress data from quiz history
            val progressData = remember(uiState.categoryScores) {
                if (uiState.totalQuizzes > 0) {
                    // Create sample data points based on attempts
                    val points = mutableListOf<Float>()
                    var runningTotal = 0f
                    uiState.categoryScores.forEach { category ->
                        repeat(category.attempts) {
                            runningTotal += category.averageScore
                            points.add(runningTotal / (points.size + 1))
                        }
                    }
                    points.takeLast(20) // Show last 20 quizzes
                } else emptyList()
            }

            ProgressLineChart(
                dataPoints = progressData,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Category Pie Chart
        AnimatedVisibility(
            visible = visibleSections >= 3,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            CategoryPieChart(
                categoryScores = uiState.categoryScores,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Streak Counter
        AnimatedVisibility(
            visible = visibleSections >= 4,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            StreakCounter(
                currentStreak = uiState.currentStreak,
                bestStreak = uiState.bestStreak,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Trophy Cabinet
        AnimatedVisibility(
            visible = visibleSections >= 5,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            TrophyCabinet(
                badges = uiState.badges,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun OverviewCards(
    totalScore: Int,
    totalQuizzes: Int,
    averageScore: Float,
    unlockedBadges: Int,
    totalBadges: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Score",
                value = totalScore.toString(),
                icon = "🎯",
                color = ElectricBlue60,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Quizzes",
                value = totalQuizzes.toString(),
                icon = "📝",
                color = VibrantPurple60,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Avg Score",
                value = averageScore.toInt().toString(),
                icon = "📊",
                color = VibrantTeal60,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Badges",
                value = "$unlockedBadges/$totalBadges",
                icon = "🏆",
                color = Color(0xFFFFD700),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.displaySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

