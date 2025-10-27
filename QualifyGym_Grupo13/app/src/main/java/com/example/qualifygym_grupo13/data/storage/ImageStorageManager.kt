package com.example.qualifygym_grupo13.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Gestor de almacenamiento de imágenes
 * Guarda y optimiza fotos de perfil en el almacenamiento interno
 */
class ImageStorageManager(private val context: Context) {
    
    companion object {
        private const val PROFILE_PHOTOS_DIR = "profile_photos"
        private const val PUBLICATION_IMAGES_DIR = "publication_images"
        private const val MAX_IMAGE_SIZE = 800 // Máximo 800px de ancho/alto
        private const val COMPRESSION_QUALITY = 85 // Calidad de compresión JPEG (0-100)
    }
    
    /**
     * Guarda una imagen de perfil y retorna su path
     * @param imageUri URI de la imagen original
     * @param userId ID del usuario (para nombrar el archivo)
     * @return Path del archivo guardado o null si hay error
     */
    fun saveProfileImage(imageUri: Uri, userId: Long): String? {
        return try {
            // 1. Obtener el directorio para fotos de perfil
            val profilePhotosDir = File(context.filesDir, PROFILE_PHOTOS_DIR)
            if (!profilePhotosDir.exists()) {
                profilePhotosDir.mkdirs()
            }
            
            // 2. Crear el archivo destino (eliminar anterior si existe)
            val imageFile = File(profilePhotosDir, "user_${userId}.jpg")
            if (imageFile.exists()) {
                imageFile.delete() // Eliminar foto anterior
            }
            
            // 3. Decodificar y optimizar la imagen
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (originalBitmap == null) {
                return null
            }
            
            // 4. Corregir orientación si es necesario
            val rotatedBitmap = correctImageOrientation(imageUri, originalBitmap)
            
            // 5. Redimensionar la imagen
            val resizedBitmap = resizeBitmap(rotatedBitmap, MAX_IMAGE_SIZE)
            
            // 6. Guardar con compresión
            FileOutputStream(imageFile).use { out ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)
            }
            
            // 7. Liberar memoria
            if (rotatedBitmap != originalBitmap) {
                rotatedBitmap.recycle()
            }
            originalBitmap.recycle()
            if (resizedBitmap != rotatedBitmap && resizedBitmap != originalBitmap) {
                resizedBitmap.recycle()
            }
            
            // 8. Retornar el path absoluto
            imageFile.absolutePath
            
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Obtiene el File de una foto de perfil
     * @param photoPath Path de la foto
     * @return File o null si no existe
     */
    fun getProfileImageFile(photoPath: String?): File? {
        if (photoPath == null) return null
        val file = File(photoPath)
        return if (file.exists()) file else null
    }
    
    /**
     * Elimina una foto de perfil
     * @param photoPath Path de la foto a eliminar
     */
    fun deleteProfileImage(photoPath: String?) {
        if (photoPath != null) {
            try {
                val file = File(photoPath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Corrige la orientación de la imagen según EXIF
     */
    private fun correctImageOrientation(imageUri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val exif = inputStream?.let { ExifInterface(it) }
            inputStream?.close()
            
            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL
            
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                else -> return bitmap
            }
            
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
            
            rotatedBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }
    
    /**
     * Redimensiona un bitmap manteniendo la proporción
     */
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        // Si ya es más pequeña que maxSize, no hace falta redimensionar
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }
        
        val scale = if (width > height) {
            maxSize.toFloat() / width
        } else {
            maxSize.toFloat() / height
        }
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Convierte un path a Uri para mostrar con Coil
     */
    fun pathToUri(photoPath: String?): Uri? {
        if (photoPath == null) return null
        val file = File(photoPath)
        return if (file.exists()) Uri.fromFile(file) else null
    }
    
    /**
     * Guarda una imagen de publicación y retorna su path
     * @param imageUri URI de la imagen original
     * @param publicationId ID de la publicación (para nombrar el archivo)
     * @return Path del archivo guardado o null si hay error
     */
    fun savePublicationImage(imageUri: Uri, publicationId: Long): String? {
        return try {
            // 1. Obtener el directorio para imágenes de publicaciones
            val publicationImagesDir = File(context.filesDir, PUBLICATION_IMAGES_DIR)
            if (!publicationImagesDir.exists()) {
                publicationImagesDir.mkdirs()
            }
            
            // 2. Crear el archivo destino
            val imageFile = File(publicationImagesDir, "publication_${publicationId}.jpg")
            if (imageFile.exists()) {
                imageFile.delete() // Eliminar imagen anterior si existe
            }
            
            // 3. Decodificar y optimizar la imagen
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (originalBitmap == null) {
                return null
            }
            
            // 4. Corregir orientación si es necesario
            val rotatedBitmap = correctImageOrientation(imageUri, originalBitmap)
            
            // 5. Redimensionar la imagen
            val resizedBitmap = resizeBitmap(rotatedBitmap, MAX_IMAGE_SIZE)
            
            // 6. Guardar con compresión
            FileOutputStream(imageFile).use { out ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)
            }
            
            // 7. Liberar memoria
            if (rotatedBitmap != originalBitmap) {
                rotatedBitmap.recycle()
            }
            originalBitmap.recycle()
            if (resizedBitmap != rotatedBitmap && resizedBitmap != originalBitmap) {
                resizedBitmap.recycle()
            }
            
            // 8. Retornar el path absoluto
            imageFile.absolutePath
            
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Elimina una imagen de publicación
     * @param imagePath Path de la imagen a eliminar
     */
    fun deletePublicationImage(imagePath: String?) {
        if (imagePath != null) {
            try {
                val file = File(imagePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

