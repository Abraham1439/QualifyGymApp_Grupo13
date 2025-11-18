package com.example.qualifygym_grupo13.data.remote

import com.example.qualifygym_grupo13.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ComentarioApi {
    // GET /api/v1/comentario/comentarios
    @GET("api/v1/comentario/comentarios")
    suspend fun getComentarios(): List<ComentarioDto>

    // GET /api/v1/comentario/comentarios/{id}
    @GET("api/v1/comentario/comentarios/{id}")
    suspend fun getComentarioById(@Path("id") id: Long): ComentarioDto

    // GET /api/v1/comentario/comentarios/publicacion/{publicacionId}
    @GET("api/v1/comentario/comentarios/publicacion/{publicacionId}")
    suspend fun getComentariosPorPublicacion(
        @Path("publicacionId") publicacionId: Long,
        @Query("incluirOcultos") incluirOcultos: Boolean = false
    ): List<ComentarioDto>

    // GET /api/v1/comentario/comentarios/usuario/{usuarioId}
    @GET("api/v1/comentario/comentarios/usuario/{usuarioId}")
    suspend fun getComentariosPorUsuario(@Path("usuarioId") usuarioId: Long): List<ComentarioDto>

    // GET /api/v1/comentario/comentarios/publicacion/{publicacionId}/count
    @GET("api/v1/comentario/comentarios/publicacion/{publicacionId}/count")
    suspend fun contarComentariosPorPublicacion(@Path("publicacionId") publicacionId: Long): Long

    // GET /api/v1/comentario/comentarios/usuario/{usuarioId}/count
    @GET("api/v1/comentario/comentarios/usuario/{usuarioId}/count")
    suspend fun contarComentariosPorUsuario(@Path("usuarioId") usuarioId: Long): Long

    // POST /api/v1/comentario/comentarios
    @POST("api/v1/comentario/comentarios")
    suspend fun crearComentario(@Body comentario: ComentarioCreateDto): ComentarioDto

    // PUT /api/v1/comentario/comentarios/{id}
    @PUT("api/v1/comentario/comentarios/{id}")
    suspend fun actualizarComentario(
        @Path("id") id: Long,
        @Body comentario: ComentarioUpdateDto
    ): ComentarioDto

    // PUT /api/v1/comentario/comentarios/{id}/ocultar
    @PUT("api/v1/comentario/comentarios/{id}/ocultar")
    suspend fun ocultarComentario(
        @Path("id") id: Long,
        @Body motivo: ComentarioOcultarDto
    ): ComentarioDto

    // PUT /api/v1/comentario/comentarios/{id}/mostrar
    @PUT("api/v1/comentario/comentarios/{id}/mostrar")
    suspend fun mostrarComentario(@Path("id") id: Long): ComentarioDto

    // DELETE /api/v1/comentario/comentarios/{id}
    @DELETE("api/v1/comentario/comentarios/{id}")
    suspend fun eliminarComentario(@Path("id") id: Long): Response<Unit>
}

