package com.example.qualifygym_grupo13.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)    // Clave primaria autoincremental)
    val id: Long =0L,

    val name: String,                   // Nombre completo del usuario
    val email: String,                  // Correo (idealmente único a nivel de negocio)
    val phone: String,                  // Teléfono del usuario (agregado)
    val password: String,               // Contraseña (para demo; en prod usar hash)
    val isAdmin: Boolean = false,       // Indica si el usuario es administrador
    val photoUrl: String? = null        // URL/Path de la foto de perfil

)