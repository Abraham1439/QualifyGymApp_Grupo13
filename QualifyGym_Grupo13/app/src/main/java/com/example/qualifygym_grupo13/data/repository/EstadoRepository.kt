package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.EstadoApi
import com.example.qualifygym_grupo13.data.remote.RemoteModule
import com.example.qualifygym_grupo13.data.remote.dto.*
import retrofit2.HttpException

class EstadoRepository(
    private val api: EstadoApi = RemoteModule.estadoApi
) {
    // Obtiene todos los estados
    suspend fun fetchEstados(): Result<List<EstadoDto>> = try {
        Result.success(api.getEstados())
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene un estado por ID
    suspend fun fetchEstadoById(id: Long): Result<EstadoDto> = try {
        Result.success(api.getEstadoById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene un estado por nombre
    suspend fun fetchEstadoPorNombre(nombre: String): Result<EstadoDto> = try {
        Result.success(api.getEstadoPorNombre(nombre))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Verifica si existe un estado por nombre
    suspend fun existeEstadoPorNombre(nombre: String): Result<Boolean> = try {
        Result.success(api.existeEstadoPorNombre(nombre))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Crea un nuevo estado
    suspend fun create(estado: EstadoCreateDto): Result<EstadoDto> = try {
        Result.success(api.crearEstado(estado))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene o crea un estado
    suspend fun obtenerOCrearEstado(estado: EstadoCreateDto): Result<EstadoDto> = try {
        Result.success(api.obtenerOCrearEstado(estado))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Actualiza un estado
    suspend fun update(id: Long, estado: EstadoUpdateDto): Result<EstadoDto> = try {
        Result.success(api.actualizarEstado(id, estado))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Elimina un estado
    suspend fun delete(id: Long): Result<Unit> = try {
        val resp = api.eliminarEstado(id)
        if (resp.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(resp))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
