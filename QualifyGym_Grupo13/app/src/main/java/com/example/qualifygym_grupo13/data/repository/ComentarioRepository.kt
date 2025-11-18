package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.ComentarioApi
import com.example.qualifygym_grupo13.data.remote.RemoteModule
import com.example.qualifygym_grupo13.data.remote.dto.*
import retrofit2.HttpException

class ComentarioRepository(
    private val api: ComentarioApi = RemoteModule.comentarioApi
) {
    // Obtiene todos los comentarios
    suspend fun fetchComentarios(): Result<List<ComentarioDto>> = try {
        Result.success(api.getComentarios())
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene un comentario por ID
    suspend fun fetchComentarioById(id: Long): Result<ComentarioDto> = try {
        Result.success(api.getComentarioById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene comentarios por publicación
    suspend fun fetchComentariosPorPublicacion(publicacionId: Long, incluirOcultos: Boolean = false): Result<List<ComentarioDto>> = try {
        Result.success(api.getComentariosPorPublicacion(publicacionId, incluirOcultos))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene comentarios por usuario
    suspend fun fetchComentariosPorUsuario(usuarioId: Long): Result<List<ComentarioDto>> = try {
        Result.success(api.getComentariosPorUsuario(usuarioId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Cuenta comentarios por publicación
    suspend fun contarComentariosPorPublicacion(publicacionId: Long): Result<Long> = try {
        Result.success(api.contarComentariosPorPublicacion(publicacionId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Cuenta comentarios por usuario
    suspend fun contarComentariosPorUsuario(usuarioId: Long): Result<Long> = try {
        Result.success(api.contarComentariosPorUsuario(usuarioId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Crea un nuevo comentario
    suspend fun create(comentario: ComentarioCreateDto): Result<ComentarioDto> = try {
        Result.success(api.crearComentario(comentario))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Actualiza un comentario
    suspend fun update(id: Long, comentario: ComentarioUpdateDto): Result<ComentarioDto> = try {
        Result.success(api.actualizarComentario(id, comentario))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Oculta un comentario
    suspend fun ocultar(id: Long, motivoBaneo: String): Result<ComentarioDto> = try {
        Result.success(api.ocultarComentario(id, ComentarioOcultarDto(motivoBaneo)))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Muestra un comentario (desocultar)
    suspend fun mostrar(id: Long): Result<ComentarioDto> = try {
        Result.success(api.mostrarComentario(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Elimina un comentario
    suspend fun delete(id: Long): Result<Unit> = try {
        val resp = api.eliminarComentario(id)
        if (resp.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(resp))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
