package com.devden.quizly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.devden.quizly.data.local.PreferencesDataStore
import com.devden.quizly.navigation.QuizlyNavGraph
import com.devden.quizly.ui.theme.QuizlyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesDataStore: PreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by preferencesDataStore.themeMode.collectAsState(initial = "SYSTEM")
            val systemInDarkTheme = isSystemInDarkTheme()

            val darkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> systemInDarkTheme // SYSTEM
            }

            QuizlyTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    QuizlyNavGraph(navController = navController)
                }
            }
        }
    }
}
