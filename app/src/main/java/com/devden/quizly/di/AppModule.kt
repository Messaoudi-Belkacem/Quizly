package com.devden.quizly.di

import android.content.Context
import com.devden.quizly.data.json.QuestionJsonParser
import com.devden.quizly.data.local.ScoreDataStore
import com.devden.quizly.data.local.database.QuestionDao
import com.devden.quizly.data.local.database.QuizDatabase
import com.devden.quizly.data.repository.QuizRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideQuizDatabase(@ApplicationContext context: Context): QuizDatabase {
        return QuizDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideQuestionDao(database: QuizDatabase): QuestionDao {
        return database.questionDao()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideQuestionJsonParser(
        @ApplicationContext context: Context,
        moshi: Moshi
    ): QuestionJsonParser {
        return QuestionJsonParser(context, moshi)
    }

    @Provides
    @Singleton
    fun provideQuizRepository(
        questionDao: QuestionDao,
        jsonParser: QuestionJsonParser,
        moshi: Moshi
    ): QuizRepository {
        return QuizRepository(questionDao, jsonParser, moshi)
    }

    @Provides
    @Singleton
    fun provideScoreDataStore(@ApplicationContext context: Context): ScoreDataStore {
        return ScoreDataStore(context)
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): com.devden.quizly.data.local.PreferencesDataStore {
        return com.devden.quizly.data.local.PreferencesDataStore(context)
    }

    @Provides
    @Singleton
    fun provideSoundManager(@ApplicationContext context: Context): com.devden.quizly.util.SoundManager {
        return com.devden.quizly.util.SoundManager(context)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): com.devden.quizly.util.NotificationManager {
        return com.devden.quizly.util.NotificationManager(context)
    }

    @Provides
    @Singleton
    fun provideReminderScheduler(@ApplicationContext context: Context): com.devden.quizly.util.ReminderScheduler {
        return com.devden.quizly.util.ReminderScheduler(context)
    }
}

