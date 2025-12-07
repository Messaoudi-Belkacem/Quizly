package com.devden.quizly.ui.results.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.devden.quizly.data.model.QuizCategory
import com.devden.quizly.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedScoreReveal(
    targetScore: Int,
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentScore by remember { mutableStateOf(0) }
    var scale by remember { mutableStateOf(1f) }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    LaunchedEffect(targetScore) {
        val duration = 2000L
        val steps = 50
        val delayTime = duration / steps

        repeat(steps) { step ->
            delay(delayTime)
            // Use float division to avoid integer rounding issues
            val progress = (step + 1).toFloat() / steps.toFloat()
            currentScore = (targetScore * progress).toInt()
            scale = if (step == steps - 1) 1.2f else 1f
        }

        // Ensure we reach the exact target score
        currentScore = targetScore
        delay(200)
        scale = 1f
        onComplete()
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        Canvas(modifier = Modifier.size(200.dp)) {
            if (scale > 1f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue60.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 2 * scale
                )
            }
        }

        Text(
            text = currentScore.toString(),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.5f
            ),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(animatedScale)
        )
    }
}

@Composable
fun TrophyBadgeAnimation(
    achievementLevel: String,
    isNewBest: Boolean,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        delay(500)
        visible = true
        delay(300)
        rotation = 360f
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Sparkle particles
            SparkleParticles(trigger = visible)

            // Trophy icon
            Icon(
                imageVector = if (isNewBest) Icons.Default.EmojiEvents else Icons.Default.Star,
                contentDescription = "Trophy",
                modifier = Modifier
                    .size(120.dp)
                    .rotate(animatedRotation),
                tint = Color(0xFFFFD700)
            )

            // Shine loop
            ShineEffect(active = visible)
        }
    }
}

@Composable
private fun SparkleParticles(trigger: Boolean) {
    var particles by remember { mutableStateOf<List<SparkleParticle>>(emptyList()) }

    LaunchedEffect(trigger) {
        if (trigger) {
            particles = List(20) { index ->
                val angle = (index * 360f / 20f) * (Math.PI / 180f).toFloat()
                SparkleParticle(
                    x = 0.5f,
                    y = 0.5f,
                    velocityX = cos(angle) * 2f,
                    velocityY = sin(angle) * 2f,
                    alpha = 1f,
                    size = 4f
                )
            }

            repeat(40) {
                delay(16)
                particles = particles.map { particle ->
                    particle.copy(
                        x = particle.x + particle.velocityX * 0.02f,
                        y = particle.y + particle.velocityY * 0.02f,
                        alpha = (particle.alpha - 0.025f).coerceAtLeast(0f)
                    )
                }.filter { it.alpha > 0 }
            }

            particles = emptyList()
        }
    }

    Canvas(modifier = Modifier.size(200.dp)) {
        particles.forEach { particle ->
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(
                    x = particle.x * size.width,
                    y = particle.y * size.height
                )
            )
        }
    }
}

@Composable
private fun ShineEffect(active: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shineRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine"
    )

    if (active) {
        Canvas(modifier = Modifier.size(150.dp).rotate(shineRotation)) {
            val path = Path().apply {
                moveTo(size.width / 2, 0f)
                lineTo(size.width / 2 + 2f, size.height / 2 - 20f)
                lineTo(size.width / 2, size.height / 2 - 10f)
                lineTo(size.width / 2 - 2f, size.height / 2 - 20f)
                close()
            }

            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun SegmentedProgressBar(
    correctCount: Int,
    incorrectCount: Int,
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableStateOf(false) }
    var correctFilled by remember { mutableStateOf(false) }
    var incorrectFilled by remember { mutableStateOf(false) }

    val total = correctCount + incorrectCount
    val correctProgress = if (total > 0) correctCount.toFloat() / total else 0f
    val incorrectProgress = if (total > 0) incorrectCount.toFloat() / total else 0f

    val animatedCorrectProgress by animateFloatAsState(
        targetValue = if (animationStarted) correctProgress else 0f,
        animationSpec = tween(1000, easing = EaseOut),
        finishedListener = {
            if (it > 0f) correctFilled = true
        },
        label = "correct"
    )

    val animatedIncorrectProgress by animateFloatAsState(
        targetValue = if (animationStarted) incorrectProgress else 0f,
        animationSpec = tween(1000, delayMillis = 500, easing = EaseOut),
        finishedListener = {
            if (it > 0f) incorrectFilled = true
        },
        label = "incorrect"
    )

    LaunchedEffect(Unit) {
        delay(300)
        animationStarted = true
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Results Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$correctCount / $total",
                style = MaterialTheme.typography.titleMedium,
                color = SuccessGreen
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Correct segment
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedCorrectProgress)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    SuccessGreen,
                                    SuccessGreenLight
                                )
                            )
                        )
                        .then(
                            if (correctFilled) {
                                Modifier.pulseEffect()
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (animatedCorrectProgress > 0.1f) {
                        Text(
                            text = "✓ $correctCount",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Incorrect segment
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(
                            if (animatedCorrectProgress > 0f) {
                                animatedIncorrectProgress / (1f - animatedCorrectProgress)
                            } else 0f
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    ErrorRed,
                                    ErrorRedLight
                                )
                            )
                        )
                        .then(
                            if (incorrectFilled) {
                                Modifier.pulseEffect()
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (animatedIncorrectProgress > 0.1f) {
                        Text(
                            text = "✗ $incorrectCount",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Modifier.pulseEffect(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    return this.scale(scale)
}

@Composable
fun CategoryPerformanceGrid(
    categories: List<Pair<QuizCategory, Float>>, // Category to accuracy
    bestCategory: QuizCategory?,
    modifier: Modifier = Modifier
) {
    var visibleCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        categories.indices.forEach { index ->
            delay(100)
            visibleCount = index + 1
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Category Performance",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        categories.take(visibleCount).forEach { (category, accuracy) ->
            CategoryPerformanceItem(
                category = category,
                accuracy = accuracy,
                isBest = category == bestCategory,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CategoryPerformanceItem(
    category: QuizCategory,
    accuracy: Float,
    isBest: Boolean,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(getCategoryColor(category).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.displayName,
                        tint = getCategoryColor(category),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${accuracy.toInt()}% accuracy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                if (isBest) {
                    AnimatedCrown()
                }
            }
        }
    }
}

@Composable
private fun AnimatedCrown() {
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
        Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = "Best Category",
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun AnimatedActionButtons(
    onPlayAgain: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var playAgainVisible by remember { mutableStateOf(false) }
    var goHomeVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        playAgainVisible = true
        delay(150)
        goHomeVisible = true
    }

    Column(modifier = modifier) {
        AnimatedVisibility(
            visible = playAgainVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn()
        ) {
            Button(
                onClick = onPlayAgain,
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
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(
            visible = goHomeVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn()
        ) {
            OutlinedButton(
                onClick = onGoHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Go Home", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun getCategoryColor(category: QuizCategory): Color {
    return when (category) {
        QuizCategory.SCIENCE -> CategoryScience
        QuizCategory.HISTORY -> CategoryHistory
        QuizCategory.GEOGRAPHY -> CategoryGeography
        QuizCategory.LITERATURE -> CategoryLiterature
        QuizCategory.VIDEO_GAMES -> CategoryGames
        QuizCategory.TECHNOLOGY -> CategoryTechnology
        QuizCategory.SPORTS -> CategorySports
        QuizCategory.FOOD -> CategoryFood
        QuizCategory.ISLAM -> CategoryIslam
    }
}

private data class SparkleParticle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val alpha: Float,
    val size: Float
)

