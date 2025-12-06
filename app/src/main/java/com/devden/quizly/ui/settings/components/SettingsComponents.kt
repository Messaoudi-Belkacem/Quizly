package com.devden.quizly.ui.settings.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import com.devden.quizly.ui.settings.ThemeMode
import com.devden.quizly.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ThemeToggleCard(
    currentTheme: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var showCheckmark by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = when (currentTheme) {
            ThemeMode.LIGHT -> Color(0xFFFFF9E6)
            ThemeMode.DARK -> Color(0xFF1A1A2E)
            ThemeMode.SYSTEM -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(500),
        label = "theme_bg"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                AnimatedVisibility(
                    visible = showCheckmark,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Saved",
                        tint = SuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val scope = rememberCoroutineScope()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ThemeMode.entries.forEach { theme ->
                    ThemeOption(
                        theme = theme,
                        isSelected = currentTheme == theme,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            onThemeChanged(theme)
                            showCheckmark = true
                            scope.launch {
                                delay(1000)
                                showCheckmark = false
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOption(
    theme: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "theme_scale"
    )

    Column(
        modifier = modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(64.dp * scale),
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) ElectricBlue60 else MaterialTheme.colorScheme.surface,
            tonalElevation = if (isSelected) 4.dp else 1.dp
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (theme) {
                        ThemeMode.LIGHT -> Icons.Default.LightMode
                        ThemeMode.DARK -> Icons.Default.DarkMode
                        ThemeMode.SYSTEM -> Icons.Default.Brightness4
                    },
                    contentDescription = theme.name,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (theme) {
                ThemeMode.LIGHT -> "Light"
                ThemeMode.DARK -> "Dark"
                ThemeMode.SYSTEM -> "Auto"
            },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) ElectricBlue60 else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SettingToggleItem(
    title: String,
    description: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    playPreview: Boolean = false,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                onCheckedChange(!checked)
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (checked) ElectricBlue60 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        AnimatedSwitch(
            checked = checked,
            onCheckedChange = {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                onCheckedChange(it)
            }
        )
    }
}

@Composable
private fun AnimatedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = SuccessGreen,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun ExpandableSection(
    title: String,
    icon: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chevron_rotation"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        onToggle()
                    }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }

            // Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                    content()
                }
            }
        }
    }
}

@Composable
fun DangerButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var shake by remember { mutableStateOf(false) }
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    val offsetX by animateFloatAsState(
        targetValue = if (shake) 10f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        finishedListener = {
            if (shake) {
                scope.launch {
                    delay(50)
                    shake = false
                }
            }
        },
        label = "shake"
    )

    OutlinedButton(
        onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            shake = true
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .offset(x = offsetX.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ErrorRed
        ),
        border = BorderStroke(1.dp, ErrorRed)
    ) {
        Icon(imageVector = icon, contentDescription = text)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun PulsingButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = ElectricBlue60
        )
    ) {
        Icon(imageVector = icon, contentDescription = text)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

