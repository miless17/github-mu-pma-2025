package com.example.poznejcesko

import android.app.Application
import com.example.poznejcesko.data.AppDatabase
import com.example.poznejcesko.data.PoznejCeskoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PoznejCeskoApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { 
        PoznejCeskoRepository(
            database.userDao(), 
            database.regionDao(), 
            database.questionDao(), 
            database.scoreDao(),
            database.userRegionStateDao()
        ) 
    }
}
