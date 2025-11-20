package com.example.qualifygym_grupo13.data.domain

// Modelo de dominio para Notificacion
data class NotificacionDomain(
    val idNotificacion: Long,
    val usuarioId: Long,
    val publicacionId: Long,
    val mensaje: String,
    val fechaCreacion: String, // Formato: "dd-MM-yyyy HH:mm"
    val leida: Boolean
)

