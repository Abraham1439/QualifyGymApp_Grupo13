package com.example.qualifygym_grupo13.data.remote.dto

// DTO para Estado del microservicio de Estados
data class EstadoDto(
    val idEstado: Long,
    val nombre: String
)

// DTO para crear estado
data class EstadoCreateDto(
    val nombre: String
)

// DTO para actualizar estado
data class EstadoUpdateDto(
    val nombre: String
)

