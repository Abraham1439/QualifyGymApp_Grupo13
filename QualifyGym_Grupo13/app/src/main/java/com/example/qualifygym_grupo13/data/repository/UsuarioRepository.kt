package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.RemoteModule
import com.example.qualifygym_grupo13.data.remote.UsuarioApi
import com.example.qualifygym_grupo13.data.remote.dto.LoginRequestDto
import com.example.qualifygym_grupo13.data.remote.dto.UsuarioCreateDto
import com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto
import retrofit2.HttpException

class UsuarioRepository(
    private val api: UsuarioApi = RemoteModule.usuarioApi
) {
    // Obtiene todos los usuarios
    suspend fun fetchUsuarios(): Result<List<UsuarioDto>> = try {
        Result.success(api.getusuarios())
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene un usuario por ID
    suspend fun fetchUsuarioById(id: Long): Result<UsuarioDto> = try {
        Result.success(api.getUsuarioById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Crea un nuevo usuario
    suspend fun create(usuario: UsuarioCreateDto): Result<UsuarioDto> = try {
        Result.success(api.crearUsuario(usuario))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Actualiza un usuario existente
    suspend fun update(id: Long, usuario: UsuarioCreateDto): Result<UsuarioDto> = try {
        Result.success(api.actualizarUsuario(id, usuario))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Elimina un usuario por ID
    suspend fun delete(id: Long): Result<Unit> = try {
        val resp = api.eliminarUsuario(id)
        if (resp.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(resp))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Login de usuario
    suspend fun login(email: String, password: String): Result<String> = try {
        val loginRequest = LoginRequestDto(email, password)
        val resp = api.login(loginRequest)
        if (resp.isSuccessful) {
            Result.success(resp.body() ?: "Login exitoso")
        } else {
            Result.failure(HttpException(resp))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

}