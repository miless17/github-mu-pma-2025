package com.example.poznejcesko

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.poznejcesko.data.PoznejCeskoRepository
import com.example.poznejcesko.data.RegionWithState
import com.example.poznejcesko.data.User
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PoznejCeskoRepository) : ViewModel() {

    val allUsers: LiveData<List<User>> = repository.allUsers.asLiveData()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    // When currentUser changes, reload regions with state for that user
    val allRegions: LiveData<List<RegionWithState>> = _currentUser.switchMap { user ->
        if (user != null) {
            repository.getRegionsForUser(user.id).asLiveData()
        } else {
            // Return empty list or default locked list if no user
            MutableLiveData(emptyList()) 
        }
    }

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    fun createNewUser(name: String) = viewModelScope.launch {
        val newUser = User(name = name)
        val newId = repository.insertUser(newUser)
        _currentUser.value = newUser.copy(id = newId.toInt())
    }
}

class MainViewModelFactory(private val repository: PoznejCeskoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
