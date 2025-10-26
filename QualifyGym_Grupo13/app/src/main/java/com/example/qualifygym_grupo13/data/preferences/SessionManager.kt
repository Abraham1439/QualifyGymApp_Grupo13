package com.example.qualifygym_grupo13.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de sesión usando SharedPreferences (más simple que DataStore)
 * Compatible con patrón MVVM
 */
class SessionManager(context: Context) {
    
    companion object {
        private const val PREF_NAME = "QualifyGymSession"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    /**
     * Guarda la sesión del usuario al iniciar sesión
     */
    fun saveUserSession(userId: Long, email: String) {
        prefs.edit().apply {
            putLong(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    /**
     * Obtiene el ID del usuario guardado
     */
    fun getUserId(): Long? {
        val userId = prefs.getLong(KEY_USER_ID, -1L)
        return if (userId != -1L) userId else null
    }
    
    /**
     * Obtiene el email del usuario guardado
     */
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
    
    /**
     * Verifica si hay una sesión activa
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Limpia la sesión del usuario al cerrar sesión
     */
    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            remove(KEY_IS_LOGGED_IN)
            apply()
        }
    }
}

