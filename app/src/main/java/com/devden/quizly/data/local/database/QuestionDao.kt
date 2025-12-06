package com.devden.quizly.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId")
    fun getQuestionsByCategory(categoryId: Int): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId AND difficulty = :difficulty")
    fun getQuestionsByCategoryAndDifficulty(categoryId: Int, difficulty: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :questionId")
    suspend fun getQuestionById(questionId: String): QuestionEntity?

    @Query("SELECT COUNT(*) FROM questions WHERE categoryId = :categoryId")
    suspend fun getQuestionCountByCategory(categoryId: Int): Int

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getTotalQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("DELETE FROM questions WHERE categoryId = :categoryId")
    suspend fun deleteQuestionsByCategory(categoryId: Int)

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Transaction
    suspend fun clearAndInsert(questions: List<QuestionEntity>) {
        deleteAllQuestions()
        insertAll(questions)
    }
}

