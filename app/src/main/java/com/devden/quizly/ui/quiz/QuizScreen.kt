package com.devden.quizly.ui.quiz

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import com.devden.quizly.ui.quiz.components.*
import com.devden.quizly.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateBack: () -> Unit,
    onQuizComplete: (score: Int, totalQuestions: Int) -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current

    // Handle quiz completion
    LaunchedEffect(uiState.isQuizComplete) {
        if (uiState.isQuizComplete) {
            onQuizComplete(uiState.score, uiState.questions.size)
        }
    }

    // Trigger haptic feedback on answer selection
    LaunchedEffect(uiState.isAnswered) {
        if (uiState.isAnswered) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Floating background particles
        if (!uiState.isAnswered) {
            FloatingParticles(modifier = Modifier.fillMaxSize())
        }

        // Confetti animation for correct answers
        ConfettiAnimation(
            trigger = uiState.isAnswered && uiState.isCorrect == true,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.category?.displayName ?: "Quiz",
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
                        // Sound toggle
                        IconButton(onClick = { viewModel.toggleSound() }) {
                            Icon(
                                imageVector = if (uiState.soundEnabled) {
                                    Icons.AutoMirrored.Filled.VolumeUp
                                } else {
                                    Icons.AutoMirrored.Filled.VolumeOff
                                },
                                contentDescription = "Toggle Sound"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            when {
                uiState.isLoading -> {
                    LoadingState(modifier = Modifier.padding(paddingValues))
                }
                uiState.error != null -> {
                    ErrorState(
                        error = uiState.error ?: "",
                        onRetry = { viewModel.restartQuiz() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                uiState.isQuizComplete -> {
                    QuizCompleteState(
                        score = uiState.score,
                        totalQuestions = uiState.questions.size,
                        correctAnswers = uiState.answeredQuestions.count { it.isCorrect },
                        maxStreak = uiState.maxStreak,
                        onRestart = { viewModel.restartQuiz() },
                        onExit = onNavigateBack,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                else -> {
                    QuizActiveState(
                        uiState = uiState,
                        onAnswerSelected = { index ->
                            viewModel.onAnswerSelected(index)
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading questions...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = ErrorRed
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

@Composable
private fun QuizActiveState(
    uiState: QuizUiState,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentQuestion = uiState.questions.getOrNull(uiState.currentQuestionIndex) ?: return

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress bar
        LinearProgressBar(
            currentQuestion = uiState.currentQuestionIndex + 1,
            totalQuestions = uiState.questions.size,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Top info row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score display
            ScoreDisplay(score = uiState.score)

            // Timer
            CircularTimer(
                timeRemaining = uiState.timeRemaining,
                totalTime = currentQuestion.timeLimit
            )

            // Streak display
            StreakDisplay(streak = uiState.currentStreak)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Swipeable question content with smooth animations
        AnimatedContent(
            targetState = uiState.currentQuestionIndex,
            transitionSpec = {
                // Slide from right to left when moving to next question
                if (targetState > initialState) {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(
                        animationSpec = tween(300)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeOut(
                        animationSpec = tween(300)
                    )
                } else {
                    // Slide from left to right when going back (shouldn't happen but handles edge case)
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(
                        animationSpec = tween(300)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeOut(
                        animationSpec = tween(300)
                    )
                }
            },
            label = "question_transition"
        ) { questionIndex ->
            val question = uiState.questions.getOrNull(questionIndex)

            if (question != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Question card
                    QuestionCard(
                        questionText = question.questionText,
                        questionNumber = questionIndex + 1,
                        totalQuestions = uiState.questions.size,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Answer options
                    question.options.forEachIndexed { index, option ->
                        val optionLetter = ('A' + index).toString()

                        AnswerOptionCard(
                            optionText = option,
                            optionLetter = optionLetter,
                            isSelected = uiState.selectedAnswerIndex == index,
                            isCorrect = if (index == question.correctAnswerIndex) true else null,
                            isAnswered = uiState.isAnswered,
                            onClick = { onAnswerSelected(index) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (index < question.options.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Correct answer feedback
                    AnimatedVisibility(
                        visible = uiState.isAnswered && uiState.isCorrect == true,
                        enter = scaleIn(
                            initialScale = 0.8f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(
                            animationSpec = tween(300)
                        ) + expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = SuccessGreen.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Correct",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Excellent! 🎉",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                    Text(
                                        text = "+10 points",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    // Incorrect answer feedback
                    AnimatedVisibility(
                        visible = uiState.isAnswered && uiState.isCorrect == false,
                        enter = slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(
                            animationSpec = tween(300)
                        ) + expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorRed.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Incorrect",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Not quite right",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ErrorRed
                                    )
                                    Text(
                                        text = "Better luck next time!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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

@Composable
private fun QuizCompleteState(
    score: Int,
    totalQuestions: Int,
    correctAnswers: Int,
    maxStreak: Int,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = (correctAnswers.toFloat() / totalQuestions) * 100
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Starburst animation
        StarburstAnimation(
            trigger = visible,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        modifier = Modifier.size(120.dp),
                        tint = Color(0xFFFFD700)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Quiz Complete!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when {
                            percentage >= 90 -> "Outstanding! 🌟"
                            percentage >= 70 -> "Great job! 👏"
                            percentage >= 50 -> "Good effort! 💪"
                            else -> "Keep practicing! 📚"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Score card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = score.toString(),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Total Score",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard(
                            label = "Correct",
                            value = "$correctAnswers/$totalQuestions",
                            icon = Icons.Default.CheckCircle,
                            color = SuccessGreen
                        )

                        StatCard(
                            label = "Accuracy",
                            value = "${percentage.toInt()}%",
                            icon = Icons.Default.Percent,
                            color = ElectricBlue60
                        )

                        StatCard(
                            label = "Best Streak",
                            value = maxStreak.toString(),
                            icon = Icons.Default.LocalFireDepartment,
                            color = Color(0xFFFF6B35)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action buttons
                    Button(
                        onClick = onRestart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Play Again", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onExit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back to Home", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
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

