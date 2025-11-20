package com.example.qualifygym_grupo13.data.domain

// Modelo de dominio para Publicación (sin Room)
data class PublicacionDomain(
    val idPublicacion: Long = 0,
    val titulo: String,
    val fecha: Long, // Timestamp en milisegundos
    val descripcion: String,
    val oculta: Boolean = false,
    val fechaBaneo: Long? = null,
    val motivoBaneo: String? = null,
    val usuarioId: Long,
    val temaId: Long,
    val imageUrl: String? = null
)

