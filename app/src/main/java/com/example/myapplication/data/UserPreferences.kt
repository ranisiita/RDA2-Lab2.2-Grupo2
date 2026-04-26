package com.example.myapplication.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")
class UserPreferences(private val context: Context) {
    private val NAME_KEY = stringPreferencesKey("user_name")
    // Flujo de datos para leer el nombre
    val userName: Flow<String> = context.dataStore.data.map { it[NAME_KEY] ?: "" }
    // Función para guardar el nombre en disco
    suspend fun saveName(name: String) {
        context.dataStore.edit { it[NAME_KEY] = name }
    }
}