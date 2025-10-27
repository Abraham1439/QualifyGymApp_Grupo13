package com.example.qualifygym_grupo13.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    // Inserta un usuario. IGNORE si hay conflicto para evitar crashes.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity): Long

    // Devuelve un usuario por email (o null si no existe).
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?
    
    // Devuelve un usuario por ID (o null si no existe).
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getById(userId: Long): UserEntity?
    
    // Actualiza un usuario existente
    @Update
    suspend fun update(user: UserEntity)

    // Cuenta total de usuarios (para saber si hay datos y/o para seeds).
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    // Lista completa (útil para debug/administración).
    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAll(): List<UserEntity>

}