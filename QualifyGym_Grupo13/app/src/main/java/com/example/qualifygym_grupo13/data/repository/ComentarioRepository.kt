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
    
    // Ocultar comentario (actualizar campo oculto a true)
    suspend fun ocultarComentario(id: Long): Result<Unit> {
        return try {
            val comentario = comentarioDao.getById(id)
            if (comentario != null) {
                val comentarioActualizado = comentario.copy(oculto = true)
                comentarioDao.update(comentarioActualizado)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Comentario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Mostrar comentario (actualizar campo oculto a false)
    suspend fun mostrarComentario(id: Long): Result<Unit> {
        return try {
            val comentario = comentarioDao.getById(id)
            if (comentario != null) {
                val comentarioActualizado = comentario.copy(oculto = false)
                comentarioDao.update(comentarioActualizado)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Comentario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

