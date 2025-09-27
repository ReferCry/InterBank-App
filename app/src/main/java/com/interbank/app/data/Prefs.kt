package com.interbank.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para obtener un DataStore ligado al Contexto:
val Context.dataStore by preferencesDataStore(name = "app_prefs")

object Prefs {
    // Claves
    private val KEY_DNI = stringPreferencesKey("dni")
    private val KEY_HIDE_BALANCE = booleanPreferencesKey("hide_balance")

    // ----- DNI -----
    suspend fun saveDni(ctx: Context, dni: String) {
        ctx.dataStore.edit { it[KEY_DNI] = dni }
    }

    fun dniFlow(ctx: Context): Flow<String?> =
        ctx.dataStore.data.map { it[KEY_DNI] }

    // ----- (para Actividad 2) Ocultar/Mostrar saldo -----
    suspend fun setHideBalance(ctx: Context, hide: Boolean) {
        ctx.dataStore.edit { it[KEY_HIDE_BALANCE] = hide }
    }
    fun hideBalanceFlow(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[KEY_HIDE_BALANCE] ?: false }
}
