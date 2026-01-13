package com.example.poznejcesko.data

import kotlinx.coroutines.flow.Flow

class PoznejCeskoRepository(
    private val userDao: UserDao,
    private val regionDao: RegionDao,
    private val questionDao: QuestionDao,
    private val scoreDao: ScoreDao,
    private val userRegionStateDao: UserRegionStateDao
) {
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    fun getRegionsForUser(userId: Int): Flow<List<RegionWithState>> {
        return regionDao.getRegionsWithState(userId)
    }

    suspend fun getQuestionsForRegion(regionId: Int): List<Question> {
        return questionDao.getQuestionsForRegion(regionId)
    }
    
    suspend fun getRegion(regionId: Int): Region? {
        return regionDao.getRegion(regionId)
    }
    
    suspend fun getRegionByOrder(order: Int): Region? {
        return regionDao.getRegionByOrder(order)
    }

    suspend fun insertScore(score: Score) {
        scoreDao.insertScore(score)
    }

    suspend fun insertUser(user: User): Long {
        val userId = userDao.insertUser(user)
        val firstRegion = regionDao.getRegionByOrder(1)
        if (firstRegion != null) {
            unlockRegionForUser(userId.toInt(), firstRegion.id)
        }
        return userId
    }
    
    suspend fun unlockRegionForUser(userId: Int, regionId: Int) {
        val currentState = userRegionStateDao.getState(userId, regionId)
        if (currentState == null) {
            val state = UserRegionState(userId, regionId, isUnlocked = true)
            userRegionStateDao.insertOrUpdate(state)
        } else if (!currentState.isUnlocked) {
            val state = currentState.copy(isUnlocked = true)
            userRegionStateDao.insertOrUpdate(state)
        }
    }

    suspend fun getUserRegionState(userId: Int, regionId: Int): UserRegionState? {
        return userRegionStateDao.getState(userId, regionId)
    }

    suspend fun updateUserRegionState(state: UserRegionState) {
        userRegionStateDao.insertOrUpdate(state)
    }
}
