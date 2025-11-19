package com.example.qualifygym_grupo13.data.remote

import com.example.qualifygym_grupo13.data.remote.dto.LoginRequestDto
import com.example.qualifygym_grupo13.data.remote.dto.UsuarioCreateDto
import com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto
import com.example.qualifygym_grupo13.data.remote.dto.UsuarioRegisterDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuarioApi {
    // GET /api/v1/usuario/users
    @GET("api/v1/usuario/users")
    suspend fun getusuarios(): List<UsuarioDto>

    // GET /api/v1/usuario/users/{id}
    @GET("api/v1/usuario/users/{id}")
    suspend fun getUsuarioById(@Path("id") id: Long): UsuarioDto

    // GET /api/v1/usuario/users/email?email={email}
    @GET("api/v1/usuario/users/email")
    suspend fun getUsuarioByEmail(@retrofit2.http.Query("email") email: String): UsuarioDto

    // POST /api/v1/usuario/users (endpoint privado - requiere autenticación)
    @POST("api/v1/usuario/users")
    suspend fun crearUsuario(@Body usuario: UsuarioCreateDto): UsuarioDto

    // POST /api/v1/usuario/register (endpoint público - sin autenticación, no requiere rolId)
    @POST("api/v1/usuario/register")
    suspend fun registrarUsuarioPublico(@Body usuario: UsuarioRegisterDto): UsuarioDto

    // PUT /api/v1/usuario/users/{id}
    @PUT("api/v1/usuario/users/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: UsuarioCreateDto
    ): UsuarioDto

    // DELETE /api/v1/usuario/users/{id}
    @DELETE("api/v1/usuario/users/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long): Response<Unit>

    // POST /api/v1/usuario/login
    @POST("api/v1/usuario/login")
    suspend fun login(@Body loginRequest: LoginRequestDto): Response<String>
}