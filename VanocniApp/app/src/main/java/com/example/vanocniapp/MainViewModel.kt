package com.example.vanocniapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(private val storeManager: StoreManager) : ViewModel() {

    val userName: StateFlow<String> = storeManager.userName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    val budgetLimit: StateFlow<Double> = storeManager.budgetLimit.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val giftItems: StateFlow<List<GiftItem>> = storeManager.giftItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val currentSpent: StateFlow<Double> = giftItems.map { items ->
        items.sumOf { it.price }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun saveUserName(name: String) {
        viewModelScope.launch { storeManager.saveUserName(name) }
    }

    fun saveBudgetLimit(limit: Double) {
        viewModelScope.launch { storeManager.saveBudgetLimit(limit) }
    }

    fun addGiftItem(name: String, price: Double) {
        viewModelScope.launch {
            val currentList = giftItems.value.toMutableList()
            currentList.add(GiftItem(id = UUID.randomUUID().toString(), name = name, price = price))
            storeManager.saveGiftItems(currentList)
        }
    }

    fun removeGiftItem(id: String) {
        viewModelScope.launch {
            val currentList = giftItems.value.filter { it.id != id }
            storeManager.saveGiftItems(currentList)
        }
    }

    fun clearAllGifts() {
        viewModelScope.launch {
            storeManager.saveGiftItems(emptyList())
        }
    }
}
