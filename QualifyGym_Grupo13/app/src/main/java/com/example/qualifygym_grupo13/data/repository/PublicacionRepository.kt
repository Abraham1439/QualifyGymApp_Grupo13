package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.local.publicacion.PublicacionDao
import com.example.qualifygym_grupo13.data.local.publicacion.PublicacionEntity
import kotlinx.coroutines.flow.Flow

class PublicacionRepository(private val publicacionDao: PublicacionDao) {
    
    fun getAllPublicaciones(): Flow<List<PublicacionEntity>> = publicacionDao.getAll()
    
    suspend fun getPublicacionById(id: Long): PublicacionEntity? = publicacionDao.getById(id)
    
    fun getPublicacionesByUserId(userId: Long): Flow<List<PublicacionEntity>> = 
        publicacionDao.getByUserId(userId)
    
    fun getPublicacionesByTemaId(temaId: Long): Flow<List<PublicacionEntity>> = 
        publicacionDao.getByTemaId(temaId)
    
    fun searchPublicaciones(query: String): Flow<List<PublicacionEntity>> = 
        publicacionDao.search(query)
    
    suspend fun insertPublicacion(
        titulo: String,
        descripcion: String,
        userId: Long,
        temaId: Long,
        imageUrl: String? = null
    ): Result<Long> {
        return try {
            val publicacion = PublicacionEntity(
                titulo = titulo,
                fecha = System.currentTimeMillis(),
                descripcion = descripcion,
                Usuarios_id_usuario = userId,
                Tema_id_tema = temaId,
                imageUrl = imageUrl
            )
            val id = publicacionDao.insert(publicacion)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updatePublicacionImage(publicacionId: Long, imageUrl: String?): Result<Unit> {
        return try {
            val publicacion = publicacionDao.getById(publicacionId)
            if (publicacion != null) {
                val updatedPublicacion = publicacion.copy(imageUrl = imageUrl)
                publicacionDao.update(updatedPublicacion)
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Publicación no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updatePublicacion(publicacion: PublicacionEntity) {
        publicacionDao.update(publicacion)
    }
    
    suspend fun deletePublicacion(id: Long) {
        publicacionDao.deleteById(id)
    }
}

