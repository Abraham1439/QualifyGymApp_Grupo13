package com.example.qualifygym_grupo13.ui.viewmodel

import androidx.lifecycle.ViewModel                       // Base de ViewModel
import androidx.lifecycle.viewModelScope                  // Scope de corrutinas ligado al VM
import kotlinx.coroutines.delay                            // Simulamos tareas async (IO/red)
import kotlinx.coroutines.flow.MutableStateFlow            // Estado observable mutable
import kotlinx.coroutines.flow.StateFlow                   // Exposición inmutable
import kotlinx.coroutines.flow.update                      // Helper para actualizar flows
import kotlinx.coroutines.launch                            // Lanzar corrutinas
import com.example.qualifygym_grupo13.domain.validation.*             // Importamos las funciones de validación

// 1.- 🔁 NUEVO: importamos el repositorio que habla con las APIs
import com.example.qualifygym_grupo13.data.repository.UsuarioRepository
import com.example.qualifygym_grupo13.data.domain.UserDomain
import com.example.qualifygym_grupo13.data.preferences.SessionManager
import com.example.qualifygym_grupo13.data.repository.toUserDomain

// ----------------- ESTADOS DE UI (observable con StateFlow) -----------------

data class LoginUiState(                                   // Estado de la pantalla Login
    val email: String = "",                                // Campo email
    val pass: String = "",                                 // Campo contraseña (texto)
    val emailError: String? = null,                        // Error de email
    val passError: String? = null,                         // (Opcional) error de pass en login
    val isSubmitting: Boolean = false,                     // Flag de carga
    val canSubmit: Boolean = false,                        // Habilitar botón
    val success: Boolean = false,                          // Resultado OK
    val errorMsg: String? = null                           // Error global (credenciales inválidas)
)

data class RegisterUiState(                                // Estado de la pantalla Registro (<= 5 campos)
    val name: String = "",                                 // 1) Nombre
    val email: String = "",                                // 2) Email
    val phone: String = "",                                // 3) Teléfono
    val pass: String = "",                                 // 4) Contraseña
    val confirm: String = "",                              // 5) Confirmación

    val nameError: String? = null,                         // Errores por campo
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: List<String> = emptyList(),             //Esto se cambia a una lista que se creo en el validators
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,                     // Flag de carga
    val canSubmit: Boolean = false,                        // Habilitar botón
    val success: Boolean = false,                          // Resultado OK
    val errorMsg: String? = null                           // Error global (ej: duplicado)
)

// ----------------- COLECCIÓN EN MEMORIA (solo para la demo) -----------------

//2.- Eliminamos la estructura de DemoUser

class AuthViewModel(
    //NUEVO: 4.- inyectamos el repositorio que usa APIs
    private val repository: UsuarioRepository,
    // Gestor de sesión para persistencia simple
    private val sessionManager: SessionManager
) : ViewModel() {                         // ViewModel que maneja Login/Registro

    // 3.- Eliminamos Colección **estática** en memoria compartida entre instancias del VM (sin storage persistente)


    // Flujos de estado para observar desde la UI
    private val _login = MutableStateFlow(LoginUiState())   // Estado interno (Login)
    val login: StateFlow<LoginUiState> = _login             // Exposición inmutable

    private val _register = MutableStateFlow(RegisterUiState()) // Estado interno (Registro)
    val register: StateFlow<RegisterUiState> = _register        // Exposición inmutable
    
    // Estado del usuario actual (después de login exitoso)
    private val _currentUser = MutableStateFlow<UserDomain?>(null)
    val currentUser: StateFlow<UserDomain?> = _currentUser
    
    // Estado para indicar si se está verificando la sesión guardada
    private val _isCheckingSession = MutableStateFlow(true)
    val isCheckingSession: StateFlow<Boolean> = _isCheckingSession
    
    init {
        // Al iniciar el ViewModel, intentar restaurar la sesión guardada
        restoreSession()
    }

    // ----------------- LOGIN: handlers y envío -----------------

    fun onLoginEmailChange(value: String) {                 // Handler cuando cambia el email
        _login.update { it.copy(email = value, emailError = validateEmail(value)) } // Guardamos + validamos
        recomputeLoginCanSubmit()                           // Recalculamos habilitado
    }

    fun onLoginPassChange(value: String) {                  // Handler cuando cambia la contraseña
        _login.update { it.copy(pass = value) }             // Guardamos (sin validar fuerza aquí)
        recomputeLoginCanSubmit()                           // Recalculamos habilitado
    }

    private fun recomputeLoginCanSubmit() {                 // Regla para habilitar botón "Entrar"
        val s = _login.value                                // Tomamos el estado actual
        val can = s.emailError == null &&                   // Email válido
                s.email.isNotBlank() &&                   // Email no vacío
                s.pass.isNotBlank()                       // Password no vacía
        _login.update { it.copy(canSubmit = can) }          // Actualizamos el flag
    }

    fun submitLogin() {                                     // Acción de login (simulación async)
        val s = _login.value                                // Snapshot del estado
        if (!s.canSubmit || s.isSubmitting) return          // Si no se puede o ya está cargando, salimos
        viewModelScope.launch {                             // Lanzamos corrutina
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) } // Seteamos loading
            delay(500)                                      // Simulamos tiempo de verificación

            //6.- Se cambia lo anterior por esto  NUEVO: consulta real a la BD vía repositorio
            val result = repository.login(s.email.trim(), s.pass)

            // Interpreta el resultado y actualiza estado
            _login.update {
                if (result.isSuccess) {
                    // Guardar el usuario actual cuando el login sea exitoso
                    val user = result.getOrNull()
                    _currentUser.value = user
                    
                    // NUEVO: Guardar la sesión en SharedPreferences
                    user?.let {
                        sessionManager.saveUserSession(it.id, it.email)
                    }
                    
                    it.copy(isSubmitting = false, success = true, errorMsg = null) // OK: éxito
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación")
                }
            }
        }
    }
    
    /**
     * Restaura la sesión guardada al iniciar la app
     * Maneja errores de red de forma más elegante
     */
    private fun restoreSession() {
        viewModelScope.launch {
            try {
                // Verificar si hay sesión guardada
                val userId = sessionManager.getUserId()
                val userEmail = sessionManager.getUserEmail()
                
                if (userId != null && sessionManager.isLoggedIn() && userEmail != null) {
                    // Intentar obtener el usuario del servidor
                    val user = repository.getUserById(userId)
                    
                    if (user != null) {
                        // Usuario encontrado, restaurar sesión
                        _currentUser.value = user
                    } else {
                        // Si no se pudo obtener (puede ser error de red o usuario no existe)
                        // Intentar obtener por email como fallback
                        try {
                            val emailResult = repository.findUsuarioByEmail(userEmail)
                            val userByEmail = emailResult.getOrNull()
                            
                            if (userByEmail != null) {
                                _currentUser.value = userByEmail.toUserDomain()
                                // Actualizar el ID de sesión por si cambió
                                sessionManager.saveUserSession(userByEmail.id, userEmail)
                            } else {
                                // No se encontró el usuario, puede ser que no hay conexión
                                // Mantener la sesión pero sin usuario (se pedirá login si es necesario)
                                // No limpiamos la sesión para no forzar login si es solo un problema de red
                            }
                        } catch (e: Exception) {
                            // Error al buscar por email, mantener sesión pero sin usuario
                            // Esto permite que la app funcione aunque haya problemas de red
                        }
                    }
                }
            } catch (e: Exception) {
                // Si hay un error crítico, limpiar la sesión
                // Pero solo si es un error que no es de red
                if (e !is java.net.UnknownHostException && 
                    e !is java.net.SocketTimeoutException && 
                    e !is java.net.ConnectException) {
                    sessionManager.clearSession()
                }
            } finally {
                _isCheckingSession.value = false
            }
        }
    }

    fun clearLoginResult() {                                // Limpia banderas tras navegar
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO: handlers y envío -----------------

    fun onNameChange(value: String) {                       // Handler del nombre
        val filtered = value.filter { it.isLetter() || it.isWhitespace() } // Filtramos números/símbolos (solo letras/espacios)
        _register.update {                                  // Guardamos + validamos
            it.copy(name = filtered, nameError = validateNameLettersOnly(filtered))
        }
        recomputeRegisterCanSubmit()                        // Recalculamos habilitado
    }

    fun onRegisterEmailChange(value: String) {              // Handler del email
        _register.update { it.copy(email = value, emailError = validateEmail(value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {                      // Handler del teléfono
        val digitsOnly = value.filter { it.isDigit() }      // Dejamos solo dígitos
        _register.update {                                  // Guardamos + validamos
            it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {               // Handler de la contraseña
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) } // Validamos seguridad
        // Revalidamos confirmación con la nueva contraseña
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {                    // Handler de confirmación
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }


    //Aqui hay cambios se agrego el passError.isEmpty()
    private fun recomputeRegisterCanSubmit() {              // Habilitar "Registrar" si todo OK
        val s = _register.value                              // Tomamos el estado actual

        //Comprobar los errores del String
        val noStringErrors = s.nameError == null && s.emailError == null && s.phoneError == null && s.confirmError == null

        val noPassErrors = s.passError.isEmpty() //Sin errores

        val filled = s.name.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank() // Todo lleno

        //Boton habilitado si no hay errores y todo lleno
        _register.update { it.copy(canSubmit = noStringErrors && noPassErrors && filled) } // Actualizamos flag (boton :v)
    }

    fun submitRegister() {                                  // Acción de registro (simulación async)
        val s = _register.value                              // Snapshot del estado
        if (!s.canSubmit || s.isSubmitting) return          // Evitamos reentradas
        viewModelScope.launch {                             // Corrutina
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) } // Loading
            delay(700)                                      // Simulamos IO

            // 7.- Se cambia esto por lo anterior NUEVO: inserta en BD (con teléfono) vía repositorio
            val result = repository.register(
                name = s.name.trim(),
                email = s.email.trim(),
                phone = s.phone.trim(),                     // Incluye teléfono
                password = s.pass
            )

            // Interpreta resultado y actualiza estado
            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)  // OK
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar")
                }
            }
        }
    }

    // Actualizar perfil del usuario (requiere contraseña actual para validar)
    suspend fun updateUserProfile(name: String, email: String, phone: String, currentPassword: String? = null): Result<UserDomain> {
        val user = _currentUser.value
        return if (user != null) {
            // Si no se proporciona contraseña, retornar error
            if (currentPassword == null || currentPassword.isBlank()) {
                return Result.failure(IllegalArgumentException("Se requiere la contraseña actual para actualizar el perfil"))
            }
            
            val result = repository.updateProfile(
                userId = user.id,
                newName = name.trim(),
                newEmail = email.trim(),
                newPhone = phone.trim(),
                currentPassword = currentPassword
            )
            
            // Si la actualización fue exitosa, actualizar el usuario actual en el ViewModel
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
            }
            
            result
        } else {
            Result.failure(IllegalStateException("No hay usuario en sesión"))
        }
    }

    // Cambiar contraseña del usuario
    suspend fun changeUserPassword(currentPassword: String, newPassword: String): Result<UserDomain> {
        val user = _currentUser.value
        return if (user != null) {
            val result = repository.changePassword(
                userId = user.id,
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            
            // Si el cambio fue exitoso, actualizar el usuario actual en el ViewModel
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
            }
            
            result
        } else {
            Result.failure(IllegalStateException("No hay usuario en sesión"))
        }
    }

    // Actualizar foto de perfil del usuario
    suspend fun updateUserProfilePhoto(photoPath: String?): Result<UserDomain> {
        val user = _currentUser.value
        return if (user != null) {
            val result = repository.updateProfilePhoto(
                userId = user.id,
                photoPath = photoPath
            )
            
            // Si la actualización fue exitosa, actualizar el usuario actual en el ViewModel
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
            }
            
            result
        } else {
            Result.failure(IllegalStateException("No hay usuario en sesión"))
        }
    }

    fun clearLoginData() {
        _login.update {
            LoginUiState() // Esto restablece todos los valores a los defaults
        }
        _currentUser.value = null // Limpiar usuario actual al cerrar sesión
    }

    //limpiar el registro al logout
    fun clearAllAuthData() {
        _login.update { LoginUiState() }
        _register.update { RegisterUiState() }
        _currentUser.value = null // Limpiar usuario actual
        
        // NUEVO: Limpiar la sesión guardada
        sessionManager.clearSession()
    }
}
