package com.example.shelflife.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shelflife.ui.EXPIRING_SOON_DAYS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val shakeKey = booleanPreferencesKey("shake_to_open")
    private val warningDaysKey = intPreferencesKey("expiring_soon_days")

    val shakeEnabled: Flow<Boolean> = context.dataStore.data.map { it[shakeKey] ?: true }

    val warningDays: Flow<Int> = context.dataStore.data.map { it[warningDaysKey] ?: EXPIRING_SOON_DAYS }

    suspend fun setShakeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[shakeKey] = enabled }
    }

    suspend fun setWarningDays(days: Int) {
        context.dataStore.edit { it[warningDaysKey] = days }
    }
}
