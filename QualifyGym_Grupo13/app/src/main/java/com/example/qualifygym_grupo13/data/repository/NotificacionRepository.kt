package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.domain.NotificacionDomain
import com.example.qualifygym_grupo13.data.remote.PublicacionApi
import com.example.qualifygym_grupo13.data.remote.ComentarioApi
import com.example.qualifygym_grupo13.data.remote.dto.NotificacionDto
import java.text.SimpleDateFormat
import java.util.*

class NotificacionRepository(
    private val publicacionApi: PublicacionApi,
    private val comentarioApi: ComentarioApi
) {
    // Función de extensión para convertir DTO a Domain
    private fun NotificacionDto.toNotificacionDomain(): NotificacionDomain {
        return NotificacionDomain(
            idNotificacion = this.idNotificacion,
            usuarioId = this.usuarioId,
            publicacionId = this.publicacionId,
            comentarioId = this.comentarioId,
            mensaje = this.mensaje,
            fechaCreacion = this.fechaCreacion,
            leida = this.leida
        )
    }
    
    // Función auxiliar para parsear fecha y ordenar
    private fun parseDate(dateString: String): Date? {
        return try {
            SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    // Obtener todas las notificaciones de un usuario (de ambos microservicios)
    suspend fun obtenerNotificacionesPorUsuario(usuarioId: Long): Result<List<NotificacionDomain>> = try {
        val notificacionesPublicaciones = try {
            publicacionApi.getNotificacionesPorUsuario(usuarioId)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) emptyList() else throw e
        } catch (e: Exception) {
            emptyList() // Si falla, continuar con las de comentarios
        }
        
        val notificacionesComentarios = try {
            comentarioApi.getNotificacionesPorUsuario(usuarioId)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) emptyList() else throw e
        } catch (e: Exception) {
            emptyList() // Si falla, continuar con las de publicaciones
        }
        
        // Combinar ambas listas y ordenar por fecha descendente
        val todasLasNotificaciones = (notificacionesPublicaciones + notificacionesComentarios)
            .map { it.toNotificacionDomain() }
            .sortedByDescending { parseDate(it.fechaCreacion) ?: Date(0) }
        
        Result.success(todasLasNotificaciones)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtener notificaciones no leídas de un usuario (de ambos microservicios)
    suspend fun obtenerNotificacionesNoLeidasPorUsuario(usuarioId: Long): Result<List<NotificacionDomain>> = try {
        val notificacionesPublicaciones = try {
            publicacionApi.getNotificacionesNoLeidasPorUsuario(usuarioId)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) emptyList() else throw e
        } catch (e: Exception) {
            emptyList()
        }
        
        val notificacionesComentarios = try {
            comentarioApi.getNotificacionesNoLeidasPorUsuario(usuarioId)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) emptyList() else throw e
        } catch (e: Exception) {
            emptyList()
        }
        
        // Combinar ambas listas y ordenar por fecha descendente
        val todasLasNotificaciones = (notificacionesPublicaciones + notificacionesComentarios)
            .map { it.toNotificacionDomain() }
            .sortedByDescending { parseDate(it.fechaCreacion) ?: Date(0) }
        
        Result.success(todasLasNotificaciones)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Contar notificaciones no leídas de un usuario (de ambos microservicios)
    suspend fun contarNotificacionesNoLeidas(usuarioId: Long): Result<Long> = try {
        val countPublicaciones = try {
            publicacionApi.contarNotificacionesNoLeidas(usuarioId)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) 0L else 0L
        } catch (e: Exception) {
            0L
        }
        
        val countComentarios = try {
            comentarioApi.contarNotificacionesNoLeidas(usuarioId)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) 0L else 0L
        } catch (e: Exception) {
            0L
        }
        
        Result.success(countPublicaciones + countComentarios)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Marcar una notificación como leída (intenta en ambos microservicios)
    suspend fun marcarComoLeida(notificacionId: Long, usuarioId: Long): Result<NotificacionDomain> = try {
        // Primero intentar en publicaciones
        val notificacion = try {
            publicacionApi.marcarNotificacionComoLeida(notificacionId)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                // Si no se encuentra en publicaciones, intentar en comentarios
                comentarioApi.marcarNotificacionComoLeida(notificacionId)
            } else {
                throw e
            }
        } catch (e: Exception) {
            // Si falla en publicaciones, intentar en comentarios
            comentarioApi.marcarNotificacionComoLeida(notificacionId)
        }
        Result.success(notificacion.toNotificacionDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Marcar todas las notificaciones de un usuario como leídas (en ambos microservicios)
    suspend fun marcarTodasComoLeidas(usuarioId: Long): Result<Unit> = try {
        // Marcar en ambos microservicios (ignorar errores si no hay notificaciones)
        try {
            publicacionApi.marcarTodasComoLeidas(usuarioId)
        } catch (e: Exception) {
            // Ignorar errores de publicaciones
        }
        try {
            comentarioApi.marcarTodasComoLeidas(usuarioId)
        } catch (e: Exception) {
            // Ignorar errores de comentarios
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Eliminar una notificación (intenta en ambos microservicios)
    suspend fun eliminarNotificacion(notificacionId: Long, usuarioId: Long): Result<Unit> = try {
        // Primero intentar en publicaciones
        try {
            publicacionApi.eliminarNotificacion(notificacionId)
            Result.success(Unit)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                // Si no se encuentra en publicaciones, intentar en comentarios
                comentarioApi.eliminarNotificacion(notificacionId)
                Result.success(Unit)
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            // Si falla en publicaciones, intentar en comentarios
            comentarioApi.eliminarNotificacion(notificacionId)
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

