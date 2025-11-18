package com.example.qualifygym_grupo13.data.remote.dto

// DTO para Comentario del microservicio de Comentarios
data class ComentarioDto(
    val idComentario: Long,
    val comentario: String,
    val fechaRegistro: String, // Formato: "dd-MM-yyyy HH:mm"
    val oculto: Boolean,
    val fechaBaneo: String?, // Formato: "dd-MM-yyyy HH:mm" o null
    val motivoBaneo: String?,
    val usuarioId: Long,
    val publicacionId: Long
)

// DTO para crear comentario
data class ComentarioCreateDto(
    val comentario: String,
    val usuarioId: Long,
    val publicacionId: Long
)

// DTO para actualizar comentario
data class ComentarioUpdateDto(
    val comentario: String
)

// DTO para ocultar comentario
data class ComentarioOcultarDto(
    val motivoBaneo: String
)

