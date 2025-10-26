package com.example.qualifygym_grupo13.data.local.estado

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EstadoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(estado: EstadoEntity): Long
    
    @Query("SELECT * FROM estados")
    fun getAll(): Flow<List<EstadoEntity>>
    
    @Query("SELECT * FROM estados WHERE id_estado = :id LIMIT 1")
    suspend fun getById(id: Long): EstadoEntity?
    
    @Query("SELECT * FROM estados WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): EstadoEntity?
}

