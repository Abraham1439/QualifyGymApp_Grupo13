package com.example.qualifygym_grupo13.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.qualifygym_grupo13.data.local.storage.UserPreferences
import com.example.qualifygym_grupo13.data.model.Tema
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//funcion para guardar la foto capturada por la cámara
private fun createTempImageFile(context: Context): File{
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir,"images").apply {
        if(!exists()) mkdirs() //crea la carpeta si no existe
    }
    return File(storageDir,"IMG_$timeStamp.jpg") //archivo temporal jpg
}

//convertir la uri de la imagen mediante el FileProvider
private fun getImageUriFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context,authority,file)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteCommentScreen(
    tema: Tema,
    onBackClick: () -> Unit,
    onPublishClick: (String, String, List<String>) -> Unit,
    //Nuevos botones pa las acciones y las fotos ===
    onOpenGalleryClick: () -> Unit

) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var comment by remember { mutableStateOf(TextFieldValue("")) }

    // ==========================================================
    // === LÓGICA DE CÁMARA (Réplica del ejemplo del profesor) ===
    // ==========================================================
    val context = LocalContext.current

    // 1. Estado para guardar la Uri de la foto como String (persistente)
    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }
    // 2. Estado temporal para la Uri antes de lanzar la cámara (no persistente)
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    // 3. Estado para mostrar el diálogo de confirmación de borrado
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 4. Launcher para la cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                // Si la foto se tomó, guardamos la Uri pendiente como String
                photoUriString = pendingCaptureUri?.toString()
                Toast.makeText(context, "Foto guardada", Toast.LENGTH_SHORT).show()
            } else {
                // Si se canceló, limpiamos la Uri pendiente
                pendingCaptureUri = null
                // No mostramos Toast aquí, podría ser molesto si cancela a propósito
            }
        }
    )
    // === FIN LÓGICA DE CÁMARA ===


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escribir Comentario") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección del tema (sin cambios)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Comentando:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(tema.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tema.ubicacion, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Gimnasio", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Campos de texto (sin cambios)
            OutlinedTextField( /*...*/ value = title.text, onValueChange = {title = TextFieldValue(it)}, placeholder = {Text("Título...")}, modifier= Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField( /*...*/ value = comment.text, onValueChange = {comment = TextFieldValue(it)}, placeholder = {Text("Comentario...")}, modifier= Modifier.fillMaxWidth().height(120.dp), maxLines=5)


            // ==========================================================
            // === SECCIÓN DE FOTOS (Réplica del ejemplo del profesor) ===
            // ==========================================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // --- VISTA PREVIA ---
                if (photoUriString.isNullOrEmpty()) {
                    // Texto si no hay foto
                    Text(
                        text = "Añade una foto (opcional)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    // Muestra la imagen usando Coil si hay una Uri guardada
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(Uri.parse(photoUriString)) // Convertimos el String de nuevo a Uri
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto Tomada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Altura de la vista previa
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop // Escala para llenar
                    )
                }

                Spacer(Modifier.height(8.dp)) // Espacio antes de los botones

                // --- BOTÓN TOMAR FOTO ---
                FilledTonalButton(
                    onClick = {
                        // Lógica del ejemplo del profesor:
                        val file = createTempImageFile(context) // 1. Crea archivo temporal
                        val uri = getImageUriFile(context, file) // 2. Obtiene Uri segura
                        pendingCaptureUri = uri // 3. Guarda la Uri temporalmente
                        takePictureLauncher.launch(uri) // 4. Lanza la cámara
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Tomar Foto")
                    Spacer(modifier = Modifier.width(8.dp))
                    // Cambia el texto si ya hay una foto
                    Text(if (photoUriString.isNullOrEmpty()) "Tomar Foto" else "Tomar Otra Foto")
                }

                // --- BOTÓN GALERÍA ---
                OutlinedButton(
                    onClick = { onOpenGalleryClick() }, // Mantenemos la acción para el futuro
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Ver en Galería")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seleccionar de Galería")
                }

                // --- BOTÓN ELIMINAR FOTO (Solo si hay foto) ---
                if (!photoUriString.isNullOrEmpty()) {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true }, // Abre el diálogo de confirmación
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error) // Color rojo
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Foto")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar Foto")
                    }
                }
            } // Fin Column de botones/imagen
            // === FIN SECCIÓN DE FOTOS ===


            Spacer(modifier = Modifier.weight(1f)) // Espacio flexible

            // Botón de publicar (lógica actualizada para pasar la Uri como String)
            Button(
                onClick = {
                    val photoUris = if (photoUriString != null) listOf(photoUriString!!) else emptyList()
                    onPublishClick(title.text, comment.text, photoUris)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Publicar Comentario", style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    // ==========================================================
    // === DIÁLOGO DE CONFIRMACIÓN PARA BORRAR LA FOTO ===
    // ==========================================================
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false }, // Cierra si toca fuera
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar la foto?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        photoUriString = null // Borra la Uri guardada
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
}
