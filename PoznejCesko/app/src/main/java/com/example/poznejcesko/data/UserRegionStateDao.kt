package com.example.poznejcesko.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserRegionStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(state: UserRegionState)

    @Query("SELECT * FROM user_region_state WHERE userId = :userId AND regionId = :regionId")
    suspend fun getState(userId: Int, regionId: Int): UserRegionState?
}
