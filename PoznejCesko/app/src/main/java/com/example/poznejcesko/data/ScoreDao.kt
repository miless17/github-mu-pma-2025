package com.example.poznejcesko.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Query("SELECT * FROM scores WHERE userId = :userId AND regionId = :regionId ORDER BY score DESC LIMIT 1")
    fun getBestScoreForRegion(userId: Int, regionId: Int): Flow<Score?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: Score)
    
    @Query("SELECT SUM(starsEarned) FROM scores WHERE userId = :userId")
    fun getTotalStars(userId: Int): Flow<Int?>
}
