package com.example.qualifygym_grupo13.data.remote.dto

// DTO para respuesta de imagen (metadatos, sin los datos BLOB)
data class ImagenDto(
    val idImagen: Long,
    val usuarioId: Long?,
    val publicacionId: Long?,
    val tipoImagen: String, // "PERFIL" o "PUBLICACION"
    val tipoMime: String,
    val nombreArchivo: String?,
    val tamaño: Long,
    val fechaSubida: String // Formato: "dd-MM-yyyy HH:mm"
)

// DTO para respuesta de subida de imagen
data class ImagenSubidaDto(
    val idImagen: Long,
    val usuarioId: Long?,
    val publicacionId: Long?,
    val tipoImagen: String,
    val tipoMime: String,
    val nombreArchivo: String?,
    val tamaño: Long,
    val fechaSubida: String
)

