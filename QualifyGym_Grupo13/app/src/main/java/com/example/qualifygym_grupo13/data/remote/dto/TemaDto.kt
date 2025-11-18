package com.example.qualifygym_grupo13.data.remote.dto

// DTO para Tema del microservicio de Temas
data class TemaDto(
    val idTema: Long,
    val nombreTema: String,
    val estadoId: Long
)

// DTO para crear tema
data class TemaCreateDto(
    val nombreTema: String,
    val estadoId: Long
)

// DTO para actualizar tema
data class TemaUpdateDto(
    val nombreTema: String? = null,
    val estadoId: Long? = null
)

