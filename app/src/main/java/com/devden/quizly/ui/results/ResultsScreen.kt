package com.devden.quizly.ui.results

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devden.quizly.ui.results.components.*
import com.devden.quizly.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    onPlayAgain: () -> Unit,
    onGoHome: () -> Unit,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current

    var scoreRevealed by remember { mutableStateOf(false) }
    var trophyVisible by remember { mutableStateOf(false) }

    // Haptic feedback on score complete
    LaunchedEffect(scoreRevealed) {
        if (scoreRevealed) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    // Show trophy after score reveal
    LaunchedEffect(scoreRevealed) {
        if (scoreRevealed) {
            delay(300)
            trophyVisible = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // New Record background flash
        if (uiState.isNewPersonalBest) {
            NewRecordFlash()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Results",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // New Record Banner
                if (uiState.isNewPersonalBest) {
                    NewRecordBanner(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Achievement message
                Text(
                    text = uiState.achievementLevel.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = uiState.achievementLevel.emoji,
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Score reveal animation
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Score",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AnimatedScoreReveal(
                            targetScore = uiState.score,
                            onComplete = { scoreRevealed = true }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${uiState.percentage.toInt()}% Correct",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )

                        if (uiState.isNewPersonalBest && scoreRevealed) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Previous Best: ${uiState.previousBest}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Trophy/Badge animation
                if (trophyVisible) {
                    TrophyBadgeAnimation(
                        achievementLevel = uiState.achievementLevel.title,
                        isNewBest = uiState.isNewPersonalBest,
                        modifier = Modifier.size(150.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Results breakdown
                AnimatedVisibility(
                    visible = scoreRevealed,
                    enter = fadeIn() + expandVertically()
                ) {
                    Column {
                        SegmentedProgressBar(
                            correctCount = uiState.correctAnswers,
                            incorrectCount = uiState.incorrectAnswers,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Statistics cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard(
                                label = "Correct",
                                value = uiState.correctAnswers.toString(),
                                icon = Icons.Default.CheckCircle,
                                color = SuccessGreen
                            )

                            StatCard(
                                label = "Streak",
                                value = uiState.maxStreak.toString(),
                                icon = Icons.Default.LocalFireDepartment,
                                color = Color(0xFFFF6B35)
                            )

                            StatCard(
                                label = "Total",
                                value = uiState.totalQuestions.toString(),
                                icon = Icons.Default.Quiz,
                                color = ElectricBlue60
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Category performance
                        if (uiState.allCategoryScores.isNotEmpty()) {
                            CategoryPerformanceGrid(
                                categories = uiState.allCategoryScores
                                    .filter { it.attempts > 0 }
                                    .map { it.category to it.accuracy }
                                    .take(5),
                                bestCategory = uiState.bestCategory,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        // Action buttons
                        AnimatedActionButtons(
                            onPlayAgain = onPlayAgain,
                            onGoHome = onGoHome,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun NewRecordFlash() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(300)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(150)),
        exit = fadeOut(animationSpec = tween(150))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFD700).copy(alpha = 0.3f))
        )
    }
}

@Composable
private fun NewRecordBanner(
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    var waveOffset by remember { mutableStateOf(0f) }

    val animatedWaveOffset by animateFloatAsState(
        targetValue = waveOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "wave"
    )

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
        waveOffset = 1f
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFD700).copy(alpha = 0.2f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "New Record",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "🎉 New Personal Best! 🎉",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn()
    ) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

