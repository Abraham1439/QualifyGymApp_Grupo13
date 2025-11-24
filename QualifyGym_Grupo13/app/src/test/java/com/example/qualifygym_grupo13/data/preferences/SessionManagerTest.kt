package com.example.qualifygym_grupo13.data.preferences

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SessionManagerTest {

    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        sessionManager = SessionManager(RuntimeEnvironment.getApplication())
        sessionManager.clearSession() // Limpiar antes de cada test
    }

    // ========== Tests para saveUserSession ==========//
    @Test
    fun saveUserSession_guarda_datos_correctamente() {
        // Valida: Guardar sesión de usuario correctamente
        // Retorna: Datos guardados y isLoggedIn = true
        sessionManager.saveUserSession(1L, "test@example.com")
        
        assertEquals(1L, sessionManager.getUserId())
        assertEquals("test@example.com", sessionManager.getUserEmail())
        assertTrue(sessionManager.isLoggedIn())
    }

    // ========== Tests para getUserId ==========//
    @Test
    fun getUserId_devuelve_id_guardado() {
        // Valida: Obtener ID de usuario guardado
        // Retorna: ID correcto
        sessionManager.saveUserSession(123L, "test@example.com")
        assertEquals(123L, sessionManager.getUserId())
    }

    @Test
    fun getUserId_devuelve_null_sin_sesion() {
        // Valida: Obtener ID sin sesión guardada
        // Retorna: null
        assertNull(sessionManager.getUserId())
    }

    // ========== Tests para getUserEmail ==========//
    @Test
    fun getUserEmail_devuelve_email_guardado() {
        // Valida: Obtener email de usuario guardado
        // Retorna: Email correcto
        sessionManager.saveUserSession(1L, "test@example.com")
        assertEquals("test@example.com", sessionManager.getUserEmail())
    }

    @Test
    fun getUserEmail_devuelve_null_sin_sesion() {
        // Valida: Obtener email sin sesión guardada
        // Retorna: null
        assertNull(sessionManager.getUserEmail())
    }

    // ========== Tests para isLoggedIn ==========//
    @Test
    fun isLoggedIn_true_con_sesion_guardada() {
        // Valida: Verificar sesión activa con datos guardados
        // Retorna: true
        sessionManager.saveUserSession(1L, "test@example.com")
        assertTrue(sessionManager.isLoggedIn())
    }

    @Test
    fun isLoggedIn_false_sin_sesion() {
        // Valida: Verificar sesión activa sin datos guardados
        // Retorna: false
        assertFalse(sessionManager.isLoggedIn())
    }

    // ========== Tests para clearSession ==========//
    @Test
    fun clearSession_limpia_todos_los_datos() {
        // Valida: Limpiar sesión elimina todos los datos
        // Retorna: Todos los métodos devuelven valores por defecto
        sessionManager.saveUserSession(1L, "test@example.com")
        sessionManager.clearSession()
        
        assertNull(sessionManager.getUserId())
        assertNull(sessionManager.getUserEmail())
        assertFalse(sessionManager.isLoggedIn())
    }

    @Test
    fun clearSession_sobrescribe_sesion_anterior() {
        // Valida: Limpiar sesión y guardar nueva funciona correctamente
        // Retorna: Nueva sesión guardada correctamente
        sessionManager.saveUserSession(1L, "test1@example.com")
        sessionManager.clearSession()
        sessionManager.saveUserSession(2L, "test2@example.com")
        
        assertEquals(2L, sessionManager.getUserId())
        assertEquals("test2@example.com", sessionManager.getUserEmail())
        assertTrue(sessionManager.isLoggedIn())
    }
}

