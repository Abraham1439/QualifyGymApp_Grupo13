package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.local.user.UserDao             //DAO de usuario
import com.example.qualifygym_grupo13.data.local.user.UserEntity          //Entidad de usuario

// Repositorio: orquesta reglas de negocio para login/registro sobre el DAO.
class UserRepository(
    private val userDao: UserDao // Inyección del DAO
) {

    // Obtener usuario por ID (para restaurar sesión)
    suspend fun getUserById(userId: Long): UserEntity? {
        return userDao.getById(userId)
    }

    // Login: busca por email y valida contraseña
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)                         // Busca usuario
        return if (user != null && user.password == password) {      // Verifica pass
            Result.success(user)                                     // Éxito
        } else {
            Result.failure(IllegalArgumentException("La cuenta no existe o la contraseña es incorrecta")) // Error
        }
    }


    // Registro: valida no duplicado y crea nuevo usuario (con teléfono)
    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        val exists = userDao.getByEmail(email) != null               // ¿Correo ya usado?
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(                                     // Inserta nuevo
            UserEntity(
                name = name,
                email = email,
                phone = phone,                                       // Teléfono incluido
                password = password
            )
        )
        return Result.success(id)                                    // Devuelve ID generado
    }

    // Actualizar perfil: valida que el nuevo email no esté en uso por otro usuario
    suspend fun updateProfile(userId: Long, newName: String, newEmail: String, newPhone: String): Result<UserEntity> {
        // Obtener el usuario actual
        val currentUser = userDao.getById(userId)
        if (currentUser == null) {
            return Result.failure(IllegalArgumentException("Usuario no encontrado"))
        }
        
        // Si el email cambió, verificar que no esté en uso por otro usuario
        if (newEmail != currentUser.email) {
            val emailExists = userDao.getByEmail(newEmail)
            if (emailExists != null && emailExists.id != userId) {
                return Result.failure(IllegalStateException("El correo ya está siendo utilizado por otro usuario"))
            }
        }
        
        // Actualizar el usuario
        val updatedUser = currentUser.copy(
            name = newName,
            email = newEmail,
            phone = newPhone
        )
        userDao.update(updatedUser)
        
        return Result.success(updatedUser)
    }

    // Cambiar contraseña: verifica que la contraseña actual sea correcta
    suspend fun changePassword(userId: Long, currentPassword: String, newPassword: String): Result<UserEntity> {
        // Obtener el usuario actual
        val currentUser = userDao.getById(userId)
        if (currentUser == null) {
            return Result.failure(IllegalArgumentException("Usuario no encontrado"))
        }
        
        // Verificar que la contraseña actual coincida
        if (currentUser.password != currentPassword) {
            return Result.failure(IllegalArgumentException("La contraseña actual no coincide"))
        }
        
        // Verificar que la nueva contraseña sea diferente
        if (currentPassword == newPassword) {
            return Result.failure(IllegalArgumentException("La nueva contraseña debe ser diferente a la actual"))
        }
        
        // Actualizar la contraseña
        val updatedUser = currentUser.copy(password = newPassword)
        userDao.update(updatedUser)
        
        return Result.success(updatedUser)
    }

    // Actualizar foto de perfil
    suspend fun updateProfilePhoto(userId: Long, photoPath: String?): Result<UserEntity> {
        val currentUser = userDao.getById(userId)
        if (currentUser == null) {
            return Result.failure(IllegalArgumentException("Usuario no encontrado"))
        }
        
        val updatedUser = currentUser.copy(photoUrl = photoPath)
        userDao.update(updatedUser)
        
        return Result.success(updatedUser)
    }

}