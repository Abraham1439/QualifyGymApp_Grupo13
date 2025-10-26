package com.example.qualifygym_grupo13.data.local.publicacion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicacionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(publicacion: PublicacionEntity): Long
    
    @Update
    suspend fun update(publicacion: PublicacionEntity)
    
    @Query("SELECT * FROM publicaciones ORDER BY fecha DESC")
    fun getAll(): Flow<List<PublicacionEntity>>
    
    @Query("SELECT * FROM publicaciones WHERE id_publicacion = :id LIMIT 1")
    suspend fun getById(id: Long): PublicacionEntity?
    
    @Query("SELECT * FROM publicaciones WHERE Usuarios_id_usuario = :userId ORDER BY fecha DESC")
    fun getByUserId(userId: Long): Flow<List<PublicacionEntity>>
    
    @Query("SELECT * FROM publicaciones WHERE Tema_id_tema = :temaId ORDER BY fecha DESC")
    fun getByTemaId(temaId: Long): Flow<List<PublicacionEntity>>
    
    @Query("SELECT * FROM publicaciones WHERE titulo LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%' ORDER BY fecha DESC")
    fun search(query: String): Flow<List<PublicacionEntity>>
    
    @Query("DELETE FROM publicaciones WHERE id_publicacion = :id")
    suspend fun deleteById(id: Long)
}

