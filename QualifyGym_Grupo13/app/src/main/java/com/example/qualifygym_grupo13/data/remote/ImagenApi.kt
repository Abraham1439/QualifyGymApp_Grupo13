package com.example.qualifygym_grupo13.data.remote

import com.example.qualifygym_grupo13.data.remote.dto.ImagenDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ImagenApi {
    
    // POST /api/v1/imagen/perfil/{usuarioId}
    @Multipart
    @POST("api/v1/imagen/perfil/{usuarioId}")
    suspend fun subirFotoPerfil(
        @Path("usuarioId") usuarioId: Long,
        @Part archivo: MultipartBody.Part
    ): Response<Map<String, Any>>
    
    // GET /api/v1/imagen/perfil/{usuarioId}
    @GET("api/v1/imagen/perfil/{usuarioId}")
    suspend fun obtenerFotoPerfil(@Path("usuarioId") usuarioId: Long): Response<ResponseBody>
    
    // DELETE /api/v1/imagen/perfil/{usuarioId}
    @DELETE("api/v1/imagen/perfil/{usuarioId}")
    suspend fun eliminarFotoPerfil(@Path("usuarioId") usuarioId: Long): Response<Unit>
    
    // POST /api/v1/imagen/publicacion/{publicacionId}
    @Multipart
    @POST("api/v1/imagen/publicacion/{publicacionId}")
    suspend fun subirFotoPublicacion(
        @Path("publicacionId") publicacionId: Long,
        @Query("usuarioId") usuarioId: Long,
        @Part archivo: MultipartBody.Part
    ): Response<Map<String, Any>>
    
    // GET /api/v1/imagen/publicacion/{publicacionId}
    @GET("api/v1/imagen/publicacion/{publicacionId}")
    suspend fun obtenerImagenesPublicacion(@Path("publicacionId") publicacionId: Long): Response<List<ImagenDto>>
    
    // GET /api/v1/imagen/{idImagen}
    @GET("api/v1/imagen/{idImagen}")
    suspend fun obtenerImagenPorId(@Path("idImagen") idImagen: Long): Response<ResponseBody>
    
    // DELETE /api/v1/imagen/{idImagen}
    @DELETE("api/v1/imagen/{idImagen}")
    suspend fun eliminarImagen(@Path("idImagen") idImagen: Long): Response<Unit>
    
    // GET /api/v1/imagen/usuario/{usuarioId}/count
    @GET("api/v1/imagen/usuario/{usuarioId}/count")
    suspend fun contarImagenesPorUsuario(@Path("usuarioId") usuarioId: Long): Long
    
    // GET /api/v1/imagen/publicacion/{publicacionId}/count
    @GET("api/v1/imagen/publicacion/{publicacionId}/count")
    suspend fun contarImagenesPorPublicacion(@Path("publicacionId") publicacionId: Long): Long
}

