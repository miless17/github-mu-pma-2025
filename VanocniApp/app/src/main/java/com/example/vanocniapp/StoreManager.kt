package com.example.vanocniapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class StoreManager(private val context: Context) {

    companion object {
        private val USER_NAME = stringPreferencesKey("user_name")
        private val BUDGET_LIMIT = doublePreferencesKey("budget_limit")
        private val GIFT_ITEMS = stringPreferencesKey("gift_items")
    }

    val userName: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences[USER_NAME] ?: "" }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences -> preferences[USER_NAME] = name }
    }

    val budgetLimit: Flow<Double> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences[BUDGET_LIMIT] ?: 0.0 }

    suspend fun saveBudgetLimit(limit: Double) {
        context.dataStore.edit { preferences -> preferences[BUDGET_LIMIT] = limit }
    }

    val giftItems: Flow<List<GiftItem>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val jsonString = preferences[GIFT_ITEMS] ?: "[]"
            try {
                Json.decodeFromString<List<GiftItem>>(jsonString)
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun saveGiftItems(items: List<GiftItem>) {
        val jsonString = Json.encodeToString(items)
        context.dataStore.edit { preferences ->
            preferences[GIFT_ITEMS] = jsonString
        }
    }
}
