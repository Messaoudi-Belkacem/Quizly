package com.devden.quizly.ui.stats.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devden.quizly.ui.stats.Badge
import com.devden.quizly.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TrophyCabinet(
    badges: List<Badge>,
    modifier: Modifier = Modifier
) {
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "🏆 Trophy Cabinet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${badges.count { it.isUnlocked }} of ${badges.size} unlocked",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calculate height based on number of rows needed
            val rows = (badges.size + 2) / 3 // Round up for 3 columns
            val gridHeight = (rows * 100 + (rows - 1) * 12).dp // 100dp per item + 12dp spacing

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gridHeight),
                userScrollEnabled = false // Disable internal scrolling since parent scrolls
            ) {
                items(badges) { badge ->
                    BadgeItem(
                        badge = badge,
                        onClick = { selectedBadge = badge }
                    )
                }
            }
        }
    }

    // Badge detail dialog
    selectedBadge?.let { badge ->
        BadgeDetailDialog(
            badge = badge,
            onDismiss = { selectedBadge = null }
        )
    }
}

@Composable
private fun BadgeItem(
    badge: Badge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }
    val scope = rememberCoroutineScope()

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "badge_scale"
    )

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
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .scale(animatedScale)
                .clickable {
                    scale = 0.9f
                    scope.launch {
                        delay(100)
                        scale = 1f
                    }
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            // Glow effect for unlocked badges
            if (badge.isUnlocked) {
                GlowEffect(modifier = Modifier.matchParentSize())
            }

            // Badge container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (badge.isUnlocked) {
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            )
                        }
                    )
                    .border(
                        width = if (badge.isUnlocked) 2.dp else 1.dp,
                        color = if (badge.isUnlocked) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (badge.isUnlocked) {
                    Text(
                        text = badge.icon,
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    // Locked badge - grayscale question mark
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Locked",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GlowEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Canvas(modifier = modifier) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFD700).copy(alpha = glowAlpha),
                    Color.Transparent
                ),
                center = Offset(size.width / 2, size.height / 2),
                radius = size.minDimension / 2
            )
        )
    }
}

@Composable
private fun BadgeDetailDialog(
    badge: Badge,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        visible = true
    }

    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Badge icon with animation
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                if (badge.isUnlocked) {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFFD700).copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    )
                                } else {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.Gray.copy(alpha = 0.2f),
                                            Color.Transparent
                                        )
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (badge.isUnlocked) {
                            SparkleEffect()
                            Text(
                                text = badge.icon,
                                style = MaterialTheme.typography.displayLarge
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = "Locked",
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (badge.isUnlocked) "✓ Unlocked" else "🔒 Locked",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (badge.isUnlocked) SuccessGreen else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            visible = false
                            scope.launch {
                                delay(300)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
private fun SparkleEffect() {
    var sparkles by remember { mutableStateOf<List<Sparkle>>(emptyList()) }

    LaunchedEffect(Unit) {
        while (true) {
            sparkles = List(8) { index ->
                val angle = (index * 45f) * (kotlin.math.PI / 180f).toFloat()
                Sparkle(
                    x = 0.5f,
                    y = 0.5f,
                    velocityX = cos(angle) * 1.5f,
                    velocityY = sin(angle) * 1.5f,
                    alpha = 1f,
                    size = 4f
                )
            }

            repeat(20) {
                delay(50)
                sparkles = sparkles.map { sparkle ->
                    sparkle.copy(
                        x = sparkle.x + sparkle.velocityX * 0.05f,
                        y = sparkle.y + sparkle.velocityY * 0.05f,
                        alpha = (sparkle.alpha - 0.05f).coerceAtLeast(0f)
                    )
                }.filter { it.alpha > 0 }
            }

            delay(500)
        }
    }

    Canvas(modifier = Modifier.size(120.dp)) {
        sparkles.forEach { sparkle ->
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = sparkle.alpha),
                radius = sparkle.size,
                center = Offset(
                    x = sparkle.x * size.width,
                    y = sparkle.y * size.height
                )
            )
        }
    }
}

@Composable
fun StreakCounter(
    currentStreak: Int,
    bestStreak: Int,
    modifier: Modifier = Modifier
) {
    var displayStreak by remember { mutableStateOf(0) }

    LaunchedEffect(currentStreak) {
        val start = displayStreak
        val end = currentStreak
        val steps = 20
        val increment = (end - start).toFloat() / steps

        repeat(steps) { step ->
            delay(30)
            displayStreak = start + (increment * (step + 1)).toInt()
        }
        displayStreak = end
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔥 Current Streak",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Animated fire effect
                AnimatedFireEffect(isActive = currentStreak > 0)

                // Streak number
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = displayStreak.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (currentStreak > 0) Color(0xFFFF6B35) else Color.Gray
                    )
                    Text(
                        text = "days",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Best: $bestStreak days",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun AnimatedFireEffect(isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "fire")

    val flameScale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame1"
    )

    val flameScale2 by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame2"
    )

    val emberAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "embers"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (isActive) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // Main flame
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = 0.8f),
                        Color(0xFFFF6B35).copy(alpha = 0.5f),
                        Color(0xFFFF4500).copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                radius = 80f * flameScale1,
                center = Offset(centerX, centerY)
            )

            // Secondary flame
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = 0.6f),
                        Color(0xFFFF6B35).copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                radius = 60f * flameScale2,
                center = Offset(centerX, centerY - 10f)
            )

            // Embers
            repeat(6) { index ->
                val angle = (index * 60f) * (kotlin.math.PI / 180f).toFloat()
                val distance = 100f
                val emberX = centerX + cos(angle) * distance
                val emberY = centerY + sin(angle) * distance

                drawCircle(
                    color = Color(0xFFFF6B35).copy(alpha = emberAlpha),
                    radius = 3f,
                    center = Offset(emberX, emberY)
                )
            }
        } else {
            // Fading embers when streak is 0
            repeat(8) { index ->
                val angle = (index * 45f) * (kotlin.math.PI / 180f).toFloat()
                val distance = 60f
                val emberX = size.width / 2 + cos(angle) * distance
                val emberY = size.height / 2 + sin(angle) * distance

                drawCircle(
                    color = Color.Gray.copy(alpha = 0.2f),
                    radius = 4f,
                    center = Offset(emberX, emberY)
                )
            }
        }
    }
}

private data class Sparkle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val alpha: Float,
    val size: Float
)

