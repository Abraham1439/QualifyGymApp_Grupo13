package com.example.qualifygym_grupo13.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.qualifygym_grupo13.data.remote.ImagenApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import retrofit2.Response
import java.util.HashMap

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ImagenRepositoryTest {

    private lateinit var api: ImagenApi
    private lateinit var context: android.content.Context
    private lateinit var repository: ImagenRepository

    @Before
    fun setup() {
        api = mockk<ImagenApi>(relaxed = true)
        context = RuntimeEnvironment.getApplication()
        repository = ImagenRepository(api, context)
    }

    // ========== Tests para subirFotoPerfil ==========//
    @Test
    fun subirFotoPerfil_exitoso() {
        // Valida: Subir foto de perfil exitosamente
        // Retorna: Result.success con ID de imagen
        runBlocking {
            val responseMap = HashMap<String, Any>()
            responseMap["idImagen"] = 123L
            val response = Response.success<Map<String, Any>>(200, responseMap)

            coEvery { api.subirFotoPerfil(any(), any()) } returns response
            
            // Crear un bitmap válido y guardarlo como JPEG
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            val tempFile = java.io.File(context.cacheDir, "test_image.jpg")
            val outputStream = java.io.FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            val uri = Uri.fromFile(tempFile)
            
            val result = repository.subirFotoPerfil(1, uri)
            assertTrue(result.isSuccess)
            assertEquals(123L, result.getOrNull())
            
            // Limpiar
            tempFile.delete()
            bitmap.recycle()
        }
    }

    @Test
    fun subirFotoPerfil_error_respuesta() {
        // Valida: Error al subir foto de perfil
        // Retorna: Result.failure con mensaje de error
        runBlocking {
            val errorResponse = Response.error<Map<String, Any>>(400, "Error".toResponseBody("application/json".toMediaType()))
            coEvery { api.subirFotoPerfil(any(), any()) } returns errorResponse
            
            // Crear un bitmap válido y guardarlo como JPEG
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            val tempFile = java.io.File(context.cacheDir, "test_image.jpg")
            val outputStream = java.io.FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            val uri = Uri.fromFile(tempFile)
            
            val result = repository.subirFotoPerfil(1, uri)
            assertTrue(result.isFailure)
            
            // Limpiar
            tempFile.delete()
            bitmap.recycle()
        }
    }

    // ========== Tests para obtenerFotoPerfil ==========//
    @Test
    fun obtenerFotoPerfil_exitoso() {
        // Valida: Obtener foto de perfil exitosamente
        // Retorna: Result.success con Bitmap
        runBlocking {
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            val outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val bytes = outputStream.toByteArray()
            
            val responseBody = bytes.toResponseBody("image/jpeg".toMediaType())
            val response = Response.success<okhttp3.ResponseBody>(200, responseBody)
            
            coEvery { api.obtenerFotoPerfil(any()) } returns response
            
            val result = repository.obtenerFotoPerfil(1)
            assertTrue(result.isSuccess)
            assertNotNull(result.getOrNull())
        }
    }

    @Test
    fun obtenerFotoPerfil_no_existe() {
        // Valida: Obtener foto de perfil que no existe
        // Retorna: Result.success con null
        runBlocking {
            val errorResponse = Response.error<okhttp3.ResponseBody>(404, "Not found".toResponseBody("application/json".toMediaType()))
            coEvery { api.obtenerFotoPerfil(any()) } returns errorResponse
            
            val result = repository.obtenerFotoPerfil(1)
            assertTrue(result.isSuccess)
            assertNull(result.getOrNull())
        }
    }

    @Test
    fun obtenerFotoPerfil_error() {
        // Valida: Error al obtener foto de perfil
        // Retorna: Result.failure
        runBlocking {
            val errorResponse = Response.error<okhttp3.ResponseBody>(500, "Server error".toResponseBody("application/json".toMediaType()))
            coEvery { api.obtenerFotoPerfil(any()) } returns errorResponse
            
            val result = repository.obtenerFotoPerfil(1)
            assertTrue(result.isFailure)
        }
    }

    // ========== Tests para subirFotoPublicacion ==========//
    @Test
    fun subirFotoPublicacion_exitoso() {
        // Valida: Subir foto de publicación exitosamente
        // Retorna: Result.success con ID de imagen
        runBlocking {
            val responseMap = HashMap<String, Any>()
            responseMap["idImagen"] = 456L
            val response = Response.success<Map<String, Any>>(200, responseMap)

            coEvery { api.subirFotoPublicacion(any(), any(), any()) } returns response
            
            // Crear un bitmap válido y guardarlo como JPEG
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            val tempFile = java.io.File(context.cacheDir, "test_image.jpg")
            val outputStream = java.io.FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            val uri = Uri.fromFile(tempFile)
            
            val result = repository.subirFotoPublicacion(1, 1, uri)
            assertTrue(result.isSuccess)
            assertEquals(456L, result.getOrNull())
            
            // Limpiar
            tempFile.delete()
            bitmap.recycle()
        }
    }

    @Test
    fun subirFotoPublicacion_error_respuesta() {
        // Valida: Error al subir foto de publicación
        // Retorna: Result.failure con mensaje de error
        runBlocking {
            val errorResponse = Response.error<Map<String, Any>>(400, "Error".toResponseBody("application/json".toMediaType()))
            coEvery { api.subirFotoPublicacion(any(), any(), any()) } returns errorResponse
            
            // Crear un bitmap válido y guardarlo como JPEG
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            val tempFile = java.io.File(context.cacheDir, "test_image.jpg")
            val outputStream = java.io.FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            val uri = Uri.fromFile(tempFile)
            
            val result = repository.subirFotoPublicacion(1, 1, uri)
            assertTrue(result.isFailure)
            
            // Limpiar
            tempFile.delete()
            bitmap.recycle()
        }
    }

    // ========== Tests para obtenerImagenPorId ==========//
    @Test
    fun obtenerImagenPorId_exitoso() {
        // Valida: Obtener imagen por ID exitosamente
        // Retorna: Result.success con Bitmap
        runBlocking {
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            val outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val bytes = outputStream.toByteArray()
            
            val responseBody = bytes.toResponseBody("image/jpeg".toMediaType())
            val response = Response.success<okhttp3.ResponseBody>(200, responseBody)
            
            coEvery { api.obtenerImagenPorId(any()) } returns response
            
            val result = repository.obtenerImagenPorId(1)
            assertTrue(result.isSuccess)
            assertNotNull(result.getOrNull())
        }
    }

    @Test
    fun obtenerImagenPorId_no_existe() {
        // Valida: Obtener imagen por ID que no existe
        // Retorna: Result.success con null
        runBlocking {
            val errorResponse = Response.error<okhttp3.ResponseBody>(404, "Not found".toResponseBody("application/json".toMediaType()))
            coEvery { api.obtenerImagenPorId(any()) } returns errorResponse
            
            val result = repository.obtenerImagenPorId(999)
            assertTrue(result.isSuccess)
            assertNull(result.getOrNull())
        }
    }

    @Test
    fun obtenerImagenPorId_error() {
        // Valida: Error al obtener imagen por ID
        // Retorna: Result.failure
        runBlocking {
            val errorResponse = Response.error<okhttp3.ResponseBody>(500, "Server error".toResponseBody("application/json".toMediaType()))
            coEvery { api.obtenerImagenPorId(any()) } returns errorResponse
            
            val result = repository.obtenerImagenPorId(1)
            assertTrue(result.isFailure)
        }
    }

    // ========== Tests para obtenerImagenesPublicacion ==========//
    @Test
    fun obtenerImagenesPublicacion_exitoso() {
        // Valida: Obtener imágenes de publicación exitosamente
        // Retorna: Result.success con lista de IDs
        runBlocking {
            val imagenesDto = listOf(
                com.example.qualifygym_grupo13.data.remote.dto.ImagenDto(1, null, 1, "PUBLICACION", "image/jpeg", "imagen1.jpg", 1024, "01-01-2024 10:00"),
                com.example.qualifygym_grupo13.data.remote.dto.ImagenDto(2, null, 1, "PUBLICACION", "image/jpeg", "imagen2.jpg", 2048, "01-01-2024 11:00")
            )
            val response = Response.success<List<com.example.qualifygym_grupo13.data.remote.dto.ImagenDto>>(200, imagenesDto)
            
            coEvery { api.obtenerImagenesPublicacion(any()) } returns response
            
            val result = repository.obtenerImagenesPublicacion(1)
            assertTrue(result.isSuccess)
            assertEquals(2, result.getOrNull()!!.size)
            assertEquals(1L, result.getOrNull()!![0])
            assertEquals(2L, result.getOrNull()!![1])
        }
    }

    @Test
    fun obtenerImagenesPublicacion_vacia() {
        // Valida: Obtener imágenes de publicación sin imágenes
        // Retorna: Result.success con lista vacía
        runBlocking {
            val emptyResponse = Response.success<List<com.example.qualifygym_grupo13.data.remote.dto.ImagenDto>>(200, emptyList())
            coEvery { api.obtenerImagenesPublicacion(any()) } returns emptyResponse
            
            val result = repository.obtenerImagenesPublicacion(1)
            assertTrue(result.isSuccess)
            assertTrue(result.getOrNull()!!.isEmpty())
        }
    }

    @Test
    fun obtenerImagenesPublicacion_error() {
        // Valida: Error al obtener imágenes de publicación
        // Retorna: Result.failure
        runBlocking {
            val errorResponse = Response.error<List<com.example.qualifygym_grupo13.data.remote.dto.ImagenDto>>(500, "Server error".toResponseBody("application/json".toMediaType()))
            coEvery { api.obtenerImagenesPublicacion(any()) } returns errorResponse
            
            val result = repository.obtenerImagenesPublicacion(1)
            assertTrue(result.isFailure)
        }
    }

    // ========== Tests para eliminarFotoPerfil ==========//
    @Test
    fun eliminarFotoPerfil_exitoso() {
        // Valida: Eliminar foto de perfil exitosamente
        // Retorna: Result.success
        runBlocking {
            val response = Response.success<Unit>(200, null)
            coEvery { api.eliminarFotoPerfil(any()) } returns response
            
            val result = repository.eliminarFotoPerfil(1)
            assertTrue(result.isSuccess)
        }
    }

    @Test
    fun eliminarFotoPerfil_error() {
        // Valida: Error al eliminar foto de perfil
        // Retorna: Result.failure
        runBlocking {
            val errorResponse = Response.error<Unit>(500, "Server error".toResponseBody("application/json".toMediaType()))
            coEvery { api.eliminarFotoPerfil(any()) } returns errorResponse
            
            val result = repository.eliminarFotoPerfil(1)
            assertTrue(result.isFailure)
        }
    }
}

