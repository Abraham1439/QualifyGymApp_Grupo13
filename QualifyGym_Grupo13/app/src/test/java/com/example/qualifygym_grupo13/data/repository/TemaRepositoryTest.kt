package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.TemaApi
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

//Test del microservicio de Temas
class TemaRepositoryTest {

    // ========== Tests para fetchTemas ==========//
    @Test
    fun fetchTemas_devuelve_lista_ok() = runBlocking {
        // Valida: Obtener lista de temas exitosamente
        // Retorna: Result.success con lista de temas
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)
        val temas = listOf(
            TemaDto(1, "Tema 1", 1)
        )

        coEvery { api.getTemas() } returns temas
        val result = repo.fetchTemas()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("Tema 1", result.getOrNull()!![0].nombreTema)
    }

    @Test
    fun fetchTemas_maneja_error() = runBlocking {
        // Valida: Manejo de error al obtener temas
        // Retorna: Result.failure
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)

        coEvery { api.getTemas() } throws Exception("Error de red")
        val result = repo.fetchTemas()
        assertTrue(result.isFailure)
    }

    // ========== Tests para fetchTemaById ==========//
    @Test
    fun fetchTemaById_devuelve_tema_ok() = runBlocking {
        // Valida: Obtener tema por ID exitosamente
        // Retorna: Result.success con TemaDto
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)
        val tema = TemaDto(1, "Tema 1", 1)

        coEvery { api.getTemaById(1) } returns tema
        val result = repo.fetchTemaById(1)
        assertTrue(result.isSuccess)
        assertEquals("Tema 1", result.getOrNull()!!.nombreTema)
    }

    // ========== Tests para fetchTemaPorNombre ==========//
    @Test
    fun fetchTemaPorNombre_devuelve_tema_ok() = runBlocking {
        // Valida: Obtener tema por nombre exitosamente
        // Retorna: Result.success con TemaDto
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)
        val tema = TemaDto(1, "Tema 1", 1)

        coEvery { api.getTemaPorNombre("Tema 1") } returns tema
        val result = repo.fetchTemaPorNombre("Tema 1")
        assertTrue(result.isSuccess)
        assertEquals("Tema 1", result.getOrNull()!!.nombreTema)
    }

    // ========== Tests para existeTemaPorNombre ==========//
    @Test
    fun existeTemaPorNombre_true() = runBlocking {
        // Valida: Verificar existencia de tema por nombre (existe)
        // Retorna: Result.success con true
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)

        coEvery { api.existeTemaPorNombre("Tema 1") } returns true
        val result = repo.existeTemaPorNombre("Tema 1")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!)
    }

    @Test
    fun existeTemaPorNombre_false() = runBlocking {
        // Valida: Verificar existencia de tema por nombre (no existe)
        // Retorna: Result.success con false
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)

        coEvery { api.existeTemaPorNombre("Tema Inexistente") } returns false
        val result = repo.existeTemaPorNombre("Tema Inexistente")
        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!)
    }

    // ========== Tests para fetchTemasPorEstado ==========//
    @Test
    fun fetchTemasPorEstado_devuelve_lista_ok() = runBlocking {
        // Valida: Obtener temas por estado exitosamente
        // Retorna: Result.success con lista de temas
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)
        val temas = listOf(
            TemaDto(1, "Tema 1", 1)
        )

        coEvery { api.getTemasPorEstado(1) } returns temas
        val result = repo.fetchTemasPorEstado(1)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    // ========== Tests para contarTemasPorEstado ==========//
    @Test
    fun contarTemasPorEstado_devuelve_count_ok() = runBlocking {
        // Valida: Contar temas por estado exitosamente
        // Retorna: Result.success con count
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)

        coEvery { api.contarTemasPorEstado(1) } returns 4L
        val result = repo.contarTemasPorEstado(1)
        assertTrue(result.isSuccess)
        assertEquals(4L, result.getOrNull())
    }

    // ========== Tests para buscarTemas ==========//
    @Test
    fun buscarTemas_devuelve_resultados() = runBlocking {
        // Valida: Buscar temas por query exitosamente
        // Retorna: Result.success con lista de temas encontrados
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)
        val temas = listOf(
            TemaDto(1, "Tema 1", 1)
        )

        coEvery { api.buscarTemas("Tema") } returns temas
        val result = repo.buscarTemas("Tema")
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    // ========== Tests para create ==========//
    @Test
    fun create_tema_exitoso() = runBlocking {
        // Valida: Crear tema exitosamente
        // Retorna: Result.success con TemaDto creado
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)
        val temaCreate = TemaCreateDto("Nuevo Tema", 1)
        val temaCreado = TemaDto(1, "Nuevo Tema", 1)

        coEvery { api.crearTema(temaCreate) } returns temaCreado
        val result = repo.create(temaCreate)
        assertTrue(result.isSuccess)
        assertEquals("Nuevo Tema", result.getOrNull()!!.nombreTema)
    }

    // ========== Tests para update ==========//
    @Test
    fun update_tema_exitoso() = runBlocking {
        // Valida: Actualizar tema exitosamente
        // Retorna: Result.success con TemaDto actualizado
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)
        val temaUpdate = TemaUpdateDto("Tema Actualizado", 1)
        val temaActualizado = TemaDto(1, "Tema Actualizado", 1)

        coEvery { api.actualizarTema(1, temaUpdate) } returns temaActualizado
        val result = repo.update(1, temaUpdate)
        assertTrue(result.isSuccess)
        assertEquals("Tema Actualizado", result.getOrNull()!!.nombreTema)
    }

    // ========== Tests para delete ==========//
    @Test
    fun delete_tema_exitoso() = runBlocking {
        // Valida: Eliminar tema exitosamente
        // Retorna: Result.success
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)
        val response = Response.success<Unit>(204, null)

        coEvery { api.eliminarTema(1) } returns response
        val result = repo.delete(1)
        assertTrue(result.isSuccess)
    }

    @Test
    fun delete_tema_error() = runBlocking {
        // Valida: Error al eliminar tema
        // Retorna: Result.failure
        val api = mockk<TemaApi>()
        val repo = TemaRepository(api)
        val errorResponse = Response.error<Unit>(404, "Not found".toResponseBody("application/json".toMediaType()))

        coEvery { api.eliminarTema(1) } returns errorResponse
        val result = repo.delete(1)
        assertTrue(result.isFailure)
    }
}

