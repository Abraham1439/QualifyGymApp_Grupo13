package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.domain.UserDomain
import com.example.qualifygym_grupo13.data.remote.RemoteModule
import com.example.qualifygym_grupo13.data.remote.UsuarioApi
import com.example.qualifygym_grupo13.data.remote.dto.LoginRequestDto
import com.example.qualifygym_grupo13.data.remote.dto.UsuarioCreateDto
import com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto
import com.example.qualifygym_grupo13.data.remote.dto.UsuarioRegisterDto
import retrofit2.HttpException

// Función helper para convertir UsuarioDto a UserDomain
fun UsuarioDto.toUserDomain(phone: String? = null, password: String = "", photoUrl: String? = null): UserDomain {
    return UserDomain(
        id = this.id,
        name = this.username,
        email = this.email,
        phone = phone ?: this.phone ?: "",
        password = password, // No viene del DTO por seguridad
        isAdmin = this.rol?.nombre?.equals("Administrador", ignoreCase = true) == true,
        photoUrl = photoUrl
    )
}

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

    // Crea un nuevo usuario (usa endpoint público para registro)
    suspend fun create(usuario: UsuarioRegisterDto): Result<UsuarioDto> {
        return try {
            // Usar el endpoint público para registro (sin autenticación)
            Result.success(api.registrarUsuarioPublico(usuario))
        } catch (e: retrofit2.HttpException) {
            // Manejar errores HTTP específicos
            val errorBody = try {
                e.response()?.errorBody()?.string() ?: "Sin detalles"
            } catch (ex: Exception) {
                "No se pudo leer el cuerpo del error"
            }
            
            val errorMessage = when (e.code()) {
                401 -> "No autorizado. El endpoint de registro debería ser público. Detalles: $errorBody"
                400 -> {
                    // Intentar extraer mensaje más específico
                    if (errorBody.contains("username")) {
                        "El nombre de usuario ya está registrado o es inválido."
                    } else if (errorBody.contains("email")) {
                        "El email ya está registrado o es inválido."
                    } else {
                        "Datos inválidos: $errorBody"
                    }
                }
                409 -> "El usuario ya existe: $errorBody"
                500 -> "Error interno del servidor. Por favor intenta más tarde."
                else -> "Error del servidor (${e.code()}): $errorBody"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("No se pudo conectar al servidor. Verifica tu conexión a internet."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado. El servidor no respondió a tiempo."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("No se pudo establecer conexión con el servidor. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message ?: e.javaClass.simpleName}"))
        }
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

    // Busca un usuario por email usando el endpoint optimizado
    suspend fun findUsuarioByEmail(email: String): Result<UsuarioDto?> = try {
        val usuario = api.getUsuarioByEmail(email)
        Result.success(usuario)
    } catch (e: retrofit2.HttpException) {
        if (e.code() == 404) {
            Result.success(null) // Usuario no encontrado, retornar null en lugar de error
        } else {
            Result.failure(e)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Login de usuario usando email (el microservicio ahora acepta email directamente)
    suspend fun login(email: String, password: String): Result<UserDomain> {
        return try {
            // Hacer login directamente con email y password
            val loginRequest = LoginRequestDto(email.trim(), password)
            val resp = api.login(loginRequest)
            
            if (resp.isSuccessful) {
                // Cerrar el ResponseBody para liberar recursos (el microservicio devuelve "Login exitoso" como texto)
                resp.body()?.close()
                
                // Si el login es exitoso, obtener el usuario por email para construir UserDomain
                val usuarioResult = findUsuarioByEmail(email.trim())
                val usuario = usuarioResult.getOrNull()
                
                if (usuario != null) {
                    Result.success(usuario.toUserDomain())
                } else {
                    // Login exitoso pero no se pudo obtener el usuario (caso raro)
                    Result.failure(IllegalStateException("Login exitoso pero no se pudo obtener la información del usuario"))
                }
            } else {
                // Extraer mensaje de error del response
                val errorBody = try {
                    resp.errorBody()?.string() ?: "Credenciales inválidas"
                } catch (e: Exception) {
                    "Credenciales inválidas"
                }
                
                // Crear un mensaje de error más descriptivo
                val errorMessage = when (resp.code()) {
                    401 -> {
                        if (errorBody.contains("Credenciales inválidas")) {
                            "Email o contraseña incorrectos. Verifica tus credenciales."
                        } else {
                            "Credenciales inválidas: $errorBody"
                        }
                    }
                    400 -> "Datos inválidos: $errorBody"
                    500 -> "Error del servidor. Por favor intenta más tarde."
                    else -> "Error de autenticación (${resp.code()}): $errorBody"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: retrofit2.HttpException) {
            // Manejar errores HTTP específicos
            val errorBody = try {
                e.response()?.errorBody()?.string() ?: "Error de autenticación"
            } catch (ex: Exception) {
                "Error de autenticación"
            }
            val errorMessage = when (e.code()) {
                401 -> {
                    if (errorBody.contains("Credenciales inválidas") || errorBody.contains("Credenciales")) {
                        "Email o contraseña incorrectos. Verifica tus credenciales."
                    } else {
                        "Credenciales inválidas: $errorBody"
                    }
                }
                400 -> "Datos inválidos. Verifica que el email y contraseña estén correctos: $errorBody"
                500 -> "Error del servidor. Por favor intenta más tarde."
                else -> "Error de autenticación (${e.code()}): $errorBody"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("No se pudo conectar al servidor. Verifica tu conexión a internet."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado. El servidor no respondió a tiempo."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("No se pudo establecer conexión con el servidor. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message ?: e.javaClass.simpleName}"))
        }
    }

    // Obtener usuario por ID con mejor manejo de errores
    suspend fun getUserById(userId: Long): UserDomain? = try {
        val result = fetchUsuarioById(userId)
        result.getOrNull()?.toUserDomain()
    } catch (e: retrofit2.HttpException) {
        if (e.code() == 404) {
            null // Usuario no encontrado
        } else {
            null // En caso de otro error, retornar null
        }
    } catch (e: java.net.UnknownHostException) {
        null // Sin conexión, retornar null para que se use caché local
    } catch (e: java.net.SocketTimeoutException) {
        null // Timeout, retornar null
    } catch (e: Exception) {
        null // Cualquier otro error, retornar null
    }

    // Registro: crea un nuevo usuario
    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        return try {
            // Verificar si el email ya existe
            val existeResult = findUsuarioByEmail(email)
            val existe = existeResult.getOrNull() != null
            
            if (existe) {
                Result.failure(IllegalStateException("El correo ya está registrado"))
            } else {
                // Crear el usuario usando el endpoint público (no requiere rolId, se asigna automáticamente)
                val usuarioRegister = UsuarioRegisterDto(
                    username = name,
                    password = password,
                    email = email,
                    phone = phone
                )
                
                val result = create(usuarioRegister)
                result.fold(
                    onSuccess = { usuario -> Result.success(usuario.id) },
                    onFailure = { error -> Result.failure(error) }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar perfil del usuario (requiere contraseña actual para validar)
    suspend fun updateProfile(userId: Long, newName: String, newEmail: String, newPhone: String, currentPassword: String): Result<UserDomain> {
        return try {
            // Obtener el usuario actual
            val currentUserResult = fetchUsuarioById(userId)
            val currentUser = currentUserResult.getOrNull()
            
            if (currentUser == null) {
                Result.failure(IllegalArgumentException("Usuario no encontrado"))
            } else {
                // Verificar la contraseña actual haciendo login
                val loginResult = login(currentUser.email, currentPassword)
                if (loginResult.isFailure) {
                    Result.failure(IllegalArgumentException("La contraseña actual no coincide"))
                } else {
                    // Si el email cambió, verificar que no esté en uso
                    if (newEmail != currentUser.email) {
                        val emailExistsResult = findUsuarioByEmail(newEmail)
                        val emailExists = emailExistsResult.getOrNull()
                        if (emailExists != null && emailExists.id != userId) {
                            Result.failure(IllegalStateException("El correo ya está siendo utilizado por otro usuario"))
                        } else {
                            // Actualizar el usuario (usamos la contraseña actual ya que el microservicio la requiere)
                            val usuarioUpdate = UsuarioCreateDto(
                                username = newName,
                                password = currentPassword, // Usamos la contraseña actual (no la cambiamos)
                                email = newEmail,
                                phone = newPhone,
                                rolId = currentUser.rol?.id ?: 2L
                            )
                            
                            val result = update(userId, usuarioUpdate)
                            result.fold(
                                onSuccess = { usuario -> Result.success(usuario.toUserDomain(phone = newPhone)) },
                                onFailure = { error -> Result.failure(error) }
                            )
                        }
                    } else {
                        // Email no cambió, solo actualizar
                        val usuarioUpdate = UsuarioCreateDto(
                            username = newName,
                            password = currentPassword,
                            email = newEmail,
                            phone = newPhone,
                            rolId = currentUser.rol?.id ?: 2L
                        )
                        
                        val result = update(userId, usuarioUpdate)
                        result.fold(
                            onSuccess = { usuario -> Result.success(usuario.toUserDomain(phone = newPhone)) },
                            onFailure = { error -> Result.failure(error) }
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cambiar contraseña (requiere implementación en el microservicio o usar update)
    suspend fun changePassword(userId: Long, currentPassword: String, newPassword: String): Result<UserDomain> {
        return try {
            // Obtener el usuario actual
            val currentUserResult = fetchUsuarioById(userId)
            val currentUser = currentUserResult.getOrNull()
            
            if (currentUser == null) {
                Result.failure(IllegalArgumentException("Usuario no encontrado"))
            } else {
                // Verificar la contraseña actual haciendo login
                val loginResult = login(currentUser.email, currentPassword)
                if (loginResult.isFailure) {
                    Result.failure(IllegalArgumentException("La contraseña actual no coincide"))
                } else {
                    // Verificar que la nueva contraseña sea diferente
                    if (currentPassword == newPassword) {
                        Result.failure(IllegalArgumentException("La nueva contraseña debe ser diferente a la actual"))
                    } else {
                        // Actualizar con la nueva contraseña
                        val usuarioUpdate = UsuarioCreateDto(
                            username = currentUser.username,
                            password = newPassword,
                            email = currentUser.email,
                            phone = currentUser.phone ?: "",
                            rolId = currentUser.rol?.id ?: 2L
                        )
                        
                        val result = update(userId, usuarioUpdate)
                        result.fold(
                            onSuccess = { usuario -> Result.success(usuario.toUserDomain()) },
                            onFailure = { error -> Result.failure(error) }
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar foto de perfil (esto se maneja localmente, no en el microservicio)
    suspend fun updateProfilePhoto(userId: Long, photoPath: String?): Result<UserDomain> {
        return try {
            val currentUserResult = fetchUsuarioById(userId)
            val currentUser = currentUserResult.getOrNull()
            
            if (currentUser == null) {
                Result.failure(IllegalArgumentException("Usuario no encontrado"))
            } else {
                // La foto se guarda localmente, solo retornamos el usuario con la nueva foto
                Result.success(currentUser.toUserDomain(photoUrl = photoPath))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}