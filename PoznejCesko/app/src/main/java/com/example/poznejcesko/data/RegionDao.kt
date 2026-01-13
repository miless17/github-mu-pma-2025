package com.example.poznejcesko.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RegionDao {
    
    @Query("""
        SELECT 
            r.id, 
            r.name, 
            r.requiredScoreToUnlock, 
            r.`order`, 
            CASE 
                WHEN r.`order` = 1 THEN 0 
                WHEN urs.isUnlocked IS NOT NULL AND urs.isUnlocked = 1 THEN 0 
                ELSE 1 
            END as isLocked,
            IFNULL(urs.bestScore, 0) as bestScore,
            IFNULL(urs.maxPoints, 0) as maxPoints
        FROM regions r
        LEFT JOIN user_region_state urs ON r.id = urs.regionId AND urs.userId = :userId
        ORDER BY r.`order` ASC
    """)
    fun getRegionsWithState(userId: Int): Flow<List<RegionWithState>>
    
    @Query("SELECT * FROM regions ORDER BY `order` ASC")
    fun getAllRegions(): Flow<List<Region>>

    @Query("SELECT * FROM regions WHERE id = :regionId")
    suspend fun getRegion(regionId: Int): Region?
    
    @Query("SELECT * FROM regions WHERE `order` = :order LIMIT 1")
    suspend fun getRegionByOrder(order: Int): Region?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegions(regions: List<Region>)

    @Update
    suspend fun updateRegion(region: Region)
}
