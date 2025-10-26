package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.local.imagen.ImagenDao
import com.example.qualifygym_grupo13.data.local.imagen.ImagenEntity
import kotlinx.coroutines.flow.Flow

class ImagenRepository(private val imagenDao: ImagenDao) {
    
    fun getImagenesByPublicacionId(publicacionId: Long): Flow<List<ImagenEntity>> = 
        imagenDao.getByPublicacionId(publicacionId)
    
    suspend fun getImagenById(id: Long): ImagenEntity? = imagenDao.getById(id)
    
    suspend fun insertImagen(
        nombreImagen: String,
        imagenBytes: ByteArray,
        publicacionId: Long,
        estadoId: Long
    ): Result<Long> {
        return try {
            val imagen = ImagenEntity(
                nombre_imagen = nombreImagen,
                imagen = imagenBytes,
                Publicacion_id_publicacion = publicacionId,
                Estado_id_estado = estadoId
            )
            val id = imagenDao.insert(imagen)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteImagen(id: Long) {
        imagenDao.deleteById(id)
    }
}

