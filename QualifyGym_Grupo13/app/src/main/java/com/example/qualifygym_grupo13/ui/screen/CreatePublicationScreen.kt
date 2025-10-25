package com.example.qualifygym_grupo13.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Función para crear un archivo temporal donde se guardará la foto capturada por la cámara
 * @param context Contexto de la aplicación para acceder al directorio de caché
 * @return File objeto que representa el archivo temporal donde se guardará la imagen
 */
private fun createTempImageFile(context: Context): File{
    // Genera un timestamp único para evitar nombres de archivo duplicados
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    
    // Crea el directorio "images" dentro del caché de la aplicación
    val storageDir = File(context.cacheDir,"images").apply {
        if(!exists()) mkdirs() // Crea la carpeta si no existe
    }
    
    // Retorna un archivo temporal con nombre único basado en el timestamp
    return File(storageDir,"IMG_$timeStamp.jpg") // Archivo temporal en formato JPG
}

/**
 * Función para convertir un archivo local en una URI segura usando FileProvider
 * Esto es necesario para compartir archivos con otras aplicaciones (como la cámara) de forma segura
 * @param context Contexto de la aplicación
 * @param file Archivo local que se quiere convertir a URI
 * @return Uri URI segura que puede ser compartida con otras aplicaciones
 */
private fun getImageUriFile(context: Context, file: File): Uri {
    // Construye la autoridad del FileProvider usando el package name de la app
    val authority = "${context.packageName}.fileprovider"
    
    // Convierte el archivo local en una URI segura usando FileProvider
    return FileProvider.getUriForFile(context, authority, file)
}

/**
 * Función para verificar si la aplicación tiene permisos de cámara
 * @param context Contexto de la aplicación
 * @return Boolean true si tiene permisos, false si no
 */
private fun hasCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.CAMERA
    ) == PermissionChecker.PERMISSION_GRANTED
}

@Composable
fun CreatePublicationScreen(
    onPublished: (String, String, String, List<String>) -> Unit
) {
    val (title, setTitle) = remember { mutableStateOf("") }
    val (desc, setDesc) = remember { mutableStateOf("") }
    val (topic, setTopic) = remember { mutableStateOf("") }

    // ==========================================================
    // === LÓGICA DE CÁMARA - Configuración y Estados ===
    // ==========================================================
    val context = LocalContext.current // Obtiene el contexto de la aplicación actual

    // Estado persistente para guardar la URI de la foto como String
    // Se mantiene durante cambios de configuración (rotación de pantalla, etc.)
    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }
    
    // Estado temporal para la URI antes de lanzar la cámara (no persistente)
    // Se usa para pasar la URI a la aplicación de cámara
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    
    // Estado para controlar la visibilidad del diálogo de confirmación de borrado
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Estado para controlar la visibilidad del diálogo de permisos de cámara
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Launcher para la aplicación de cámara usando ActivityResultContracts
    // Este launcher maneja la comunicación con la aplicación de cámara del sistema
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(), // Contrato para tomar fotos
        onResult = { success -> // Callback que se ejecuta cuando la cámara termina
            if (success) {
                // Si la foto se tomó exitosamente, guardamos la URI como String persistente
                photoUriString = pendingCaptureUri?.toString()
                Toast.makeText(context, "Foto guardada", Toast.LENGTH_SHORT).show()
            } else {
                // Si se canceló la captura, limpiamos la URI temporal
                pendingCaptureUri = null
                // No mostramos Toast aquí para evitar molestias si cancela intencionalmente
            }
        }
    )

    // Launcher para la galería usando ActivityResultContracts
    // Este launcher maneja la selección de imágenes desde la galería del dispositivo
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), // Contrato para seleccionar contenido
        onResult = { uri -> // Callback que se ejecuta cuando se selecciona una imagen
            if (uri != null) {
                // Si se seleccionó una imagen, guardamos la URI como String persistente
                photoUriString = uri.toString()
                Toast.makeText(context, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
            }
            // Si se canceló la selección, no hacemos nada (no mostramos Toast)
        }
    )
    
    // Launcher para solicitar permisos de cámara
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Si se otorgó el permiso, proceder a tomar la foto
            val file = createTempImageFile(context)
            val uri = getImageUriFile(context, file)
            pendingCaptureUri = uri
            takePictureLauncher.launch(uri)
        } else {
            // Si se denegó el permiso, mostrar mensaje
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }
    // === FIN LÓGICA DE CÁMARA Y GALERÍA ===

    /**
     * Función para manejar el clic del botón de tomar foto
     * Verifica permisos antes de abrir la cámara
     */
    fun handleTakePhotoClick() {
        if (hasCameraPermission(context)) {
            // Si ya tiene permisos, proceder directamente
            val file = createTempImageFile(context)
            val uri = getImageUriFile(context, file)
            pendingCaptureUri = uri
            takePictureLauncher.launch(uri)
        } else {
            // Si no tiene permisos, mostrar diálogo explicativo
            showPermissionDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Nueva publicación", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = setTitle,
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = topic,
            onValueChange = setTopic,
            label = { Text("Tema") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = desc,
            onValueChange = setDesc,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )
        
        Spacer(Modifier.height(16.dp))
        
        // === SECCIÓN DE FOTOS - Interfaz de Usuario ===
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título de la sección de fotos
            Text(
                text = "Imagen (Opcional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            // Vista previa de la imagen si hay una foto seleccionada
            if (!photoUriString.isNullOrEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    // Muestra la imagen capturada usando la librería Coil
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(Uri.parse(photoUriString)) // Convierte el String guardado de vuelta a Uri
                            .crossfade(true) // Efecto de transición suave al cargar
                            .build(),
                        contentDescription = "Foto Tomada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Altura fija para la vista previa
                            .clip(RoundedCornerShape(12.dp)), // Bordes redondeados
                        contentScale = ContentScale.Crop // Escala la imagen para llenar el espacio
                    )
                }
            }

            Spacer(Modifier.height(8.dp)) // Espacio visual antes de los botones

            // --- BOTÓN PRINCIPAL: TOMAR FOTO (SIEMPRE VISIBLE) ---
            FilledTonalButton(
                onClick = { handleTakePhotoClick() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Tomar Foto")
                Spacer(modifier = Modifier.width(8.dp))
                // Cambia el texto dinámicamente según si ya hay una foto
                Text(if (photoUriString.isNullOrEmpty()) "Tomar Foto" else "Tomar Otra Foto")
            }

            // --- BOTÓN SECUNDARIO: SELECCIONAR DE GALERÍA (SIEMPRE VISIBLE) ---
            OutlinedButton(
                onClick = { 
                    // Lanza el selector de imágenes de la galería
                    pickImageLauncher.launch("image/*") // Filtro para mostrar solo imágenes
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Ver en Galería")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Seleccionar de Galería")
            }

            // --- BOTÓN VOLVER A TOMAR FOTO (Solo visible si hay una foto) ---
            if (!photoUriString.isNullOrEmpty()) {
                OutlinedButton(
                    onClick = { handleTakePhotoClick() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Volver a Tomar Foto")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Volver a Tomar Foto")
                }
            }

            // --- BOTÓN ELIMINAR FOTO (Solo visible si hay una foto) ---
            if (!photoUriString.isNullOrEmpty()) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true }, // Abre el diálogo de confirmación
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error) // Color rojo para indicar acción destructiva
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Foto")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar Foto")
                }
            }
        }
        // === FIN SECCIÓN DE FOTOS ===
        
        Spacer(Modifier.height(16.dp))
        
        Button(
            onClick = {
                // Prepara la lista de URIs de fotos para enviar al callback
                val photoUris = if (photoUriString != null) listOf(photoUriString!!) else emptyList()
                
                // Llama al callback con el título, tema, descripción y lista de fotos
                onPublished(title, topic, desc, photoUris)
            },
            enabled = title.isNotBlank() && topic.isNotBlank() && desc.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar")
        }
    }

    // === DIÁLOGO DE CONFIRMACIÓN PARA ELIMINAR LA FOTO ===
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false }, // Cierra el diálogo si toca fuera
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar la foto?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        photoUriString = null // Elimina la URI guardada (borra la foto)
                        showDeleteDialog = false // Cierra el diálogo
                        Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
                    }
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // === DIÁLOGO DE PERMISOS DE CÁMARA ===
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permiso de Cámara") },
            text = {
                Text("Esta aplicación necesita acceso a la cámara para tomar fotos. ¿Permitir el acceso?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                ) { Text("Permitir") }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

