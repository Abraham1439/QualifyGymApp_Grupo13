package com.example.qualifygym_grupo13.data.remote

import com.example.qualifygym_grupo13.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface PublicacionApi {
    // GET /api/v1/publicacion/publicaciones
    @GET("api/v1/publicacion/publicaciones")
    suspend fun getPublicaciones(
        @Query("incluirOcultas") incluirOcultas: Boolean = false
    ): List<PublicacionDto>

    // GET /api/v1/publicacion/publicaciones/{id}
    @GET("api/v1/publicacion/publicaciones/{id}")
    suspend fun getPublicacionById(@Path("id") id: Long): PublicacionDto

    // GET /api/v1/publicacion/publicaciones/tema/{temaId}
    @GET("api/v1/publicacion/publicaciones/tema/{temaId}")
    suspend fun getPublicacionesPorTema(
        @Path("temaId") temaId: Long,
        @Query("incluirOcultas") incluirOcultas: Boolean = false
    ): List<PublicacionDto>

    // GET /api/v1/publicacion/publicaciones/usuario/{usuarioId}
    @GET("api/v1/publicacion/publicaciones/usuario/{usuarioId}")
    suspend fun getPublicacionesPorUsuario(
        @Path("usuarioId") usuarioId: Long,
        @Query("incluirOcultas") incluirOcultas: Boolean = false
    ): List<PublicacionDto>

    // GET /api/v1/publicacion/publicaciones/buscar?query=texto
    @GET("api/v1/publicacion/publicaciones/buscar")
    suspend fun buscarPublicaciones(@Query("query") query: String): List<PublicacionDto>

    // GET /api/v1/publicacion/publicaciones/tema/{temaId}/count
    @GET("api/v1/publicacion/publicaciones/tema/{temaId}/count")
    suspend fun contarPublicacionesPorTema(@Path("temaId") temaId: Long): Long

    // GET /api/v1/publicacion/publicaciones/usuario/{usuarioId}/count
    @GET("api/v1/publicacion/publicaciones/usuario/{usuarioId}/count")
    suspend fun contarPublicacionesPorUsuario(@Path("usuarioId") usuarioId: Long): Long

    // POST /api/v1/publicacion/publicaciones
    @POST("api/v1/publicacion/publicaciones")
    suspend fun crearPublicacion(@Body publicacion: PublicacionCreateDto): PublicacionDto

    // PUT /api/v1/publicacion/publicaciones/{id}
    @PUT("api/v1/publicacion/publicaciones/{id}")
    suspend fun actualizarPublicacion(
        @Path("id") id: Long,
        @Body publicacion: PublicacionUpdateDto
    ): PublicacionDto

    // PUT /api/v1/publicacion/publicaciones/{id}/imagen
    @PUT("api/v1/publicacion/publicaciones/{id}/imagen")
    suspend fun actualizarImagenPublicacion(
        @Path("id") id: Long,
        @Body imagen: PublicacionImagenDto
    ): PublicacionDto

    // PUT /api/v1/publicacion/publicaciones/{id}/ocultar
    @PUT("api/v1/publicacion/publicaciones/{id}/ocultar")
    suspend fun ocultarPublicacion(
        @Path("id") id: Long,
        @Body motivo: PublicacionOcultarDto
    ): PublicacionDto

    // PUT /api/v1/publicacion/publicaciones/{id}/mostrar
    @PUT("api/v1/publicacion/publicaciones/{id}/mostrar")
    suspend fun mostrarPublicacion(@Path("id") id: Long): PublicacionDto

    // DELETE /api/v1/publicacion/publicaciones/{id}
    @DELETE("api/v1/publicacion/publicaciones/{id}")
    suspend fun eliminarPublicacion(@Path("id") id: Long): Response<Unit>

    // ========== NOTIFICACIONES ==========
    
    // GET /api/v1/publicacion/notificaciones/usuario/{usuarioId}
    @GET("api/v1/publicacion/notificaciones/usuario/{usuarioId}")
    suspend fun getNotificacionesPorUsuario(@Path("usuarioId") usuarioId: Long): List<NotificacionDto>

    // GET /api/v1/publicacion/notificaciones/usuario/{usuarioId}/no-leidas
    @GET("api/v1/publicacion/notificaciones/usuario/{usuarioId}/no-leidas")
    suspend fun getNotificacionesNoLeidasPorUsuario(@Path("usuarioId") usuarioId: Long): List<NotificacionDto>

    // GET /api/v1/publicacion/notificaciones/usuario/{usuarioId}/no-leidas/count
    @GET("api/v1/publicacion/notificaciones/usuario/{usuarioId}/no-leidas/count")
    suspend fun contarNotificacionesNoLeidas(@Path("usuarioId") usuarioId: Long): Long

    // PUT /api/v1/publicacion/notificaciones/{id}/marcar-leida
    @PUT("api/v1/publicacion/notificaciones/{id}/marcar-leida")
    suspend fun marcarNotificacionComoLeida(@Path("id") id: Long): NotificacionDto

    // PUT /api/v1/publicacion/notificaciones/usuario/{usuarioId}/marcar-todas-leidas
    @PUT("api/v1/publicacion/notificaciones/usuario/{usuarioId}/marcar-todas-leidas")
    suspend fun marcarTodasComoLeidas(@Path("usuarioId") usuarioId: Long): Response<Unit>

    // DELETE /api/v1/publicacion/notificaciones/{id}
    @DELETE("api/v1/publicacion/notificaciones/{id}")
    suspend fun eliminarNotificacion(@Path("id") id: Long): Response<Unit>
}

