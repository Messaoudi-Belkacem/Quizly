package com.devden.quizly.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class QuizCategory(
    val displayName: String,
    val icon: ImageVector,
    val description: String
) {
    SCIENCE(
        displayName = "Science",
        icon = Icons.Default.Science,
        description = "Test your knowledge of the natural world"
    ),
    HISTORY(
        displayName = "History",
        icon = Icons.Default.HistoryEdu,
        description = "Journey through time"
    ),
    GEOGRAPHY(
        displayName = "Geography",
        icon = Icons.Default.Public,
        description = "Explore the world"
    ),
    LITERATURE(
        displayName = "Literature",
        icon = Icons.AutoMirrored.Filled.MenuBook,
        description = "Books and authors"
    ),
    VIDEO_GAMES(
        displayName = "Video Games",
        icon = Icons.Default.SportsEsports,
        description = "Gaming knowledge"
    ),
    TECHNOLOGY(
        displayName = "Technology",
        icon = Icons.Default.Computer,
        description = "Tech and programming"
    ),
    SPORTS(
        displayName = "Sports",
        icon = Icons.Default.SportsSoccer,
        description = "Athletic competitions"
    ),
    FOOD(
        displayName = "Food & Cooking",
        icon = Icons.Default.Restaurant,
        description = "Culinary delights"
    ),
    ISLAM(
        displayName = "Islam",
        icon = Icons.Default.Mosque,
        description = "Islamic knowledge"
    ),
    AI_ETHICS(
        displayName = "AI Ethics",
        icon = Icons.Default.Android,
        description = "Ethical considerations in AI"
    ),
    RESPONSIBILITY_OF_SOCIAL_NETWORKS(
        displayName = "Responsibility of Social Networks",
        icon = Icons.Default.Share,
        description = "Social media ethics"
    ),
    ETHICS_IN_IOT(
        displayName = "Ethics in IoT",
        icon = Icons.Default.Wifi,
        description = "Ethical issues in Internet of Things"
    ),
    DIGITAL_REVOLUTION(
        displayName = "Digital Revolution",
        icon = Icons.Default.Memory,
        description = "Impact of digital technology"
    )
}


