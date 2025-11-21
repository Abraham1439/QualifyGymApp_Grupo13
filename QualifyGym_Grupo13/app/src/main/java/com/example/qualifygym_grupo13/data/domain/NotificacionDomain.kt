package com.example.qualifygym_grupo13.data.domain

// Modelo de dominio para Notificacion (compatible con notificaciones de publicaciones y comentarios)
data class NotificacionDomain(
    val idNotificacion: Long,
    val usuarioId: Long,
    val publicacionId: Long? = null, // Para notificaciones de publicaciones
    val comentarioId: Long? = null, // Para notificaciones de comentarios
    val mensaje: String,
    val fechaCreacion: String, // Formato: "dd-MM-yyyy HH:mm"
    val leida: Boolean
)

