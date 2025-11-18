package com.example.qualifygym_grupo13.data.remote.dto

// DTO para Publicacion del microservicio de Publicaciones
data class PublicacionDto(
    val idPublicacion: Long,
    val titulo: String,
    val descripcion: String,
    val fecha: String, // Formato: "dd-MM-yyyy HH:mm"
    val oculta: Boolean,
    val fechaBaneo: String?, // Formato: "dd-MM-yyyy HH:mm" o null
    val motivoBaneo: String?,
    val usuarioId: Long,
    val temaId: Long,
    val imageUrl: String?
)

// DTO para crear publicación
data class PublicacionCreateDto(
    val titulo: String,
    val descripcion: String,
    val usuarioId: Long,
    val temaId: Long,
    val imageUrl: String? = null
)

// DTO para actualizar publicación
data class PublicacionUpdateDto(
    val titulo: String? = null,
    val descripcion: String? = null
)

// DTO para actualizar imagen
data class PublicacionImagenDto(
    val imageUrl: String
)

// DTO para ocultar publicación
data class PublicacionOcultarDto(
    val motivoBaneo: String
)

