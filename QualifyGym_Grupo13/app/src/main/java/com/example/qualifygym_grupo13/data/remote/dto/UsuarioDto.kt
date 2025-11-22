package com.example.qualifygym_grupo13.data.remote.dto

// DTO para Usuario del microservicio de Usuarios
data class UsuarioDto(
    val id: Long,
    val username: String,
    val email: String,
    val phone: String?,
    val rol: RolDto?,
    val photoUrl: String? = null // ID de la imagen del microservicio de Imágenes
)

data class RolDto(
    val id: Long?,
    val nombre: String?
)

// DTO para crear/actualizar usuario (endpoint privado - requiere rolId)
data class UsuarioCreateDto(
    val username: String,
    val password: String,
    val email: String,
    val phone: String,
    val rolId: Long? = null, // Opcional para el endpoint público de registro
    val photoUrl: String? = null // ID de la imagen del microservicio de Imágenes
)

// DTO para registro público (no requiere rolId, se asigna automáticamente)
data class UsuarioRegisterDto(
    val username: String,
    val password: String,
    val email: String,
    val phone: String
)

// DTO para login (el microservicio ahora espera email, no username)
data class LoginRequestDto(
    val email: String,
    val password: String
)
