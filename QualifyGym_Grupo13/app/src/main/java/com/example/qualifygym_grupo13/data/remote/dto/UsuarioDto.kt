package com.example.qualifygym_grupo13.data.remote.dto

// DTO para Usuario del microservicio de Usuarios
data class UsuarioDto(
    val id: Long,
    val username: String,
    val email: String,
    val rol: RolDto?
)

data class RolDto(
    val id: Long?,
    val nombre: String?
)

// DTO para crear/actualizar usuario
data class UsuarioCreateDto(
    val username: String,
    val password: String,
    val email: String,
    val rolId: Long
)

// DTO para login
data class LoginRequestDto(
    val email: String,
    val password: String
)
