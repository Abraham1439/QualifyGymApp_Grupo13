package com.example.qualifygym_grupo13.data.remote

import com.example.qualifygym_grupo13.data.remote.dto.EstadoDto
import com.example.qualifygym_grupo13.data.remote.dto.EstadoCreateDto
import com.example.qualifygym_grupo13.data.remote.dto.EstadoUpdateDto
import retrofit2.Response
import retrofit2.http.*

interface EstadoApi {
    // GET /api/v1/estado/estados
    @GET("api/v1/estado/estados")
    suspend fun getEstados(): List<EstadoDto>

    // GET /api/v1/estado/estados/{id}
    @GET("api/v1/estado/estados/{id}")
    suspend fun getEstadoById(@Path("id") id: Long): EstadoDto

    // GET /api/v1/estado/estados/nombre/{nombre}
    @GET("api/v1/estado/estados/nombre/{nombre}")
    suspend fun getEstadoPorNombre(@Path("nombre") nombre: String): EstadoDto

    // GET /api/v1/estado/estados/existe/{nombre}
    @GET("api/v1/estado/estados/existe/{nombre}")
    suspend fun existeEstadoPorNombre(@Path("nombre") nombre: String): Boolean

    // POST /api/v1/estado/estados
    @POST("api/v1/estado/estados")
    suspend fun crearEstado(@Body estado: EstadoCreateDto): EstadoDto

    // POST /api/v1/estado/estados/obtener-o-crear
    @POST("api/v1/estado/estados/obtener-o-crear")
    suspend fun obtenerOCrearEstado(@Body estado: EstadoCreateDto): EstadoDto

    // PUT /api/v1/estado/estados/{id}
    @PUT("api/v1/estado/estados/{id}")
    suspend fun actualizarEstado(
        @Path("id") id: Long,
        @Body estado: EstadoUpdateDto
    ): EstadoDto

    // DELETE /api/v1/estado/estados/{id}
    @DELETE("api/v1/estado/estados/{id}")
    suspend fun eliminarEstado(@Path("id") id: Long): Response<Unit>
}

