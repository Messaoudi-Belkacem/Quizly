package com.devden.quizly

import android.app.Application
import android.util.Log
import com.devden.quizly.data.repository.QuizRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class QuizlyApplication : Application() {

    @Inject
    lateinit var quizRepository: QuizRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // Reload questions from JSON on every app startup
        applicationScope.launch {
            try {
                Log.d("QuizlyApplication", "Reloading questions from JSON file...")
                quizRepository.reloadQuestionsFromJson()
                Log.d("QuizlyApplication", "Questions reloaded successfully")
            } catch (e: Exception) {
                Log.e("QuizlyApplication", "Failed to reload questions", e)
            }
        }
    }
}
