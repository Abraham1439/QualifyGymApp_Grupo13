package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.RemoteModule
import com.example.qualifygym_grupo13.data.remote.TemaApi
import com.example.qualifygym_grupo13.data.remote.dto.*
import retrofit2.HttpException

class TemaRepository(
    private val api: TemaApi = RemoteModule.temaApi
) {
    // Obtiene todos los temas
    suspend fun fetchTemas(): Result<List<TemaDto>> = try {
        Result.success(api.getTemas())
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene un tema por ID
    suspend fun fetchTemaById(id: Long): Result<TemaDto> = try {
        Result.success(api.getTemaById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene temas por estado
    suspend fun fetchTemasPorEstado(estadoId: Long): Result<List<TemaDto>> = try {
        Result.success(api.getTemasPorEstado(estadoId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Busca temas por nombre
    suspend fun buscarTemas(query: String): Result<List<TemaDto>> = try {
        Result.success(api.buscarTemas(query))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene un tema por nombre exacto
    suspend fun fetchTemaPorNombre(nombre: String): Result<TemaDto> = try {
        Result.success(api.getTemaPorNombre(nombre))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Verifica si existe un tema por nombre
    suspend fun existeTemaPorNombre(nombre: String): Result<Boolean> = try {
        Result.success(api.existeTemaPorNombre(nombre))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Cuenta temas por estado
    suspend fun contarTemasPorEstado(estadoId: Long): Result<Long> = try {
        Result.success(api.contarTemasPorEstado(estadoId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Crea un nuevo tema
    suspend fun create(tema: TemaCreateDto): Result<TemaDto> = try {
        Result.success(api.crearTema(tema))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Actualiza un tema
    suspend fun update(id: Long, tema: TemaUpdateDto): Result<TemaDto> = try {
        Result.success(api.actualizarTema(id, tema))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Elimina un tema
    suspend fun delete(id: Long): Result<Unit> = try {
        val resp = api.eliminarTema(id)
        if (resp.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(resp))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
