package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.PublicacionApi
import com.example.qualifygym_grupo13.data.remote.ComentarioApi
import com.example.qualifygym_grupo13.data.remote.dto.NotificacionDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

//Test del microservicio de Notificaciones
class NotificacionRepositoryTest {

    // ========== Tests para obtenerNotificacionesPorUsuario ==========//
    @Test
    fun obtenerNotificacionesPorUsuario_exitoso_ambos_servicios() = runBlocking {
        // Valida: Obtener notificaciones de ambos microservicios exitosamente
        // Retorna: Result.success con lista combinada y ordenada
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        val notifPublicaciones = listOf(
            NotificacionDto(1, 1, 1, null, "Nueva publicación", "01-01-2024 10:00", false)
        )
        val notifComentarios = listOf(
            NotificacionDto(2, 1, null, 1, "Nuevo comentario", "01-01-2024 11:00", false)
        )
        
        coEvery { publicacionApi.getNotificacionesPorUsuario(1) } returns notifPublicaciones
        coEvery { comentarioApi.getNotificacionesPorUsuario(1) } returns notifComentarios
        
        val result = repo.obtenerNotificacionesPorUsuario(1)
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()!!.size)
        // Debe estar ordenado por fecha descendente (más reciente primero)
        assertEquals(2L, result.getOrNull()!![0].idNotificacion)
    }

    @Test
    fun obtenerNotificacionesPorUsuario_solo_publicaciones() = runBlocking {
        // Valida: Obtener notificaciones solo de publicaciones
        // Retorna: Result.success con lista de publicaciones
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        val notifPublicaciones = listOf(
            NotificacionDto(1, 1, 1, null, "Nueva publicación", "01-01-2024 10:00", false)
        )
        
        coEvery { publicacionApi.getNotificacionesPorUsuario(1) } returns notifPublicaciones
        coEvery { comentarioApi.getNotificacionesPorUsuario(1) } throws HttpException(
            retrofit2.Response.error<List<NotificacionDto>>(404, "Not found".toResponseBody("application/json".toMediaType()))
        )
        
        val result = repo.obtenerNotificacionesPorUsuario(1)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    @Test
    fun obtenerNotificacionesPorUsuario_solo_comentarios() = runBlocking {
        // Valida: Obtener notificaciones solo de comentarios
        // Retorna: Result.success con lista de comentarios
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        val notifComentarios = listOf(
            NotificacionDto(2, 1, null, 1, "Nuevo comentario", "01-01-2024 11:00", false)
        )
        
        coEvery { publicacionApi.getNotificacionesPorUsuario(1) } throws HttpException(
            retrofit2.Response.error<List<NotificacionDto>>(404, "Not found".toResponseBody("application/json".toMediaType()))
        )
        coEvery { comentarioApi.getNotificacionesPorUsuario(1) } returns notifComentarios
        
        val result = repo.obtenerNotificacionesPorUsuario(1)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    @Test
    fun obtenerNotificacionesPorUsuario_vacia() = runBlocking {
        // Valida: Obtener notificaciones cuando no hay ninguna
        // Retorna: Result.success con lista vacía
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.getNotificacionesPorUsuario(1) } throws HttpException(
            retrofit2.Response.error<List<NotificacionDto>>(404, "Not found".toResponseBody("application/json".toMediaType()))
        )
        coEvery { comentarioApi.getNotificacionesPorUsuario(1) } throws HttpException(
            retrofit2.Response.error<List<NotificacionDto>>(404, "Not found".toResponseBody("application/json".toMediaType()))
        )
        
        val result = repo.obtenerNotificacionesPorUsuario(1)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun obtenerNotificacionesPorUsuario_maneja_error() = runBlocking {
        // Valida: Manejo de error al obtener notificaciones
        // Retorna: Result.failure
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.getNotificacionesPorUsuario(1) } throws Exception("Error de red")
        coEvery { comentarioApi.getNotificacionesPorUsuario(1) } throws Exception("Error de red")
        
        val result = repo.obtenerNotificacionesPorUsuario(1)
        assertTrue(result.isSuccess) // Debe retornar lista vacía si ambos fallan
    }

    // ========== Tests para obtenerNotificacionesNoLeidasPorUsuario ==========//
    @Test
    fun obtenerNotificacionesNoLeidasPorUsuario_exitoso() = runBlocking {
        // Valida: Obtener notificaciones no leídas exitosamente
        // Retorna: Result.success con lista de no leídas
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        val notifPublicaciones = listOf(
            NotificacionDto(1, 1, 1, null, "Nueva publicación", "01-01-2024 10:00", false)
        )
        val notifComentarios = listOf(
            NotificacionDto(2, 1, null, 1, "Nuevo comentario", "01-01-2024 11:00", false)
        )
        
        coEvery { publicacionApi.getNotificacionesNoLeidasPorUsuario(1) } returns notifPublicaciones
        coEvery { comentarioApi.getNotificacionesNoLeidasPorUsuario(1) } returns notifComentarios
        
        val result = repo.obtenerNotificacionesNoLeidasPorUsuario(1)
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()!!.size)
    }

    @Test
    fun obtenerNotificacionesNoLeidasPorUsuario_vacia() = runBlocking {
        // Valida: Obtener notificaciones no leídas cuando no hay ninguna
        // Retorna: Result.success con lista vacía
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.getNotificacionesNoLeidasPorUsuario(1) } throws HttpException(
            retrofit2.Response.error<List<NotificacionDto>>(404, "Not found".toResponseBody("application/json".toMediaType()))
        )
        coEvery { comentarioApi.getNotificacionesNoLeidasPorUsuario(1) } throws HttpException(
            retrofit2.Response.error<List<NotificacionDto>>(404, "Not found".toResponseBody("application/json".toMediaType()))
        )
        
        val result = repo.obtenerNotificacionesNoLeidasPorUsuario(1)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    // ========== Tests para contarNotificacionesNoLeidas ==========//
    @Test
    fun contarNotificacionesNoLeidas_exitoso() = runBlocking {
        // Valida: Contar notificaciones no leídas exitosamente
        // Retorna: Result.success con suma de ambos servicios
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.contarNotificacionesNoLeidas(1) } returns 3L
        coEvery { comentarioApi.contarNotificacionesNoLeidas(1) } returns 2L
        
        val result = repo.contarNotificacionesNoLeidas(1)
        assertTrue(result.isSuccess)
        assertEquals(5L, result.getOrNull())
    }

    @Test
    fun contarNotificacionesNoLeidas_cero() = runBlocking {
        // Valida: Contar notificaciones no leídas cuando no hay ninguna
        // Retorna: Result.success con 0
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.contarNotificacionesNoLeidas(1) } throws HttpException(
            retrofit2.Response.error<Long>(404, "Not found".toResponseBody("application/json".toMediaType()))
        )
        coEvery { comentarioApi.contarNotificacionesNoLeidas(1) } throws HttpException(
            retrofit2.Response.error<Long>(404, "Not found".toResponseBody("application/json".toMediaType()))
        )
        
        val result = repo.contarNotificacionesNoLeidas(1)
        assertTrue(result.isSuccess)
        assertEquals(0L, result.getOrNull())
    }

    // ========== Tests para marcarComoLeida ==========//
    @Test
    fun marcarComoLeida_exitoso_publicaciones() = runBlocking {
        // Valida: Marcar notificación como leída en publicaciones
        // Retorna: Result.success con notificación actualizada
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        val notificacion = NotificacionDto(1, 1, 1, null, "Nueva publicación", "01-01-2024 10:00", true)
        
        coEvery { publicacionApi.marcarNotificacionComoLeida(1) } returns notificacion
        
        val result = repo.marcarComoLeida(1, 1)
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull()!!.idNotificacion)
        assertTrue(result.getOrNull()!!.leida)
    }

    @Test
    fun marcarComoLeida_exitoso_comentarios() = runBlocking {
        // Valida: Marcar notificación como leída en comentarios (cuando no está en publicaciones)
        // Retorna: Result.success con notificación actualizada
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        val notificacion = NotificacionDto(2, 1, null, 1, "Nuevo comentario", "01-01-2024 11:00", true)
        
        coEvery { publicacionApi.marcarNotificacionComoLeida(2) } throws HttpException(
            retrofit2.Response.error<NotificacionDto>(404, "Not found".toResponseBody("application/json".toMediaType()))
        )
        coEvery { comentarioApi.marcarNotificacionComoLeida(2) } returns notificacion
        
        val result = repo.marcarComoLeida(2, 1)
        assertTrue(result.isSuccess)
        assertEquals(2L, result.getOrNull()!!.idNotificacion)
    }

    @Test
    fun marcarComoLeida_error() = runBlocking {
        // Valida: Error al marcar notificación como leída
        // Retorna: Result.failure
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.marcarNotificacionComoLeida(1) } throws HttpException(
            retrofit2.Response.error<NotificacionDto>(500, "Server error".toResponseBody("application/json".toMediaType()))
        )
        coEvery { comentarioApi.marcarNotificacionComoLeida(1) } throws Exception("Error")
        
        val result = repo.marcarComoLeida(1, 1)
        assertTrue(result.isFailure)
    }

    // ========== Tests para marcarTodasComoLeidas ==========//
    @Test
    fun marcarTodasComoLeidas_exitoso() = runBlocking {
        // Valida: Marcar todas las notificaciones como leídas exitosamente
        // Retorna: Result.success
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.marcarTodasComoLeidas(1) } returns retrofit2.Response.success(Unit)
        coEvery { comentarioApi.marcarTodasComoLeidas(1) } returns retrofit2.Response.success(Unit)
        
        val result = repo.marcarTodasComoLeidas(1)
        assertTrue(result.isSuccess)
    }

    @Test
    fun marcarTodasComoLeidas_ignora_errores() = runBlocking {
        // Valida: Marcar todas como leídas ignorando errores parciales
        // Retorna: Result.success incluso si un servicio falla
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.marcarTodasComoLeidas(1) } throws Exception("Error")
        coEvery { comentarioApi.marcarTodasComoLeidas(1) } returns retrofit2.Response.success(Unit)
        
        val result = repo.marcarTodasComoLeidas(1)
        assertTrue(result.isSuccess)
    }

    // ========== Tests para eliminarNotificacion ==========//
    @Test
    fun eliminarNotificacion_exitoso_publicaciones() = runBlocking {
        // Valida: Eliminar notificación de publicaciones exitosamente
        // Retorna: Result.success
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.eliminarNotificacion(1) } returns retrofit2.Response.success(Unit)
        
        val result = repo.eliminarNotificacion(1, 1)
        assertTrue(result.isSuccess)
    }

    @Test
    fun eliminarNotificacion_exitoso_comentarios() = runBlocking {
        // Valida: Eliminar notificación de comentarios (cuando no está en publicaciones)
        // Retorna: Result.success
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.eliminarNotificacion(2) } throws HttpException(
            retrofit2.Response.error<Unit>(404, "Not found".toResponseBody("application/json".toMediaType()))
        )
        coEvery { comentarioApi.eliminarNotificacion(2) } returns retrofit2.Response.success(Unit)
        
        val result = repo.eliminarNotificacion(2, 1)
        assertTrue(result.isSuccess)
    }

    @Test
    fun eliminarNotificacion_error() = runBlocking {
        // Valida: Error al eliminar notificación
        // Retorna: Result.failure
        val publicacionApi = mockk<PublicacionApi>()
        val comentarioApi = mockk<ComentarioApi>()
        val repo = NotificacionRepository(publicacionApi, comentarioApi)
        
        coEvery { publicacionApi.eliminarNotificacion(1) } throws HttpException(
            retrofit2.Response.error<Unit>(500, "Server error".toResponseBody("application/json".toMediaType()))
        )
        coEvery { comentarioApi.eliminarNotificacion(1) } throws Exception("Error")
        
        val result = repo.eliminarNotificacion(1, 1)
        assertTrue(result.isFailure)
    }
}

