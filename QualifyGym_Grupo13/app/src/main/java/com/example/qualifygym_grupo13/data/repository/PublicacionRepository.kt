package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.PublicacionApi
import com.example.qualifygym_grupo13.data.remote.RemoteModule
import com.example.qualifygym_grupo13.data.remote.dto.*
import retrofit2.HttpException

class PublicacionRepository(
    private val api: PublicacionApi = RemoteModule.publicacionApi
) {
    // Obtiene todas las publicaciones
    suspend fun fetchPublicaciones(incluirOcultas: Boolean = false): Result<List<PublicacionDto>> = try {
        Result.success(api.getPublicaciones(incluirOcultas))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene una publicación por ID
    suspend fun fetchPublicacionById(id: Long): Result<PublicacionDto> = try {
        Result.success(api.getPublicacionById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene publicaciones por tema
    suspend fun fetchPublicacionesPorTema(temaId: Long, incluirOcultas: Boolean = false): Result<List<PublicacionDto>> = try {
        Result.success(api.getPublicacionesPorTema(temaId, incluirOcultas))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene publicaciones por usuario
    suspend fun fetchPublicacionesPorUsuario(usuarioId: Long, incluirOcultas: Boolean = false): Result<List<PublicacionDto>> = try {
        val publicaciones = api.getPublicacionesPorUsuario(usuarioId, incluirOcultas)
        // Si la API devuelve null (usuario sin publicaciones), retornar lista vacía
        Result.success(publicaciones ?: emptyList())
    } catch (e: Exception) {
        // Si el error es porque el body es null (usuario sin publicaciones), retornar lista vacía
        if (e.message?.contains("was null but response body type was declared as non-null") == true) {
            Result.success(emptyList())
        } else {
            Result.failure(e)
        }
    }

    // Busca publicaciones
    suspend fun buscarPublicaciones(query: String): Result<List<PublicacionDto>> = try {
        Result.success(api.buscarPublicaciones(query))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Cuenta publicaciones por tema
    suspend fun contarPublicacionesPorTema(temaId: Long): Result<Long> = try {
        Result.success(api.contarPublicacionesPorTema(temaId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Cuenta publicaciones por usuario
    suspend fun contarPublicacionesPorUsuario(usuarioId: Long): Result<Long> = try {
        Result.success(api.contarPublicacionesPorUsuario(usuarioId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Crea una nueva publicación
    suspend fun create(publicacion: PublicacionCreateDto): Result<PublicacionDto> = try {
        Result.success(api.crearPublicacion(publicacion))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Actualiza una publicación
    suspend fun update(id: Long, publicacion: PublicacionUpdateDto): Result<PublicacionDto> = try {
        Result.success(api.actualizarPublicacion(id, publicacion))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Actualiza la imagen de una publicación
    suspend fun updateImagen(id: Long, imageUrl: String): Result<PublicacionDto> = try {
        Result.success(api.actualizarImagenPublicacion(id, PublicacionImagenDto(imageUrl)))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Oculta una publicación
    suspend fun ocultar(id: Long, motivoBaneo: String): Result<PublicacionDto> = try {
        Result.success(api.ocultarPublicacion(id, PublicacionOcultarDto(motivoBaneo)))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Muestra una publicación (desocultar)
    suspend fun mostrar(id: Long): Result<PublicacionDto> = try {
        Result.success(api.mostrarPublicacion(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Elimina una publicación
    suspend fun delete(id: Long): Result<Unit> = try {
        val resp = api.eliminarPublicacion(id)
        if (resp.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(resp))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
