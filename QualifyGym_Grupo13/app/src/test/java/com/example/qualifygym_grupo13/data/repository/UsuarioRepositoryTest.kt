package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.remote.UsuarioApi
import com.example.qualifygym_grupo13.data.remote.dto.RolDto
import com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertFalse
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

//Test del microservicio de Usuarios
class UsuarioRepositoryTest {

    // ========== Tests para fetchUsuarios ==========//
    @Test
    fun fetchUsuarios_devuelve_lista_ok() = runBlocking {
        // Valida: Obtener lista de usuarios exitosamente
        // Retorna: Result.success con lista de usuarios
        val api = mockk<UsuarioApi>()
        val repo = UsuarioRepository(api)
        val sample = listOf(
            UsuarioDto(1, "pepe", "pepe@gmail.com", "123698547", RolDto(2, "Usuario"), null)
        )

        coEvery { api.getusuarios() } returns sample
        val result = repo.fetchUsuarios()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("pepe", result.getOrNull()!![0].username)
    }

    @Test
    fun fetchUsuarios_maneja_error() = runBlocking {
        // Valida: Manejo de error al obtener usuarios
        // Retorna: Result.failure
        val api = mockk<UsuarioApi>()
        val repo = UsuarioRepository(api)

        coEvery { api.getusuarios() } throws Exception("Error de red")
        val result = repo.fetchUsuarios()
        assertTrue(result.isFailure)
    }

    // ========== Tests para fetchUsuarioById ==========//
    @Test
    fun fetchUsuarioById_devuelve_usuario_ok() = runBlocking {
        // Valida: Obtener usuario por ID exitosamente
        // Retorna: Result.success con UsuarioDto
        val api = mockk<UsuarioApi>()
        val repo = UsuarioRepository(api)
        val usuario = UsuarioDto(1, "pepe", "pepe@gmail.com", "123698547", RolDto(2, "Usuario"), null)

        coEvery { api.getUsuarioById(1) } returns usuario
        val result = repo.fetchUsuarioById(1)
        assertTrue(result.isSuccess)
        assertEquals("pepe", result.getOrNull()!!.username)
    }

    @Test
    fun fetchUsuarioById_maneja_error() = runBlocking {
        // Valida: Manejo de error al obtener usuario por ID
        // Retorna: Result.failure
        val api = mockk<UsuarioApi>()
        val repo = UsuarioRepository(api)

        coEvery { api.getUsuarioById(1) } throws Exception("Usuario no encontrado")
        val result = repo.fetchUsuarioById(1)
        assertTrue(result.isFailure)
    }

    // ========== Tests para findUsuarioByEmail ==========//
    @Test
    fun findUsuarioByEmail_encontrado() = runBlocking {
        // Valida: Buscar usuario por email encontrado
        // Retorna: Result.success con UsuarioDto
        val api = mockk<UsuarioApi>()
        val repo = UsuarioRepository(api)
        val usuario = UsuarioDto(1, "pepe", "pepe@gmail.com", "123698547", RolDto(2, "Usuario"), null)

        coEvery { api.getUsuarioByEmail("pepe@gmail.com") } returns usuario
        val result = repo.findUsuarioByEmail("pepe@gmail.com")
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals("pepe", result.getOrNull()!!.username)
    }

    @Test
    fun findUsuarioByEmail_no_encontrado() = runBlocking {
        // Valida: Buscar usuario por email no encontrado
        // Retorna: Result.success con null
        val api = mockk<UsuarioApi>()
        val repo = UsuarioRepository(api)
        val errorResponse = Response.error<UsuarioDto>(404, "Not found".toResponseBody("application/json".toMediaType()))

        coEvery { api.getUsuarioByEmail("noexiste@gmail.com") } throws HttpException(errorResponse)
        val result = repo.findUsuarioByEmail("noexiste@gmail.com")
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    // ========== Tests para findUsuarioByPhone ==========//
    @Test
    fun findUsuarioByPhone_encontrado() = runBlocking {
        // Valida: Buscar usuario por teléfono encontrado
        // Retorna: Result.success con UsuarioDto
        val api = mockk<UsuarioApi>()
        val repo = UsuarioRepository(api)
        val usuarios = listOf(
            UsuarioDto(1, "pepe", "pepe@gmail.com", "123456789", RolDto(2, "Usuario"), null)
        )

        coEvery { api.getusuarios() } returns usuarios
        val result = repo.findUsuarioByPhone("123456789")
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals("pepe", result.getOrNull()!!.username)
    }

    @Test
    fun findUsuarioByPhone_no_encontrado() = runBlocking {
        // Valida: Buscar usuario por teléfono no encontrado
        // Retorna: Result.success con null
        val api = mockk<UsuarioApi>()
        val repo = UsuarioRepository(api)
        val usuarios = listOf<UsuarioDto>()

        coEvery { api.getusuarios() } returns usuarios
        val result = repo.findUsuarioByPhone("999999999")
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    // ========== Tests para toUserDomain ==========//
    @Test
    fun toUserDomain_convierte_correctamente() = runBlocking {
        // Valida: Conversión de UsuarioDto a UserDomain
        // Retorna: UserDomain con datos correctos
        val usuarioDto = UsuarioDto(1, "pepe", "pepe@gmail.com", "123456789", RolDto(2, "Usuario"), "photo123")
        val userDomain = usuarioDto.toUserDomain()

        assertEquals(1L, userDomain.id)
        assertEquals("pepe", userDomain.name)
        assertEquals("pepe@gmail.com", userDomain.email)
        assertEquals("123456789", userDomain.phone)
        assertEquals("photo123", userDomain.photoUrl)
        assertFalse(userDomain.isAdmin)
        assertFalse(userDomain.isModerator)
    }

    @Test
    fun toUserDomain_admin() = runBlocking {
        // Valida: Conversión de UsuarioDto con rol Administrador
        // Retorna: UserDomain con isAdmin = true
        val usuarioDto = UsuarioDto(1, "admin", "admin@gmail.com", "123456789", RolDto(1, "Administrador"), null)
        val userDomain = usuarioDto.toUserDomain()

        assertTrue(userDomain.isAdmin)
        assertFalse(userDomain.isModerator)
    }

    @Test
    fun toUserDomain_moderador() = runBlocking {
        // Valida: Conversión de UsuarioDto con rol Moderador
        // Retorna: UserDomain con isModerator = true
        val usuarioDto = UsuarioDto(1, "mod", "mod@gmail.com", "123456789", RolDto(3, "Moderador"), null)
        val userDomain = usuarioDto.toUserDomain()

        assertFalse(userDomain.isAdmin)
        assertTrue(userDomain.isModerator)
    }
}
