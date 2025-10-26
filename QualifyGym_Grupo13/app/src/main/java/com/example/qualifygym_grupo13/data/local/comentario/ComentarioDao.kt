package com.example.qualifygym_grupo13.data.local.comentario

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ComentarioDao {
    @Insert
    suspend fun insert(comentario: ComentarioEntity): Long
    
    @Update
    suspend fun update(comentario: ComentarioEntity)
    
    @Query("SELECT * FROM comentarios WHERE Publicacion_id_publicacion = :publicacionId ORDER BY fecha_registro DESC")
    fun getByPublicacionId(publicacionId: Long): Flow<List<ComentarioEntity>>
    
    @Query("SELECT * FROM comentarios WHERE Usuarios_id_usuario = :userId ORDER BY fecha_registro DESC")
    fun getByUserId(userId: Long): Flow<List<ComentarioEntity>>
    
    @Query("SELECT * FROM comentarios WHERE id_comentario = :id LIMIT 1")
    suspend fun getById(id: Long): ComentarioEntity?
    
    @Query("DELETE FROM comentarios WHERE id_comentario = :id")
    suspend fun deleteById(id: Long)
}

