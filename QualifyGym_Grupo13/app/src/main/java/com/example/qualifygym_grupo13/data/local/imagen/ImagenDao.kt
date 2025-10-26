package com.example.qualifygym_grupo13.data.local.imagen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImagenDao {
    @Insert
    suspend fun insert(imagen: ImagenEntity): Long
    
    @Query("SELECT * FROM imagenes WHERE Publicacion_id_publicacion = :publicacionId")
    fun getByPublicacionId(publicacionId: Long): Flow<List<ImagenEntity>>
    
    @Query("SELECT * FROM imagenes WHERE id_imagen = :id LIMIT 1")
    suspend fun getById(id: Long): ImagenEntity?
    
    @Query("DELETE FROM imagenes WHERE id_imagen = :id")
    suspend fun deleteById(id: Long)
}

