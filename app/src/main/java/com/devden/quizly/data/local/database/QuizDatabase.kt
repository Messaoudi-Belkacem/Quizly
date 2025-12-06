package com.devden.quizly.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [QuestionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

