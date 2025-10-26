package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.local.comentario.ComentarioDao
import com.example.qualifygym_grupo13.data.local.comentario.ComentarioEntity
import kotlinx.coroutines.flow.Flow

class ComentarioRepository(private val comentarioDao: ComentarioDao) {
    
    fun getComentariosByPublicacionId(publicacionId: Long): Flow<List<ComentarioEntity>> = 
        comentarioDao.getByPublicacionId(publicacionId)
    
    fun getComentariosByUserId(userId: Long): Flow<List<ComentarioEntity>> = 
        comentarioDao.getByUserId(userId)
    
    suspend fun getComentarioById(id: Long): ComentarioEntity? = comentarioDao.getById(id)
    
    suspend fun insertComentario(
        comentario: String,
        userId: Long,
        publicacionId: Long
    ): Result<Long> {
        return try {
            val comentarioEntity = ComentarioEntity(
                comentario = comentario,
                fecha_registro = System.currentTimeMillis(),
                Usuarios_id_usuario = userId,
                Publicacion_id_publicacion = publicacionId
            )
            val id = comentarioDao.insert(comentarioEntity)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateComentario(comentario: ComentarioEntity) {
        comentarioDao.update(comentario)
    }
    
    suspend fun deleteComentario(id: Long) {
        comentarioDao.deleteById(id)
    }
}

