package com.devden.quizly.ui.quiz.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var velocityX: Float,
    var velocityY: Float,
    var alpha: Float,
    var size: Float,
    val color: Color
)

@Composable
fun ConfettiAnimation(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }

    LaunchedEffect(trigger) {
        if (trigger) {
            // Create confetti particles
            particles = List(50) {
                Particle(
                    x = 0.5f,
                    y = 0.5f,
                    velocityX = Random.nextFloat() * 2f - 1f,
                    velocityY = Random.nextFloat() * -2f - 1f,
                    alpha = 1f,
                    size = Random.nextFloat() * 8f + 4f,
                    color = listOf(
                        Color(0xFFFFD700),
                        Color(0xFF10B981),
                        Color(0xFF00A8E8),
                        Color(0xFFEC4899),
                        Color(0xFFF97316)
                    ).random()
                )
            }

            // Animate particles
            repeat(60) {
                delay(16)
                particles = particles.map { particle ->
                    particle.copy(
                        x = particle.x + particle.velocityX * 0.02f,
                        y = particle.y + particle.velocityY * 0.02f + it * 0.001f,
                        velocityY = particle.velocityY + 0.05f, // Gravity
                        alpha = (particle.alpha - 0.016f).coerceAtLeast(0f)
                    )
                }.filter { it.alpha > 0 }
            }

            particles = emptyList()
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawCircle(
                color = particle.color.copy(alpha = particle.alpha),
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
fun FloatingParticles(
    modifier: Modifier = Modifier
) {
    var time by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            time += 0.016f
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val particles = List(15) { index ->
            val phase = index * 0.4f
            Offset(
                x = size.width * (0.1f + (index % 5) * 0.2f) +
                    cos(time + phase) * 20f,
                y = size.height * (0.1f + (index / 5) * 0.3f) +
                    sin(time * 0.5f + phase) * 30f
            )
        }

        particles.forEach { position ->
            drawCircle(
                color = Color(0xFF00A8E8).copy(alpha = 0.1f),
                radius = 4f,
                center = position
            )
        }
    }
}

@Composable
fun ShimmerEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    var shimmerOffset by remember { mutableStateOf(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            repeat(30) {
                delay(16)
                shimmerOffset += 0.05f
                if (shimmerOffset > 1f) shimmerOffset = 0f
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (shimmerOffset > 0f) {
            val shimmerX = size.width * shimmerOffset
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = 40f,
                center = Offset(shimmerX, size.height * 0.5f)
            )
        }
    }
}

@Composable
fun StarburstAnimation(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    var stars by remember { mutableStateOf<List<Particle>>(emptyList()) }

    LaunchedEffect(trigger) {
        if (trigger) {
            stars = List(20) { index ->
                val angle = (index * 360f / 20f) * (Math.PI / 180f).toFloat()
                Particle(
                    x = 0.5f,
                    y = 0.5f,
                    velocityX = cos(angle) * 3f,
                    velocityY = sin(angle) * 3f,
                    alpha = 1f,
                    size = 6f,
                    color = Color(0xFFFFD700)
                )
            }

            repeat(30) {
                delay(16)
                stars = stars.map { star ->
                    star.copy(
                        x = star.x + star.velocityX * 0.015f,
                        y = star.y + star.velocityY * 0.015f,
                        alpha = (star.alpha - 0.033f).coerceAtLeast(0f),
                        size = star.size * 0.95f
                    )
                }.filter { it.alpha > 0 }
            }

            stars = emptyList()
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        stars.forEach { star ->
            drawCircle(
                color = star.color.copy(alpha = star.alpha),
                radius = star.size,
                center = Offset(
                    x = star.x * size.width,
                    y = star.y * size.height
                )
            )
        }
    }
}

