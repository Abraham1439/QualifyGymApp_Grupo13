package com.example.qualifygym_grupo13.data.remote

import com.example.qualifygym_grupo13.data.remote.dto.TemaDto
import com.example.qualifygym_grupo13.data.remote.dto.TemaCreateDto
import com.example.qualifygym_grupo13.data.remote.dto.TemaUpdateDto
import retrofit2.Response
import retrofit2.http.*

interface TemaApi {
    // GET /api/v1/tema/temas
    @GET("api/v1/tema/temas")
    suspend fun getTemas(): List<TemaDto>

    // GET /api/v1/tema/temas/{id}
    @GET("api/v1/tema/temas/{id}")
    suspend fun getTemaById(@Path("id") id: Long): TemaDto

    // GET /api/v1/tema/temas/estado/{estadoId}
    @GET("api/v1/tema/temas/estado/{estadoId}")
    suspend fun getTemasPorEstado(@Path("estadoId") estadoId: Long): List<TemaDto>

    // GET /api/v1/tema/temas/buscar?query=texto
    @GET("api/v1/tema/temas/buscar")
    suspend fun buscarTemas(@Query("query") query: String): List<TemaDto>

    // GET /api/v1/tema/temas/nombre/{nombre}
    @GET("api/v1/tema/temas/nombre/{nombre}")
    suspend fun getTemaPorNombre(@Path("nombre") nombre: String): TemaDto

    // GET /api/v1/tema/temas/existe/{nombre}
    @GET("api/v1/tema/temas/existe/{nombre}")
    suspend fun existeTemaPorNombre(@Path("nombre") nombre: String): Boolean

    // GET /api/v1/tema/temas/estado/{estadoId}/count
    @GET("api/v1/tema/temas/estado/{estadoId}/count")
    suspend fun contarTemasPorEstado(@Path("estadoId") estadoId: Long): Long

    // POST /api/v1/tema/temas
    @POST("api/v1/tema/temas")
    suspend fun crearTema(@Body tema: TemaCreateDto): TemaDto

    // PUT /api/v1/tema/temas/{id}
    @PUT("api/v1/tema/temas/{id}")
    suspend fun actualizarTema(
        @Path("id") id: Long,
        @Body tema: TemaUpdateDto
    ): TemaDto

    // DELETE /api/v1/tema/temas/{id}
    @DELETE("api/v1/tema/temas/{id}")
    suspend fun eliminarTema(@Path("id") id: Long): Response<Unit>
}

