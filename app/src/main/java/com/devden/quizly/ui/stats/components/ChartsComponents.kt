package com.devden.quizly.ui.stats.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.devden.quizly.data.local.CategoryScore
import com.devden.quizly.data.model.QuizCategory
import com.devden.quizly.ui.theme.CategoryFood
import com.devden.quizly.ui.theme.CategoryGames
import com.devden.quizly.ui.theme.CategoryGeography
import com.devden.quizly.ui.theme.CategoryHistory
import com.devden.quizly.ui.theme.CategoryIslam
import com.devden.quizly.ui.theme.CategoryLiterature
import com.devden.quizly.ui.theme.CategoryScience
import com.devden.quizly.ui.theme.CategorySports
import com.devden.quizly.ui.theme.CategoryTechnology
import com.devden.quizly.ui.theme.ElectricBlue60
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProgressLineChart(
    dataPoints: List<Float>, // Scores over time
    modifier: Modifier = Modifier,
) {
    var drawProgress by remember { mutableFloatStateOf(0f) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val animatedProgress by animateFloatAsState(
        targetValue = drawProgress,
        animationSpec = tween(2000, easing = EaseInOut),
        label = "line_draw"
    )

    LaunchedEffect(Unit) {
        delay(300)
        drawProgress = 1f
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
                .padding(16.dp)
        ) {
            Text(
                text = "Progress Over Time",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (dataPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Complete more quizzes to see your progress!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val pointWidth = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                                val index =
                                    (offset.x / pointWidth).toInt().coerceIn(0, dataPoints.size - 1)
                                selectedIndex = if (selectedIndex == index) null else index
                            }
                        }
                ) {
                    val maxValue = dataPoints.maxOrNull() ?: 100f
                    val minValue = dataPoints.minOrNull() ?: 0f
                    val range = (maxValue - minValue).coerceAtLeast(1f)

                    val pointWidth = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                    val points = dataPoints.mapIndexed { index, value ->
                        val x = index * pointWidth
                        val normalizedValue = (value - minValue) / range
                        val y = size.height - (normalizedValue * (size.height - 40f)) - 20f
                        Offset(x, y)
                    }

                    // Draw grid lines
                    for (i in 0..4) {
                        val y = size.height * (i / 4f)
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.2f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Draw line with animation
                    val visiblePoints = (points.size * animatedProgress).toInt().coerceAtLeast(2)
                    if (visiblePoints >= 2) {
                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until visiblePoints) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }

                        // Draw gradient fill
                        val gradientPath = Path().apply {
                            addPath(path)
                            lineTo(points[visiblePoints - 1].x, size.height)
                            lineTo(points[0].x, size.height)
                            close()
                        }

                        drawPath(
                            path = gradientPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ElectricBlue60.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )

                        // Draw line
                        drawPath(
                            path = path,
                            color = ElectricBlue60,
                            style = Stroke(
                                width = 4f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // Draw data points
                        points.take(visiblePoints).forEachIndexed { index, point ->
                            val isSelected = selectedIndex == index
                            val pointSize = if (isSelected) 12f else 8f

                            // Outer circle
                            drawCircle(
                                color = ElectricBlue60,
                                radius = pointSize,
                                center = point
                            )

                            // Inner circle
                            drawCircle(
                                color = Color.White,
                                radius = pointSize - 2f,
                                center = point
                            )

                            // Pulse effect for selected
                            if (isSelected) {
                                drawCircle(
                                    color = ElectricBlue60.copy(alpha = 0.3f),
                                    radius = pointSize + 8f,
                                    center = point
                                )
                            }
                        }
                    }
                }

                // Show selected point info
                selectedIndex?.let { index ->
                    if (index < dataPoints.size) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricBlue60.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Quiz ${index + 1}: ",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${dataPoints[index].toInt()} points",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElectricBlue60
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryPieChart(
    categoryScores: List<CategoryScore>,
    modifier: Modifier = Modifier,
) {
    var rotationAngle by remember { mutableFloatStateOf(-90f) }
    var selectedSlice by remember { mutableStateOf<Int?>(null) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pie_rotation"
    )

    LaunchedEffect(Unit) {
        delay(500)
        rotationAngle = 0f
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
                text = "Category Performance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (categoryScores.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Complete quizzes to see category breakdown!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                val totalScore = categoryScores.sumOf { it.totalScore }
                val slices = categoryScores.map { score ->
                    PieSlice(
                        category = score.category,
                        value = score.totalScore.toFloat(),
                        percentage = (score.totalScore.toFloat() / totalScore) * 100f,
                        color = getCategoryColor(score.category)
                    )
                }

                Canvas(
                    modifier = Modifier
                        .size(250.dp)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f
                                val radius = size.width.coerceAtMost(size.height) / 2f - 20f

                                val dx = offset.x - centerX
                                val dy = offset.y - centerY
                                val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                                if (distance <= radius) {
                                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
                                        .toFloat()
                                    angle = (angle + 360f) % 360f

                                    var currentAngle = 0f
                                    slices.forEachIndexed { index, slice ->
                                        val sweepAngle = (slice.value / totalScore) * 360f
                                        if (angle >= currentAngle && angle < currentAngle + sweepAngle) {
                                            selectedSlice =
                                                if (selectedSlice == index) null else index
                                        }
                                        currentAngle += sweepAngle
                                    }
                                }
                            }
                        }
                ) {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val radius = size.width.coerceAtMost(size.height) / 2f - 20f

                    var startAngle = 0f

                    rotate(animatedRotation, pivot = Offset(centerX, centerY)) {
                        slices.forEachIndexed { index, slice ->
                            val sweepAngle = (slice.value / totalScore) * 360f
                            val isSelected = selectedSlice == index
                            val detachOffset = if (isSelected) 15f else 0f

                            // Calculate slice center for detach effect
                            val sliceAngle = startAngle + sweepAngle / 2f
                            val radians = Math.toRadians(sliceAngle.toDouble())
                            val offsetX = centerX + cos(radians).toFloat() * detachOffset
                            val offsetY = centerY + sin(radians).toFloat() * detachOffset

                            // Draw slice
                            drawArc(
                                color = slice.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                topLeft = Offset(offsetX - radius, offsetY - radius),
                                size = Size(radius * 2, radius * 2)
                            )

                            // Draw border
                            drawArc(
                                color = Color.White.copy(alpha = 0.5f),
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                topLeft = Offset(offsetX - radius, offsetY - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = 2f)
                            )

                            startAngle += sweepAngle
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Legend
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    slices.forEachIndexed { index, slice ->
                        PieLegendItem(
                            slice = slice,
                            isSelected = selectedSlice == index,
                            onClick = {
                                selectedSlice = if (selectedSlice == index) null else index
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PieLegendItem(
    slice: PieSlice,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) slice.color.copy(alpha = 0.1f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .drawBehind {
                        drawCircle(color = slice.color)
                    }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = slice.category.displayName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${slice.percentage.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = slice.color
            )
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

private data class PieSlice(
    val category: QuizCategory,
    val value: Float,
    val percentage: Float,
    val color: Color,
)

