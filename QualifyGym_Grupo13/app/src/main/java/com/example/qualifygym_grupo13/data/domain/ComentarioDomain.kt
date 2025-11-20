package com.example.qualifygym_grupo13.data.domain

// Modelo de dominio para Comentario (sin Room)
data class ComentarioDomain(
    val idComentario: Long = 0,
    val comentario: String,
    val fechaRegistro: Long, // Timestamp en milisegundos
    val oculto: Boolean = false,
    val fechaBaneo: Long? = null,
    val motivoBaneo: String? = null,
    val usuarioId: Long,
    val publicacionId: Long
)

