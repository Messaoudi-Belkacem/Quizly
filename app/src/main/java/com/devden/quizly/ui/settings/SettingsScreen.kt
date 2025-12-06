package com.devden.quizly.ui.settings

import android.content.Intent
import android.net.Uri
import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devden.quizly.ui.settings.components.*
import com.devden.quizly.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance Section
            ExpandableSection(
                title = SettingsSection.APPEARANCE.displayName,
                icon = SettingsSection.APPEARANCE.icon,
                isExpanded = uiState.expandedSections.contains(SettingsSection.APPEARANCE),
                onToggle = { viewModel.toggleSection(SettingsSection.APPEARANCE) }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ThemeToggleCard(
                        currentTheme = uiState.themeMode,
                        onThemeChanged = viewModel::onThemeChanged
                    )
                }
            }

            // Notifications Section
            ExpandableSection(
                title = SettingsSection.NOTIFICATIONS.displayName,
                icon = SettingsSection.NOTIFICATIONS.icon,
                isExpanded = uiState.expandedSections.contains(SettingsSection.NOTIFICATIONS),
                onToggle = { viewModel.toggleSection(SettingsSection.NOTIFICATIONS) }
            ) {
                Column {
                    NotificationType.entries.forEach { type ->
                        SettingToggleItem(
                            title = type.displayName,
                            description = when (type) {
                                NotificationType.DAILY_REMINDER -> "Get reminded to take a daily quiz"
                                NotificationType.STREAK_ALERT -> "Be notified about your streak status"
                                NotificationType.ACHIEVEMENTS -> "Celebrate your achievements"
                            },
                            icon = when (type) {
                                NotificationType.DAILY_REMINDER -> Icons.Default.Alarm
                                NotificationType.STREAK_ALERT -> Icons.Default.LocalFireDepartment
                                NotificationType.ACHIEVEMENTS -> Icons.Default.EmojiEvents
                            },
                            checked = uiState.notifications[type] ?: false,
                            onCheckedChange = { enabled ->
                                viewModel.onNotificationToggle(type, enabled)
                            }
                        )
                    }
                }
            }

            // Sound & Vibration Section (Combined with Notifications visually but separate card)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "🔊",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Sound & Haptics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    SettingToggleItem(
                        title = "Sound Effects",
                        description = "Play sounds for correct/incorrect answers",
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        checked = uiState.soundEnabled,
                        onCheckedChange = viewModel::onSoundToggle,
                        playPreview = true
                    )

                    SettingToggleItem(
                        title = "Vibration",
                        description = "Haptic feedback on interactions",
                        icon = Icons.Default.Vibration,
                        checked = uiState.hapticEnabled,
                        onCheckedChange = viewModel::onHapticToggle
                    )
                }
            }

            // Data Management Section
            ExpandableSection(
                title = SettingsSection.DATA.displayName,
                icon = SettingsSection.DATA.icon,
                isExpanded = uiState.expandedSections.contains(SettingsSection.DATA),
                onToggle = { viewModel.toggleSection(SettingsSection.DATA) }
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Manage your quiz data and progress",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    DangerButton(
                        text = "Clear History",
                        icon = Icons.Default.DeleteSweep,
                        onClick = viewModel::showClearHistoryDialog
                    )

                    DangerButton(
                        text = "Reset Progress",
                        icon = Icons.Default.RestartAlt,
                        onClick = viewModel::showResetProgressDialog
                    )
                }
            }

            // About Section
            ExpandableSection(
                title = SettingsSection.ABOUT.displayName,
                icon = SettingsSection.ABOUT.icon,
                isExpanded = uiState.expandedSections.contains(SettingsSection.ABOUT),
                onToggle = { viewModel.toggleSection(SettingsSection.ABOUT) }
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Version
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Version",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "1.0.0",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Developer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Developer",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "DevDen Team",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Rate App Button
                    PulsingButton(
                        text = "Rate App",
                        icon = Icons.Default.Star,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            // Open Play Store
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("market://details?id=${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    )

                    // Share App Button
                    OutlinedButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Check out Quizly - The best quiz app!")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Quizly"))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share App")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Clear History Dialog
    if (uiState.showClearHistoryDialog) {
        ConfirmationDialog(
            title = "Clear History?",
            message = "This will remove all your quiz history. Your progress and scores will remain.",
            confirmText = "Clear",
            onConfirm = {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                viewModel.onClearHistory()
            },
            onDismiss = viewModel::dismissClearHistoryDialog,
            isDestructive = true
        )
    }

    // Reset Progress Dialog
    if (uiState.showResetProgressDialog) {
        ConfirmationDialog(
            title = "Reset All Progress?",
            message = "⚠️ This will delete ALL your scores, streaks, and achievements. This action cannot be undone!",
            confirmText = "Reset Everything",
            onConfirm = {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                viewModel.onResetProgress()
            },
            onDismiss = viewModel::dismissResetProgressDialog,
            isDestructive = true
        )
    }
}

@Composable
private fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false,
    modifier: Modifier = Modifier
) {
    var shake by remember { mutableStateOf(false) }
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
                    delay(100)
                    shake = false
                }
            }
        },
        label = "dialog_shake"
    )

    LaunchedEffect(Unit) {
        shake = true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isDestructive) ErrorRed else ElectricBlue60
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier.graphicsLayer(translationX = offsetX)
    )
}

