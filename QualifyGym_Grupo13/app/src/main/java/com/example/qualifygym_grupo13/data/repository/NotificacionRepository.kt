package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.domain.NotificacionDomain
import com.example.qualifygym_grupo13.data.remote.PublicacionApi
import com.example.qualifygym_grupo13.data.remote.dto.NotificacionDto

class NotificacionRepository(
    private val api: PublicacionApi
) {
    // Función de extensión para convertir DTO a Domain
    private fun NotificacionDto.toNotificacionDomain(): NotificacionDomain {
        return NotificacionDomain(
            idNotificacion = this.idNotificacion,
            usuarioId = this.usuarioId,
            publicacionId = this.publicacionId,
            mensaje = this.mensaje,
            fechaCreacion = this.fechaCreacion,
            leida = this.leida
        )
    }

    // Obtener todas las notificaciones de un usuario
    suspend fun obtenerNotificacionesPorUsuario(usuarioId: Long): Result<List<NotificacionDomain>> = try {
        val notificaciones = api.getNotificacionesPorUsuario(usuarioId)
        Result.success(notificaciones.map { it.toNotificacionDomain() })
    } catch (e: retrofit2.HttpException) {
        // Si es 404 (No Content), retornar lista vacía
        if (e.code() == 404) {
            Result.success(emptyList())
        } else {
            Result.failure(e)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtener notificaciones no leídas de un usuario
    suspend fun obtenerNotificacionesNoLeidasPorUsuario(usuarioId: Long): Result<List<NotificacionDomain>> = try {
        val notificaciones = api.getNotificacionesNoLeidasPorUsuario(usuarioId)
        Result.success(notificaciones.map { it.toNotificacionDomain() })
    } catch (e: retrofit2.HttpException) {
        // Si es 404 (No Content), retornar lista vacía
        if (e.code() == 404) {
            Result.success(emptyList())
        } else {
            Result.failure(e)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Contar notificaciones no leídas de un usuario
    suspend fun contarNotificacionesNoLeidas(usuarioId: Long): Result<Long> = try {
        val count = api.contarNotificacionesNoLeidas(usuarioId)
        Result.success(count)
    } catch (e: retrofit2.HttpException) {
        // Si es 404, retornar 0 en lugar de error (endpoint no existe o no hay notificaciones)
        if (e.code() == 404) {
            Result.success(0L)
        } else {
            Result.failure(e)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Marcar una notificación como leída
    suspend fun marcarComoLeida(notificacionId: Long): Result<NotificacionDomain> = try {
        val notificacion = api.marcarNotificacionComoLeida(notificacionId)
        Result.success(notificacion.toNotificacionDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Marcar todas las notificaciones de un usuario como leídas
    suspend fun marcarTodasComoLeidas(usuarioId: Long): Result<Unit> = try {
        api.marcarTodasComoLeidas(usuarioId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Eliminar una notificación
    suspend fun eliminarNotificacion(notificacionId: Long): Result<Unit> = try {
        api.eliminarNotificacion(notificacionId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

