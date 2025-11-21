package com.example.qualifygym_grupo13.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.qualifygym_grupo13.data.remote.ImagenApi
import com.example.qualifygym_grupo13.data.remote.RemoteModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.ByteArrayOutputStream

class ImagenRepository(
    private val api: ImagenApi = RemoteModule.imagenApi,
    private val context: Context
) {
    
    /**
     * Obtiene el tipo MIME de un Uri y lo normaliza
     */
    private fun getMimeType(uri: Uri): String {
        val mimeType = when {
            uri.scheme == "content" -> {
                context.contentResolver.getType(uri) ?: "image/jpeg"
            }
            uri.scheme == "file" -> {
                val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/jpeg"
            }
            else -> "image/jpeg" // Default
        }
        
        // Normalizar tipos MIME comunes
        return when (mimeType.lowercase()) {
            "image/jpg" -> "image/jpeg"
            "image/pjpeg" -> "image/jpeg"
            "image/x-png" -> "image/png"
            else -> mimeType
        }
    }
    
    /**
     * Redimensiona y comprime un Bitmap para que no exceda el tamaño máximo
     */
    private fun compressBitmap(bitmap: Bitmap, maxWidth: Int = 1920, maxHeight: Int = 1920, quality: Int = 85): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        
        // Redimensionar si es necesario
        if (width > maxWidth || height > maxHeight) {
            val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
            width = (width * ratio).toInt()
            height = (height * ratio).toInt()
            return Bitmap.createScaledBitmap(bitmap, width, height, true)
        }
        
        return bitmap
    }
    
    /**
     * Comprime una imagen a un archivo con calidad ajustada para mantener el tamaño bajo 1MB
     */
    private fun compressImageToFile(bitmap: Bitmap, outputFile: File, mimeType: String, maxSizeBytes: Long = 900_000): File {
        var quality = 85
        var compressed = false
        
        // Intentar comprimir hasta que el tamaño sea aceptable
        while (!compressed && quality > 20) {
            val outputStream = ByteArrayOutputStream()
            
            val format = when {
                mimeType.contains("png") -> Bitmap.CompressFormat.PNG
                else -> Bitmap.CompressFormat.JPEG
            }
            
            bitmap.compress(format, quality, outputStream)
            val byteArray = outputStream.toByteArray()
            
            if (byteArray.size <= maxSizeBytes || quality <= 30) {
                FileOutputStream(outputFile).use { it.write(byteArray) }
                compressed = true
            } else {
                quality -= 10
            }
        }
        
        return outputFile
    }
    
    /**
     * Convierte un Uri a File temporal comprimido para subir
     */
    private suspend fun uriToFile(uri: Uri): Pair<File, String> = withContext(Dispatchers.IO) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val mimeType = getMimeType(uri)
        
        // Leer el bitmap original
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
            ?: throw Exception("No se pudo leer la imagen")
        
        // Redimensionar si es muy grande
        val resizedBitmap = compressBitmap(originalBitmap)
        
        // Determinar extensión basada en MIME type
        val extension = when {
            mimeType.contains("jpeg") || mimeType.contains("jpg") -> "jpg"
            mimeType.contains("png") -> "png"
            mimeType.contains("gif") -> "gif"
            mimeType.contains("webp") -> "webp"
            else -> "jpg" // Default
        }
        
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.$extension")
        
        // Comprimir y guardar
        compressImageToFile(resizedBitmap, tempFile, mimeType)
        
        // Liberar memoria
        if (originalBitmap != resizedBitmap) {
            originalBitmap.recycle()
        }
        resizedBitmap.recycle()
        
        Pair(tempFile, mimeType)
    }
    
    /**
     * Subir foto de perfil
     */
    suspend fun subirFotoPerfil(usuarioId: Long, imageUri: Uri): Result<Long> = try {
        withContext(Dispatchers.IO) {
            val (file, mimeType) = uriToFile(imageUri)
            val requestFile = file.asRequestBody(mimeType.toMediaType())
            val body = MultipartBody.Part.createFormData("archivo", file.name, requestFile)
            
            val response = api.subirFotoPerfil(usuarioId, body)
            
            // Limpiar archivo temporal
            file.delete()
            
            if (response.isSuccessful) {
                val result = response.body()
                val idImagen = (result?.get("idImagen") as? Number)?.toLong() 
                    ?: throw Exception("No se recibió ID de imagen")
                Result.success(idImagen)
            } else {
                // Intentar obtener el mensaje de error del body
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                val errorMessage = errorBody?.takeIf { it.isNotBlank() } 
                    ?: "Error al subir foto: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    /**
     * Obtener foto de perfil
     */
    suspend fun obtenerFotoPerfil(usuarioId: Long): Result<Bitmap?> = try {
        withContext(Dispatchers.IO) {
            val response = api.obtenerFotoPerfil(usuarioId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val bytes = body.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    Result.success(bitmap)
                } else {
                    Result.success(null)
                }
            } else if (response.code() == 404) {
                Result.success(null) // No hay foto de perfil
            } else {
                Result.failure(Exception("Error al obtener foto: ${response.code()}"))
            }
        }
    } catch (e: HttpException) {
        if (e.code() == 404) {
            Result.success(null)
        } else {
            Result.failure(e)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    /**
     * Subir foto de publicación
     */
    suspend fun subirFotoPublicacion(publicacionId: Long, usuarioId: Long, imageUri: Uri): Result<Long> = try {
        withContext(Dispatchers.IO) {
            val (file, mimeType) = uriToFile(imageUri)
            val requestFile = file.asRequestBody(mimeType.toMediaType())
            val body = MultipartBody.Part.createFormData("archivo", file.name, requestFile)
            
            val response = api.subirFotoPublicacion(publicacionId, usuarioId, body)
            
            // Limpiar archivo temporal
            file.delete()
            
            if (response.isSuccessful) {
                val result = response.body()
                val idImagen = (result?.get("idImagen") as? Number)?.toLong() 
                    ?: throw Exception("No se recibió ID de imagen")
                Result.success(idImagen)
            } else {
                // Intentar obtener el mensaje de error del body
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                val errorMessage = errorBody?.takeIf { it.isNotBlank() } 
                    ?: "Error al subir foto: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    /**
     * Obtener imagen por ID
     */
    suspend fun obtenerImagenPorId(idImagen: Long): Result<Bitmap?> = try {
        withContext(Dispatchers.IO) {
            val response = api.obtenerImagenPorId(idImagen)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val bytes = body.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    Result.success(bitmap)
                } else {
                    Result.success(null)
                }
            } else if (response.code() == 404) {
                Result.success(null)
            } else {
                Result.failure(Exception("Error al obtener imagen: ${response.code()}"))
            }
        }
    } catch (e: HttpException) {
        if (e.code() == 404) {
            Result.success(null)
        } else {
            Result.failure(e)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    /**
     * Obtener imágenes de una publicación
     */
    suspend fun obtenerImagenesPublicacion(publicacionId: Long): Result<List<Long>> = try {
        val response = api.obtenerImagenesPublicacion(publicacionId)
        if (response.isSuccessful) {
            val imagenes = response.body() ?: emptyList()
            Result.success(imagenes.map { it.idImagen })
        } else if (response.code() == 204) {
            Result.success(emptyList())
        } else {
            Result.failure(Exception("Error al obtener imágenes: ${response.code()}"))
        }
    } catch (e: HttpException) {
        if (e.code() == 404 || e.code() == 204) {
            Result.success(emptyList())
        } else {
            Result.failure(e)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    /**
     * Eliminar foto de perfil
     */
    suspend fun eliminarFotoPerfil(usuarioId: Long): Result<Unit> = try {
        val response = api.eliminarFotoPerfil(usuarioId)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error al eliminar foto: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

