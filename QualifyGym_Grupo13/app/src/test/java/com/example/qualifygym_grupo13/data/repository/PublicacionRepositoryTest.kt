package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.PublicacionApi
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

//Test del microservicio de Publicaciones
class PublicacionRepositoryTest {

    // ========== Tests para fetchPublicaciones ==========//
    @Test
    fun fetchPublicaciones_devuelve_lista_ok() = runBlocking {
        // Valida: Obtener lista de publicaciones exitosamente
        // Retorna: Result.success con lista de publicaciones
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val publicaciones = listOf(
            PublicacionDto(1, "Probando", "Probando una nueva publi holi #Hola", "Título", false, "01-01-2024 10:00", null, 1, 1, null)
        )

        coEvery { api.getPublicaciones(false) } returns publicaciones

        val result = repo.fetchPublicaciones()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("Probando", result.getOrNull()!![0].titulo)
    }

    @Test
    fun fetchPublicaciones_maneja_error() = runBlocking {
        // Valida: Manejo de error al obtener publicaciones
        // Retorna: Result.failure
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)

        coEvery { api.getPublicaciones(false) } throws Exception("Error de red")
        val result = repo.fetchPublicaciones(false)
        assertTrue(result.isFailure)
    }

    // ========== Tests para fetchPublicacionById ==========//
    @Test
    fun fetchPublicacionById_devuelve_publicacion_ok() = runBlocking {
        // Valida: Obtener publicación por ID exitosamente
        // Retorna: Result.success con PublicacionDto
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val publicacion = PublicacionDto(1, "Título", "Contenido", "01-01-2024 10:00", false, null, null, 1, 1, null)

        coEvery { api.getPublicacionById(1) } returns publicacion
        val result = repo.fetchPublicacionById(1)
        assertTrue(result.isSuccess)
        assertEquals("Título", result.getOrNull()!!.titulo)
    }

    // ========== Tests para fetchPublicacionesPorTema ==========//
    @Test
    fun fetchPublicacionesPorTema_devuelve_lista_ok() = runBlocking {
        // Valida: Obtener publicaciones por tema exitosamente
        // Retorna: Result.success con lista de publicaciones
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val publicaciones = listOf(
            PublicacionDto(1, "Título", "Contenido", "01-01-2024 10:00", false, null, null, 1, 1, null)
        )

        coEvery { api.getPublicacionesPorTema(1, false) } returns publicaciones
        val result = repo.fetchPublicacionesPorTema(1, false)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    // ========== Tests para fetchPublicacionesPorUsuario ==========//
    @Test
    fun fetchPublicacionesPorUsuario_devuelve_lista_ok() = runBlocking {
        // Valida: Obtener publicaciones por usuario exitosamente
        // Retorna: Result.success con lista de publicaciones
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val publicaciones = listOf(
            PublicacionDto(1, "Título", "Contenido", "01-01-2024 10:00", false, null, null, 1, 1, null)
        )

        coEvery { api.getPublicacionesPorUsuario(1, false) } returns publicaciones
        val result = repo.fetchPublicacionesPorUsuario(1, false)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    // ========== Tests para contarPublicacionesPorTema ==========//
    @Test
    fun contarPublicacionesPorTema_devuelve_count_ok() = runBlocking {
        // Valida: Contar publicaciones por tema exitosamente
        // Retorna: Result.success con count
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)

        coEvery { api.contarPublicacionesPorTema(1) } returns 5L
        val result = repo.contarPublicacionesPorTema(1)
        assertTrue(result.isSuccess)
        assertEquals(5L, result.getOrNull())
    }

    // ========== Tests para contarPublicacionesPorUsuario ==========//
    @Test
    fun contarPublicacionesPorUsuario_devuelve_count_ok() = runBlocking {
        // Valida: Contar publicaciones por usuario exitosamente
        // Retorna: Result.success con count
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)

        coEvery { api.contarPublicacionesPorUsuario(1) } returns 3L
        val result = repo.contarPublicacionesPorUsuario(1)
        assertTrue(result.isSuccess)
        assertEquals(3L, result.getOrNull())
    }

    // ========== Tests para buscarPublicaciones ==========//
    @Test
    fun buscarPublicaciones_devuelve_resultados() = runBlocking {
        // Valida: Buscar publicaciones por query exitosamente
        // Retorna: Result.success con lista de publicaciones encontradas
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val publicaciones = listOf(
            PublicacionDto(1, "Título", "Contenido", "01-01-2024 10:00", false, null, null, 1, 1, null)
        )

        coEvery { api.buscarPublicaciones("Título") } returns publicaciones
        val result = repo.buscarPublicaciones("Título")
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    // ========== Tests para create ==========//
    @Test
    fun create_publicacion_exitosa() = runBlocking {
        // Valida: Crear publicación exitosamente
        // Retorna: Result.success con PublicacionDto creada
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val publicacionCreate = PublicacionCreateDto("Nuevo título", "Nuevo contenido", 1, 1, null)
        val publicacionCreada = PublicacionDto(1, "Nuevo título", "Nuevo contenido", "01-01-2024 10:00", false, null, null, 1, 1, null)

        coEvery { api.crearPublicacion(publicacionCreate) } returns publicacionCreada
        val result = repo.create(publicacionCreate)
        assertTrue(result.isSuccess)
        assertEquals("Nuevo título", result.getOrNull()!!.titulo)
    }

    // ========== Tests para update ==========//
    @Test
    fun update_publicacion_exitosa() = runBlocking {
        // Valida: Actualizar publicación exitosamente
        // Retorna: Result.success con PublicacionDto actualizada
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val publicacionUpdate = PublicacionUpdateDto("Título actualizado", "Contenido actualizado")
        val publicacionActualizada = PublicacionDto(1, "Título actualizado", "Contenido actualizado", "01-01-2024 10:00", false, null, null, 1, 1, null)

        coEvery { api.actualizarPublicacion(1, publicacionUpdate) } returns publicacionActualizada
        val result = repo.update(1, publicacionUpdate)
        assertTrue(result.isSuccess)
        assertEquals("Título actualizado", result.getOrNull()!!.titulo)
    }

    // ========== Tests para updateImagen ==========//
    @Test
    fun updateImagen_publicacion_exitosa() = runBlocking {
        // Valida: Actualizar imagen de publicación exitosamente
        // Retorna: Result.success con PublicacionDto actualizada
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val publicacionActualizada = PublicacionDto(1, "Título", "Contenido", "01-01-2024 10:00", false, null, null, 1, 1, "image123")

        coEvery { api.actualizarImagenPublicacion(1, any()) } returns publicacionActualizada
        val result = repo.updateImagen(1, "image123")
        assertTrue(result.isSuccess)
        assertEquals("image123", result.getOrNull()!!.imageUrl)
    }

    // ========== Tests para ocultar ==========//
    @Test
    fun ocultar_publicacion_exitosa() = runBlocking {
        // Valida: Ocultar publicación exitosamente
        // Retorna: Result.success con PublicacionDto oculta
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val publicacionOculta = PublicacionDto(1, "Título", "Contenido", "01-01-2024 10:00", true, "01-01-2024 11:00", "Spam", 1, 1, null)

        coEvery { api.ocultarPublicacion(1, any()) } returns publicacionOculta
        val result = repo.ocultar(1, "Spam")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.oculta)
        assertEquals("Spam", result.getOrNull()!!.motivoBaneo)
    }

    // ========== Tests para mostrar ==========//
    @Test
    fun mostrar_publicacion_exitosa() = runBlocking {
        // Valida: Mostrar publicación (desocultar) exitosamente
        // Retorna: Result.success con PublicacionDto visible
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val publicacionVisible = PublicacionDto(1, "Título", "Contenido", "01-01-2024 10:00", false, null, null, 1, 1, null)

        coEvery { api.mostrarPublicacion(1) } returns publicacionVisible
        val result = repo.mostrar(1)
        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!.oculta)
    }

    // ========== Tests para delete ==========//
    @Test
    fun delete_publicacion_exitosa() = runBlocking {
        // Valida: Eliminar publicación exitosamente
        // Retorna: Result.success
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val response = Response.success<Unit>(204, null)

        coEvery { api.eliminarPublicacion(1) } returns response
        val result = repo.delete(1)
        assertTrue(result.isSuccess)
    }

    @Test
    fun delete_publicacion_error() = runBlocking {
        // Valida: Error al eliminar publicación
        // Retorna: Result.failure
        val api = mockk<PublicacionApi>()
        val repo = PublicacionRepository(api)
        val errorResponse = Response.error<Unit>(404, "Not found".toResponseBody("application/json".toMediaType()))

        coEvery { api.eliminarPublicacion(1) } returns errorResponse
        val result = repo.delete(1)
        assertTrue(result.isFailure)
    }
}

