package com.example.qualifygym_grupo13.ui.viewmodel


import com.example.qualifygym_grupo13.data.preferences.SessionManager
import com.example.qualifygym_grupo13.data.repository.UsuarioRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthViewModelTest {

    private lateinit var repository: UsuarioRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        repository = mockk<UsuarioRepository>(relaxed = true)
        // Configurar valores por defecto para métodos suspend que retornan Result
        // Usamos any() aquí porque es necesario para que los tests funcionen correctamente
        coEvery { repository.findUsuarioByEmail(any()) } returns Result.success(null)
        coEvery { repository.findUsuarioByPhone(any()) } returns Result.success(null)
        coEvery { repository.getUserById(any()) } returns null
        // Configurar valores por defecto para métodos que se usan en múltiples tests
        coEvery { repository.register(any(), any(), any(), any()) } returns Result.success(1L)
        coEvery { repository.login(any(), any()) } returns Result.success(
            com.example.qualifygym_grupo13.data.domain.UserDomain(
                id = 1L,
                name = "Test User",
                email = "test@example.com",
                phone = "123456789",
                isAdmin = false,
                isModerator = false
            )
        )
        coEvery { repository.updateProfile(any(), any(), any(), any(), any()) } returns Result.success(
            com.example.qualifygym_grupo13.data.domain.UserDomain(
                id = 1L,
                name = "Updated",
                email = "updated@example.com",
                phone = "987654321",
                isAdmin = false,
                isModerator = false
            )
        )
        coEvery { repository.changePassword(any(), any(), any()) } returns Result.success(
            com.example.qualifygym_grupo13.data.domain.UserDomain(
                id = 1L,
                name = "Test User",
                email = "test@example.com",
                phone = "123456789",
                isAdmin = false,
                isModerator = false
            )
        )
        coEvery { repository.updateProfilePhoto(any(), any()) } returns Result.success(
            com.example.qualifygym_grupo13.data.domain.UserDomain(
                id = 1L,
                name = "Test User",
                email = "test@example.com",
                phone = "123456789",
                isAdmin = false,
                isModerator = false,
                photoUrl = "photo123"
            )
        )
        sessionManager = SessionManager(RuntimeEnvironment.getApplication())
        viewModel = AuthViewModel(repository, sessionManager)
    }

    // ========== Tests para onLoginEmailChange ==========//
    @Test
    fun onLoginEmailChange_valida_email_invalido() {
        // Valida: Validación de email inválido en tiempo real
        // Retorna: Actualiza emailError con mensaje de error
        viewModel.onLoginEmailChange("invalid-email")
        val state = viewModel.login.value
        assertNotNull(state.emailError)
        assertEquals("invalid-email", state.email)
    }

    @Test
    fun onLoginEmailChange_valida_email_valido() {
        // Valida: Validación de email válido en tiempo real
        // Retorna: emailError es null
        viewModel.onLoginEmailChange("test@example.com")
        val state = viewModel.login.value
        assertNull(state.emailError)
        assertEquals("test@example.com", state.email)
    }

    // ========== Tests para onLoginPassChange ==========//
    @Test
    fun onLoginPassChange_actualiza_password() {
        // Valida: Actualización de contraseña en login
        // Retorna: pass actualizado en el estado
        viewModel.onLoginPassChange("password123")
        val state = viewModel.login.value
        assertEquals("password123", state.pass)
    }

    // ========== Tests para recomputeLoginCanSubmit ==========//
    @Test
    fun recomputeLoginCanSubmit_habilitado_con_datos_validos() {
        // Valida: Botón habilitado con email y password válidos
        // Retorna: canSubmit = true
        viewModel.onLoginEmailChange("test@example.com")
        viewModel.onLoginPassChange("password123")
        val state = viewModel.login.value
        assertTrue(state.canSubmit)
    }

    @Test
    fun recomputeLoginCanSubmit_deshabilitado_sin_email() {
        // Valida: Botón deshabilitado sin email
        // Retorna: canSubmit = false
        viewModel.onLoginPassChange("password123")
        val state = viewModel.login.value
        assertFalse(state.canSubmit)
    }

    @Test
    fun recomputeLoginCanSubmit_deshabilitado_sin_password() {
        // Valida: Botón deshabilitado sin password
        // Retorna: canSubmit = false
        viewModel.onLoginEmailChange("test@example.com")
        val state = viewModel.login.value
        assertFalse(state.canSubmit)
    }

    // ========== Tests para submitLogin ==========//
    @Test
    fun submitLogin_inicia_proceso() {
        // Valida: submitLogin inicia el proceso de login
        // Retorna: isSubmitting = true cuando se inicia
        viewModel.onLoginEmailChange("pepito@gmail.com")
        viewModel.onLoginPassChange("Wena123.")
        
        val stateBefore = viewModel.login.value
        assertFalse("Antes de submitLogin, isSubmitting debería ser false", stateBefore.isSubmitting)
        
        viewModel.submitLogin()
        
        val stateAfter = viewModel.login.value
        assertTrue("Después de submitLogin, isSubmitting debería ser true", stateAfter.isSubmitting)
    }

    @Test
    fun submitLogin_no_ejecuta_si_no_puede_submitir() {
        // Valida: submitLogin no ejecuta si canSubmit es false
        // Retorna: isSubmitting permanece false
        viewModel.onLoginEmailChange("") // Email vacío, canSubmit será false
        viewModel.onLoginPassChange("")
        
        val stateBefore = viewModel.login.value
        assertFalse("canSubmit debería ser false", stateBefore.canSubmit)
        
        viewModel.submitLogin()
        
        val stateAfter = viewModel.login.value
        assertFalse("isSubmitting debería permanecer false", stateAfter.isSubmitting)
    }

    // ========== Tests para onNameChange ==========//
    @Test
    fun onNameChange_filtra_numeros() {
        // Valida: Filtrado de números en nombre
        // Retorna: Solo letras y espacios en el nombre
        viewModel.onNameChange("Juan123")
        val state = viewModel.register.value
        assertEquals("Juan", state.name)
    }

    @Test
    fun onNameChange_valida_nombre_valido() {
        // Valida: Validación de nombre válido
        // Retorna: nameError es null
        viewModel.onNameChange("Juan Pérez")
        val state = viewModel.register.value
        assertNull(state.nameError)
    }

    // ========== Tests para onRegisterEmailChange ==========//
    @Test
    fun onRegisterEmailChange_valida_email_valido() {
        // Valida: Validación de email válido en registro
        // Retorna: emailError es null
        viewModel.onRegisterEmailChange("test@example.com")
        val state = viewModel.register.value
        assertEquals("test@example.com", state.email)
    }

    // ========== Tests para onPhoneChange ==========//
    @Test
    fun onPhoneChange_filtra_solo_digitos() {
        // Valida: Filtrado de solo dígitos en teléfono
        // Retorna: Solo números en phone
        viewModel.onPhoneChange("123-456-789")
        val state = viewModel.register.value
        assertEquals("123456789", state.phone)
    }

    // ========== Tests para onRegisterPassChange ==========//
    @Test
    fun onRegisterPassChange_valida_password_fuerte() {
        // Valida: Validación de contraseña fuerte
        // Retorna: passError lista vacía si es válida
        viewModel.onRegisterPassChange("Password123!")
        val state = viewModel.register.value
        assertTrue(state.passError.isEmpty())
    }

    @Test
    fun onRegisterPassChange_valida_password_debil() {
        // Valida: Validación de contraseña débil
        // Retorna: passError con mensajes de error
        viewModel.onRegisterPassChange("weak")
        val state = viewModel.register.value
        assertTrue(state.passError.isNotEmpty())
    }

    // ========== Tests para onConfirmChange ==========//
    @Test
    fun onConfirmChange_valida_coincidencia() {
        // Valida: Validación de coincidencia de contraseñas
        // Retorna: confirmError es null si coinciden
        viewModel.onRegisterPassChange("Password123!")
        viewModel.onConfirmChange("Password123!")
        val state = viewModel.register.value
        assertNull(state.confirmError)
    }

    @Test
    fun onConfirmChange_valida_no_coincidencia() {
        // Valida: Validación de no coincidencia de contraseñas
        // Retorna: confirmError con mensaje
        viewModel.onRegisterPassChange("Password123!")
        viewModel.onConfirmChange("Different123!")
        val state = viewModel.register.value
        assertNotNull(state.confirmError)
    }

    // ========== Tests para recomputeRegisterCanSubmit ==========//
    @Test
    fun recomputeRegisterCanSubmit_habilitado_con_todo_valido() {
        // Valida: Botón habilitado con todos los campos válidos
        // Retorna: canSubmit = true
        viewModel.onNameChange("Juan Pérez")
        viewModel.onRegisterEmailChange("test@example.com")
        viewModel.onPhoneChange("123456789")
        viewModel.onRegisterPassChange("Password123!")
        viewModel.onConfirmChange("Password123!")
        
        val state = viewModel.register.value
        assertTrue(state.canSubmit)
    }

    // ========== Tests para clearLoginData ==========//
    @Test
    fun clearLoginData_limpia_estado() {
        // Valida: Limpieza de datos de login
        // Retorna: Estado reseteado a valores por defecto
        viewModel.onLoginEmailChange("test@example.com")
        viewModel.onLoginPassChange("password123")
        viewModel.clearLoginData()
        
        val state = viewModel.login.value
        assertEquals("", state.email)
        assertEquals("", state.pass)
        assertNull(viewModel.currentUser.value)
    }

    // ========== Tests para clearAllAuthData ==========//
    @Test
    fun clearAllAuthData_limpia_todo() {
        // Valida: Limpieza de todos los datos de autenticación
        // Retorna: Estados de login y registro reseteados
        viewModel.onLoginEmailChange("test@example.com")
        viewModel.onNameChange("Juan")
        viewModel.clearAllAuthData()
        
        val loginState = viewModel.login.value
        val registerState = viewModel.register.value
        
        assertEquals("", loginState.email)
        assertEquals("", registerState.name)
    }

    /*// ========== Tests para submitRegister ==========//
    @Test
    fun submitRegister_exitoso() = runBlocking {
        // Valida: Registro exitoso de usuario
        // Retorna: success = true
        viewModel.onNameChange("Juan")
        viewModel.onRegisterEmailChange("juan@example.com")
        viewModel.onPhoneChange("123456789")
        viewModel.onRegisterPassChange("Password123!")
        viewModel.onConfirmChange("Password123!")
        
        // El mock del setup ya está configurado para retornar éxito
        // Esperar a que se complete la validación de email y teléfono (debounce de 500ms)
        delay(600)
        
        // Verificar que canSubmit sea true antes de registrar
        val stateBefore = viewModel.register.value
        assertTrue("canSubmit debería ser true. Estado: $stateBefore", stateBefore.canSubmit)
        assertFalse("isSubmitting debería ser false antes", stateBefore.isSubmitting)
        
        viewModel.submitRegister()
        // El ViewModel tiene un delay de 700ms + tiempo de ejecución
        // Esperar hasta que isSubmitting sea false (máximo 10 segundos)
        var attempts = 0
        while (viewModel.register.value.isSubmitting && attempts < 100) {
            delay(100)
            attempts++
        }
        
        val stateAfter = viewModel.register.value
        assertFalse("isSubmitting debería ser false después de completar. Intentos: $attempts", stateAfter.isSubmitting)
        assertTrue("El registro debería ser exitoso. Estado: success=${stateAfter.success}, errorMsg=${stateAfter.errorMsg}", stateAfter.success)
    }*/

    /*@Test
    fun submitRegister_error() = runBlocking {
        // Valida: Error en registro de usuario
        // Retorna: success = false, errorMsg no null
        viewModel.onNameChange("Juan")
        viewModel.onRegisterEmailChange("juan@example.com")
        viewModel.onPhoneChange("123456789")
        viewModel.onRegisterPassChange("Password123!")
        viewModel.onConfirmChange("Password123!")
        
        // Configurar el mock para retornar error en el registro
        coEvery { repository.register(any(), any(), any(), any()) } returns Result.failure(Exception("Error de registro"))
        
        delay(600)
        
        viewModel.submitRegister()
        // El ViewModel tiene un delay de 700ms + tiempo de ejecución
        delay(2000) // Aumentar delay para asegurar que se complete la corrutina
        
        assertFalse("El registro debería fallar", viewModel.register.value.success)
        assertNotNull("Debería haber un mensaje de error", viewModel.register.value.errorMsg)
    }*/

    // ========== Tests para updateUserProfile ==========//
    @Test
    fun updateUserProfile_sin_usuario() = runBlocking {
        // Valida: Error al actualizar perfil sin usuario en sesión
        // Retorna: Result.failure
        val result = viewModel.updateUserProfile("Juan", "juan@example.com", "123456789")
        
        assertTrue(result.isFailure)
    }

    /*@Test
    fun updateUserProfile_exitoso() = runBlocking {
        // Valida: Actualizar perfil exitosamente con usuario en sesión
        // Retorna: Result.success con UserDomain actualizado
        val userDomain = com.example.qualifygym_grupo13.data.domain.UserDomain(
            id = 1L,
            name = "Juan Original",
            email = "juan@example.com",
            phone = "123456789",
            isAdmin = false,
            isModerator = false
        )
        val updatedUserDomain = com.example.qualifygym_grupo13.data.domain.UserDomain(
            id = 1L,
            name = "Juan Actualizado",
            email = "juan.nuevo@example.com",
            phone = "987654321",
            isAdmin = false,
            isModerator = false
        )
        
        // Configurar el mock para el login con el usuario específico
        coEvery { repository.login(any(), any()) } returns Result.success(userDomain)
        // Configurar el mock para retornar el usuario actualizado
        coEvery { repository.updateProfile(1L, any(), any(), any(), any()) } returns Result.success(updatedUserDomain)
        
        viewModel.onLoginEmailChange("juan@example.com")
        viewModel.onLoginPassChange("password123")
        viewModel.submitLogin()
        
        // Esperar a que el login se complete
        delay(800)
        
        val result = viewModel.updateUserProfile("Juan Actualizado", "juan.nuevo@example.com", "987654321")
        
        assertTrue(result.isSuccess)
        assertEquals("Juan Actualizado", result.getOrNull()!!.name)
        assertEquals("juan.nuevo@example.com", result.getOrNull()!!.email)
    }*/

    // ========== Tests para changeUserPassword ==========//
    @Test
    fun changeUserPassword_sin_usuario() = runBlocking {
        // Valida: Error al cambiar contraseña sin usuario en sesión
        // Retorna: Result.failure
        val result = viewModel.changeUserPassword("OldPass123!", "NewPass123!")
        
        assertTrue(result.isFailure)
    }

    /*@Test
    fun changeUserPassword_exitoso() = runBlocking {
        // Valida: Cambiar contraseña exitosamente con usuario en sesión
        // Retorna: Result.success con UserDomain actualizado
        val userDomain = com.example.qualifygym_grupo13.data.domain.UserDomain(
            id = 1L,
            name = "Juan",
            email = "juan@example.com",
            phone = "123456789",
            isAdmin = false,
            isModerator = false
        )
        val updatedUserDomain = com.example.qualifygym_grupo13.data.domain.UserDomain(
            id = 1L,
            name = "Juan",
            email = "juan@example.com",
            phone = "123456789",
            isAdmin = false,
            isModerator = false
        )
        
        // Configurar el mock para el login con el usuario específico
        coEvery { repository.login(any(), any()) } returns Result.success(userDomain)
        // Configurar el mock para retornar el usuario actualizado
        coEvery { repository.changePassword(1L, any(), any()) } returns Result.success(updatedUserDomain)
        
        viewModel.onLoginEmailChange("juan@example.com")
        viewModel.onLoginPassChange("password123")
        viewModel.submitLogin()
        
        // Esperar a que el login se complete
        delay(800)
        
        val result = viewModel.changeUserPassword("OldPass123!", "NewPass123!")
        
        assertTrue(result.isSuccess)
        assertEquals(userDomain.id, result.getOrNull()!!.id)
    }*/

    // ========== Tests para updateUserProfilePhoto ==========//
    @Test
    fun updateUserProfilePhoto_sin_usuario() = runBlocking {
        // Valida: Error al actualizar foto sin usuario en sesión
        // Retorna: Result.failure
        val result = viewModel.updateUserProfilePhoto("photo123")
        
        assertTrue(result.isFailure)
    }

    /*@Test
    fun updateUserProfilePhoto_exitoso() = runBlocking {
        // Valida: Actualizar foto de perfil exitosamente con usuario en sesión
        // Retorna: Result.success con UserDomain actualizado
        val userDomain = com.example.qualifygym_grupo13.data.domain.UserDomain(
            id = 1L,
            name = "Juan",
            email = "juan@example.com",
            phone = "123456789",
            isAdmin = false,
            isModerator = false,
            photoUrl = null
        )
        val updatedUserDomain = com.example.qualifygym_grupo13.data.domain.UserDomain(
            id = 1L,
            name = "Juan",
            email = "juan@example.com",
            phone = "123456789",
            isAdmin = false,
            isModerator = false,
            photoUrl = "photo123"
        )
        
        // Configurar el mock para el login con el usuario específico
        coEvery { repository.login(any(), any()) } returns Result.success(userDomain)
        // Configurar el mock para retornar el usuario con foto actualizada
        coEvery { repository.updateProfilePhoto(1L, any()) } returns Result.success(updatedUserDomain)
        
        viewModel.onLoginEmailChange("juan@example.com")
        viewModel.onLoginPassChange("password123")
        viewModel.submitLogin()
        
        // Esperar a que el login se complete
        delay(800)
        
        val result = viewModel.updateUserProfilePhoto("photo123")
        
        assertTrue(result.isSuccess)
        assertEquals("photo123", result.getOrNull()!!.photoUrl)
    }*/

    // ========== Tests para isCurrentUserModerator ==========//
    @Test
    fun isCurrentUserModerator_sin_usuario() = runBlocking {
        // Valida: No hay usuario en sesión
        // Retorna: false
        val result = viewModel.isCurrentUserModerator()
        
        assertFalse(result)
    }

    /*@Test
    fun isCurrentUserModerator_es_moderador() = runBlocking {
        // Valida: Usuario es moderador
        // Retorna: true
        val userDomain = com.example.qualifygym_grupo13.data.domain.UserDomain(
            id = 1L,
            name = "Moderador",
            email = "mod@example.com",
            phone = "123456789",
            isAdmin = false,
            isModerator = true
        )
        val usuarioDto = com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto(
            id = 1L,
            username = "Moderador",
            email = "mod@example.com",
            phone = "123456789",
            rol = com.example.qualifygym_grupo13.data.remote.dto.RolDto(2, "Moderador")
        )
        
        // Configurar el mock para el login con el usuario específico
        coEvery { repository.login(any(), any()) } returns Result.success(userDomain)
        // Configurar el mock para retornar el usuario con rol de moderador
        coEvery { repository.findUsuarioByEmail("mod@example.com") } returns Result.success(usuarioDto)
        
        viewModel.onLoginEmailChange("mod@example.com")
        viewModel.onLoginPassChange("password123")
        viewModel.submitLogin()
        
        // Esperar a que el login se complete
        delay(800)
        
        val result = viewModel.isCurrentUserModerator()
        
        assertTrue(result)
    }*/

    @Test
    fun isCurrentUserModerator_no_es_moderador() = runBlocking {
        // Valida: Usuario no es moderador
        // Retorna: false
        val userDomain = com.example.qualifygym_grupo13.data.domain.UserDomain(
            id = 1L,
            name = "Usuario",
            email = "user@example.com",
            phone = "123456789",
            isAdmin = false,
            isModerator = false
        )
        val usuarioDto = com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto(
            id = 1L,
            username = "Usuario",
            email = "user@example.com",
            phone = "123456789",
            rol = com.example.qualifygym_grupo13.data.remote.dto.RolDto(3, "Usuario")
        )
        
        // Configurar el mock para el login con el usuario específico
        coEvery { repository.login(any(), any()) } returns Result.success(userDomain)
        // Configurar el mock para retornar el usuario sin rol de moderador
        coEvery { repository.findUsuarioByEmail("user@example.com") } returns Result.success(usuarioDto)
        
        viewModel.onLoginEmailChange("user@example.com")
        viewModel.onLoginPassChange("password123")
        viewModel.submitLogin()
        
        // Esperar a que el login se complete
        delay(800)
        
        val result = viewModel.isCurrentUserModerator()
        
        assertFalse(result)
    }

    // ========== Tests para clearLoginResult ==========//
    @Test
    fun clearLoginResult_limpia_estado() {
        // Valida: Limpiar resultado de login
        // Retorna: Estado de login reseteado
        // Primero establecer un estado de éxito
        viewModel.onLoginEmailChange("test@example.com")
        viewModel.onLoginPassChange("password123")
        
        viewModel.clearLoginResult()
        
        val state = viewModel.login.value
        assertFalse(state.success)
        assertNull(state.errorMsg)
    }
}