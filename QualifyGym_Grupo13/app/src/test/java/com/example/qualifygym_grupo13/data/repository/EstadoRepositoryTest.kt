package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.EstadoApi
import com.example.qualifygym_grupo13.data.remote.dto.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import retrofit2.Response

//Test del microservicio de Estados
class EstadoRepositoryTest {

    // ========== Tests para fetchEstados ==========//
    @Test
    fun fetchEstados_devuelve_lista_ok() = runBlocking {
        // Valida: Obtener lista de estados exitosamente
        // Retorna: Result.success con lista de estados
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)
        val estados = listOf(
            EstadoDto(1, "Activo")
        )

        coEvery { api.getEstados() } returns estados
        val result = repo.fetchEstados()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("Activo", result.getOrNull()!![0].nombre)
    }

    @Test
    fun fetchEstados_maneja_error() = runBlocking {
        // Valida: Manejo de error al obtener estados
        // Retorna: Result.failure
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)

        coEvery { api.getEstados() } throws Exception("Error de red")
        val result = repo.fetchEstados()
        assertTrue(result.isFailure)
    }

    // ========== Tests para fetchEstadoById ==========//
    @Test
    fun fetchEstadoById_devuelve_estado_ok() = runBlocking {
        // Valida: Obtener estado por ID exitosamente
        // Retorna: Result.success con EstadoDto
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)
        val estado = EstadoDto(1, "Activo")

        coEvery { api.getEstadoById(1) } returns estado
        val result = repo.fetchEstadoById(1)
        assertTrue(result.isSuccess)
        assertEquals("Activo", result.getOrNull()!!.nombre)
    }

    // ========== Tests para fetchEstadoPorNombre ==========//
    @Test
    fun fetchEstadoPorNombre_devuelve_estado_ok() = runBlocking {
        // Valida: Obtener estado por nombre exitosamente
        // Retorna: Result.success con EstadoDto
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)
        val estado = EstadoDto(1, "Activo")

        coEvery { api.getEstadoPorNombre("Activo") } returns estado
        val result = repo.fetchEstadoPorNombre("Activo")
        assertTrue(result.isSuccess)
        assertEquals("Activo", result.getOrNull()!!.nombre)
    }

    // ========== Tests para existeEstadoPorNombre ==========//
    @Test
    fun existeEstadoPorNombre_true() = runBlocking {
        // Valida: Verificar existencia de estado por nombre (existe)
        // Retorna: Result.success con true
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)

        coEvery { api.existeEstadoPorNombre("Activo") } returns true
        val result = repo.existeEstadoPorNombre("Activo")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!)
    }

    @Test
    fun existeEstadoPorNombre_false() = runBlocking {
        // Valida: Verificar existencia de estado por nombre (no existe)
        // Retorna: Result.success con false
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)

        coEvery { api.existeEstadoPorNombre("Inexistente") } returns false
        val result = repo.existeEstadoPorNombre("Inexistente")
        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!)
    }

    // ========== Tests para create ==========//
    @Test
    fun create_estado_exitoso() = runBlocking {
        // Valida: Crear estado exitosamente
        // Retorna: Result.success con EstadoDto creado
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)
        val estadoCreate = EstadoCreateDto("Nuevo Estado")
        val estadoCreado = EstadoDto(1, "Nuevo Estado")

        coEvery { api.crearEstado(estadoCreate) } returns estadoCreado
        val result = repo.create(estadoCreate)
        assertTrue(result.isSuccess)
        assertEquals("Nuevo Estado", result.getOrNull()!!.nombre)
    }

    // ========== Tests para obtenerOCrearEstado ==========//
    @Test
    fun obtenerOCrearEstado_exitoso() = runBlocking {
        // Valida: Obtener o crear estado exitosamente
        // Retorna: Result.success con EstadoDto
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)
        val estadoCreate = EstadoCreateDto("Activo")
        val estado = EstadoDto(1, "Activo")

        coEvery { api.obtenerOCrearEstado(estadoCreate) } returns estado
        val result = repo.obtenerOCrearEstado(estadoCreate)
        assertTrue(result.isSuccess)
        assertEquals("Activo", result.getOrNull()!!.nombre)
    }

    // ========== Tests para update ==========//
    @Test
    fun update_estado_exitoso() = runBlocking {
        // Valida: Actualizar estado exitosamente
        // Retorna: Result.success con EstadoDto actualizado
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)
        val estadoUpdate = EstadoUpdateDto("Estado Actualizado")
        val estadoActualizado = EstadoDto(1, "Estado Actualizado")

        coEvery { api.actualizarEstado(1, estadoUpdate) } returns estadoActualizado
        val result = repo.update(1, estadoUpdate)
        assertTrue(result.isSuccess)
        assertEquals("Estado Actualizado", result.getOrNull()!!.nombre)
    }

    // ========== Tests para delete ==========//
    @Test
    fun delete_estado_exitoso() = runBlocking {
        // Valida: Eliminar estado exitosamente
        // Retorna: Result.success
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)
        val response = Response.success<Unit>(204, null)

        coEvery { api.eliminarEstado(1) } returns response
        val result = repo.delete(1)
        assertTrue(result.isSuccess)
    }

    @Test
    fun delete_estado_error() = runBlocking {
        // Valida: Error al eliminar estado
        // Retorna: Result.failure
        val api = mockk<EstadoApi>()
        val repo = EstadoRepository(api)
        val errorResponse = Response.error<Unit>(404, "Not found".toResponseBody("application/json".toMediaType()))

        coEvery { api.eliminarEstado(1) } returns errorResponse
        val result = repo.delete(1)
        assertTrue(result.isFailure)
    }
}

