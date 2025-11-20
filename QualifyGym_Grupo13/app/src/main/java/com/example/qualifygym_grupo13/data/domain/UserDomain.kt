package com.example.qualifygym_grupo13.data.domain

// Modelo de dominio para User (sin Room)
data class UserDomain(
    val id: Long = 0L,
    val name: String,
    val email: String,
    val phone: String,
    val password: String = "", // Solo para uso interno, no se persiste
    val isAdmin: Boolean = false,
    val photoUrl: String? = null
)

