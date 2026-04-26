package com.example.myapplication.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.UserPreferences
import kotlinx.coroutines.launch

class FormViewModel(
    private val stateHandle: SavedStateHandle,
    private val userPrefs: UserPreferences
) : ViewModel() {
    // Estado que sobrevive a Muerte del Proceso (Process Death)
    var email by mutableStateOf(stateHandle.get<String>("email_key") ?: "")
        private set
    fun updateEmail(newEmail: String) {
        email = newEmail
        stateHandle["email_key"] = newEmail
    }
    // Estado que sobrevive a Cierre de App (DataStore)
    val nameFromDisk = userPrefs.userName.asLiveData()
    fun saveName(newName: String) {
        viewModelScope.launch { userPrefs.saveName(newName) }
    }
}