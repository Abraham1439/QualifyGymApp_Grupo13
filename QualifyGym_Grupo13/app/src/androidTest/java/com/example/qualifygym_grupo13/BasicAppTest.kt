package com.example.qualifygym_grupo13

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Test básico para verificar que la aplicación funciona correctamente
 */
@RunWith(AndroidJUnit4::class)
class BasicAppTest {

    @Test
    fun testAppContext() {
        // Valida: El contexto de la aplicación no es null
        // Retorna: Context válido con package name correcto
        val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull("El contexto de la aplicación no debería ser null", appContext)
        assertEquals("com.example.qualifygym_grupo13", appContext.packageName)
    }

    @Test
    fun testApplicationContext() {
        // Valida: El ApplicationContext está disponible
        // Retorna: ApplicationContext no null
        val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val applicationContext = appContext.applicationContext
        assertNotNull("El ApplicationContext no debería ser null", applicationContext)
        assertEquals("com.example.qualifygym_grupo13", applicationContext.packageName)
    }

    @Test
    fun testContextResources() {
        // Valida: Los recursos de la aplicación están disponibles
        // Retorna: Resources no null
        val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val resources = appContext.resources
        assertNotNull("Los recursos no deberían ser null", resources)
        assertTrue("Debería haber recursos disponibles", resources.displayMetrics.density > 0)
    }
}

