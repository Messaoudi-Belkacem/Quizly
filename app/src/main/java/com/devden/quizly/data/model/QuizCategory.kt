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
        displayName = "Islams",
        icon = Icons.Default.Mosque,
        description = "Islamic knowledge"
    )
}


