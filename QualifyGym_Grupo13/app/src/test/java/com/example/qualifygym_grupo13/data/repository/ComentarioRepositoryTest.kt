package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.ComentarioApi
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

//Test del microservicio de Comentarios
class ComentarioRepositoryTest {

    // ========== Tests para fetchComentarios ==========//
    @Test
    fun fetchComentarios_devuelve_lista_ok() = runBlocking {
        // Valida: Obtener lista de comentarios exitosamente
        // Retorna: Result.success con lista de comentarios
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)
        val comentarios = listOf(
            ComentarioDto(1, "Comentario 1", "01-01-2024 10:00", false, null, null, 1, 1)
        )

        coEvery { api.getComentarios() } returns comentarios
        val result = repo.fetchComentarios()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("Comentario 1", result.getOrNull()!![0].comentario)
    }

    @Test
    fun fetchComentarios_maneja_error() = runBlocking {
        // Valida: Manejo de error al obtener comentarios
        // Retorna: Result.failure
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)

        coEvery { api.getComentarios() } throws Exception("Error de red")
        val result = repo.fetchComentarios()
        assertTrue(result.isFailure)
    }

    // ========== Tests para fetchComentarioById ==========//
    @Test
    fun fetchComentarioById_devuelve_comentario_ok() = runBlocking {
        // Valida: Obtener comentario por ID exitosamente
        // Retorna: Result.success con ComentarioDto
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)
        val comentario = ComentarioDto(1, "Comentario 1", "01-01-2024 10:00", false, null, null, 1, 1)

        coEvery { api.getComentarioById(1) } returns comentario
        val result = repo.fetchComentarioById(1)
        assertTrue(result.isSuccess)
        assertEquals("Comentario 1", result.getOrNull()!!.comentario)
    }

    @Test
    fun fetchComentarioById_maneja_error() = runBlocking {
        // Valida: Manejo de error al obtener comentario por ID
        // Retorna: Result.failure
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)

        coEvery { api.getComentarioById(1) } throws Exception("Comentario no encontrado")
        val result = repo.fetchComentarioById(1)
        assertTrue(result.isFailure)
    }

    // ========== Tests para fetchComentariosPorPublicacion ==========//
    @Test
    fun fetchComentariosPorPublicacion_devuelve_lista_ok() = runBlocking {
        // Valida: Obtener comentarios por publicación exitosamente
        // Retorna: Result.success con lista de comentarios
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)
        val comentarios = listOf(
            ComentarioDto(1, "Comentario 1", "01-01-2024 10:00", false, null, null, 1, 1)
        )

        coEvery { api.getComentariosPorPublicacion(1, false) } returns comentarios
        val result = repo.fetchComentariosPorPublicacion(1, false)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    // ========== Tests para fetchComentariosPorUsuario ==========//
    @Test
    fun fetchComentariosPorUsuario_devuelve_lista_ok() = runBlocking {
        // Valida: Obtener comentarios por usuario exitosamente
        // Retorna: Result.success con lista de comentarios
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)
        val comentarios = listOf(
            ComentarioDto(1, "Comentario 1", "01-01-2024 10:00", false, null, null, 1, 1)
        )

        coEvery { api.getComentariosPorUsuario(1) } returns comentarios
        val result = repo.fetchComentariosPorUsuario(1)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    // ========== Tests para contarComentariosPorPublicacion ==========//
    @Test
    fun contarComentariosPorPublicacion_devuelve_count_ok() = runBlocking {
        // Valida: Contar comentarios por publicación exitosamente
        // Retorna: Result.success con count
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)

        coEvery { api.contarComentariosPorPublicacion(1) } returns 7L
        val result = repo.contarComentariosPorPublicacion(1)
        assertTrue(result.isSuccess)
        assertEquals(7L, result.getOrNull())
    }

    // ========== Tests para contarComentariosPorUsuario ==========//
    @Test
    fun contarComentariosPorUsuario_devuelve_count_ok() = runBlocking {
        // Valida: Contar comentarios por usuario exitosamente
        // Retorna: Result.success con count
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)

        coEvery { api.contarComentariosPorUsuario(1) } returns 5L
        val result = repo.contarComentariosPorUsuario(1)
        assertTrue(result.isSuccess)
        assertEquals(5L, result.getOrNull())
    }

    // ========== Tests para create ==========//
    @Test
    fun create_comentario_exitoso() = runBlocking {
        // Valida: Crear comentario exitosamente
        // Retorna: Result.success con ComentarioDto creado
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)
        val comentarioCreate = ComentarioCreateDto("Nuevo comentario", 1, 1)
        val comentarioCreado = ComentarioDto(1, "Nuevo comentario", "01-01-2024 10:00", false, null, null, 1, 1)

        coEvery { api.crearComentario(comentarioCreate) } returns comentarioCreado
        val result = repo.create(comentarioCreate)
        assertTrue(result.isSuccess)
        assertEquals("Nuevo comentario", result.getOrNull()!!.comentario)
    }

    // ========== Tests para update ==========//
    @Test
    fun update_comentario_exitoso() = runBlocking {
        // Valida: Actualizar comentario exitosamente
        // Retorna: Result.success con ComentarioDto actualizado
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)
        val comentarioUpdate = ComentarioUpdateDto("Comentario actualizado")
        val comentarioActualizado = ComentarioDto(1, "Comentario actualizado", "01-01-2024 10:00", false, null, null, 1, 1)

        coEvery { api.actualizarComentario(1, comentarioUpdate) } returns comentarioActualizado
        val result = repo.update(1, comentarioUpdate)
        assertTrue(result.isSuccess)
        assertEquals("Comentario actualizado", result.getOrNull()!!.comentario)
    }

    // ========== Tests para ocultar ==========//
    @Test
    fun ocultar_comentario_exitoso() = runBlocking {
        // Valida: Ocultar comentario exitosamente
        // Retorna: Result.success con ComentarioDto oculto
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)
        val comentarioOculto = ComentarioDto(1, "Comentario", "01-01-2024 10:00", true, "01-01-2024 11:00", "Spam", 1, 1)

        coEvery { api.ocultarComentario(1, any()) } returns comentarioOculto
        val result = repo.ocultar(1, "Spam")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.oculto)
        assertEquals("Spam", result.getOrNull()!!.motivoBaneo)
    }

    // ========== Tests para mostrar ==========//
    @Test
    fun mostrar_comentario_exitoso() = runBlocking {
        // Valida: Mostrar comentario (desocultar) exitosamente
        // Retorna: Result.success con ComentarioDto visible
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)
        val comentarioVisible = ComentarioDto(1, "Comentario", "01-01-2024 10:00", false, null, null, 1, 1)

        coEvery { api.mostrarComentario(1) } returns comentarioVisible
        val result = repo.mostrar(1)
        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!.oculto)
    }

    // ========== Tests para delete ==========//
    @Test
    fun delete_comentario_exitoso() = runBlocking {
        // Valida: Eliminar comentario exitosamente
        // Retorna: Result.success
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)
        val response = Response.success<Unit>(204, null)

        coEvery { api.eliminarComentario(1) } returns response
        val result = repo.delete(1)
        assertTrue(result.isSuccess)
    }

    @Test
    fun delete_comentario_error() = runBlocking {
        // Valida: Error al eliminar comentario
        // Retorna: Result.failure
        val api = mockk<ComentarioApi>()
        val repo = ComentarioRepository(api)
        val errorResponse = Response.error<Unit>(404, "Not found".toResponseBody("application/json".toMediaType()))

        coEvery { api.eliminarComentario(1) } returns errorResponse
        val result = repo.delete(1)
        assertTrue(result.isFailure)
    }
}

