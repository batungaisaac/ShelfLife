package com.example.shelflife.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shelflife.ui.EXPIRING_SOON_DAYS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class Supermarket(val latitude: Double, val longitude: Double)

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val shakeKey = booleanPreferencesKey("shake_to_open")
    private val warningDaysKey = intPreferencesKey("expiring_soon_days")
    private val supermarketLatKey = doublePreferencesKey("supermarket_lat")
    private val supermarketLngKey = doublePreferencesKey("supermarket_lng")

    val shakeEnabled: Flow<Boolean> = context.dataStore.data.map { it[shakeKey] ?: true }

    val warningDays: Flow<Int> = context.dataStore.data.map { it[warningDaysKey] ?: EXPIRING_SOON_DAYS }

    // Null until the user saves one.
    val supermarket: Flow<Supermarket?> = context.dataStore.data.map { prefs ->
        val lat = prefs[supermarketLatKey]
        val lng = prefs[supermarketLngKey]
        if (lat != null && lng != null) Supermarket(lat, lng) else null
    }

    suspend fun setShakeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[shakeKey] = enabled }
    }

    suspend fun setWarningDays(days: Int) {
        context.dataStore.edit { it[warningDaysKey] = days }
    }

    suspend fun setSupermarket(latitude: Double, longitude: Double) {
        context.dataStore.edit {
            it[supermarketLatKey] = latitude
            it[supermarketLngKey] = longitude
        }
    }

    suspend fun clearSupermarket() {
        context.dataStore.edit {
            it.remove(supermarketLatKey)
            it.remove(supermarketLngKey)
        }
    }
}
