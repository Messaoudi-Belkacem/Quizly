package com.devden.quizly.ui.quiz.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devden.quizly.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun QuestionCard(
    questionText: String,
    questionNumber: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(questionNumber) {
        visible = false
        delay(100)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Question $questionNumber of $totalQuestions",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = questionText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun AnswerOptionCard(
    optionText: String,
    optionLetter: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isAnswered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var shakeOffset by remember { mutableStateOf(0f) }

    val animatedScale by animateFloatAsState(
        targetValue = when {
            isSelected && isCorrect == true -> 1.05f
            isSelected && isCorrect == false -> 0.95f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    // Shake animation for wrong answer
    LaunchedEffect(isSelected, isCorrect) {
        if (isSelected && isCorrect == false) {
            repeat(3) {
                shakeOffset = -10f
                delay(50)
                shakeOffset = 10f
                delay(50)
            }
            shakeOffset = 0f
        }
    }

    // Pulse animation when not answered
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val backgroundColor = when {
        isSelected && isCorrect == true -> SuccessGreen.copy(alpha = 0.2f)
        isSelected && isCorrect == false -> ErrorRed.copy(alpha = 0.2f)
        isAnswered && isCorrect == true && !isSelected -> SuccessGreen.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isSelected && isCorrect == true -> SuccessGreen
        isSelected && isCorrect == false -> ErrorRed
        isAnswered && isCorrect == true && !isSelected -> Color(0xFFFFD700) // Golden
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    val borderWidth = when {
        isAnswered && isCorrect == true && !isSelected -> 3.dp
        isSelected -> 2.dp
        else -> 1.dp
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset(x = shakeOffset.dp)
            .scale(if (!isAnswered) pulseScale else animatedScale)
            .then(
                if (!isAnswered) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Option letter badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected && isCorrect == true -> SuccessGreen
                                isSelected && isCorrect == false -> ErrorRed
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = optionLetter,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isSelected -> Color.White
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Option text
                Text(
                    text = optionText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Result icon
                AnimatedVisibility(
                    visible = isAnswered && isSelected,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn()
                ) {
                    Icon(
                        imageVector = if (isCorrect == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = if (isCorrect == true) "Correct" else "Incorrect",
                        tint = if (isCorrect == true) SuccessGreen else ErrorRed,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Golden outline indicator for correct answer when wrong is selected
                AnimatedVisibility(
                    visible = isAnswered && isCorrect == true && !isSelected,
                    enter = scaleIn() + fadeIn()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Correct Answer",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CircularTimer(
    timeRemaining: Int,
    totalTime: Int,
    modifier: Modifier = Modifier
) {
    val progress = timeRemaining.toFloat() / totalTime.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = LinearEasing),
        label = "timer"
    )

    val timerColor = when {
        progress > 0.5f -> ElectricBlue60
        progress > 0.25f -> Color(0xFFF97316) // Orange
        else -> ErrorRed
    }

    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.Gray.copy(alpha = 0.2f),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Progress arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = timerColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Time text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeRemaining.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = timerColor
            )
            Text(
                text = "sec",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ScoreDisplay(
    score: Int,
    modifier: Modifier = Modifier
) {
    var displayedScore by remember { mutableStateOf(0) }

    LaunchedEffect(score) {
        val start = displayedScore
        val end = score
        val duration = 500
        val steps = 20
        val increment = (end - start).toFloat() / steps

        repeat(steps) { step ->
            delay((duration / steps).toLong())
            displayedScore = start + (increment * (step + 1)).toInt()
        }
        displayedScore = end
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Score",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = displayedScore.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun StreakDisplay(
    streak: Int,
    modifier: Modifier = Modifier
) {
    if (streak == 0) return

    val infiniteTransition = rememberInfiniteTransition(label = "flame")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame"
    )

    Card(
        modifier = modifier.scale(if (streak > 0) 1f else 0f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B35).copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Streak",
                tint = Color(0xFFFF6B35),
                modifier = Modifier
                    .size(24.dp)
                    .scale(flameScale)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$streak",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35)
            )
        }
    }
}

@Composable
fun LinearProgressBar(
    currentQuestion: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    val progress = currentQuestion.toFloat() / totalQuestions.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ElectricBlue60,
                            VibrantPurple60,
                            VibrantTeal60
                        )
                    )
                )
        )
    }
}

