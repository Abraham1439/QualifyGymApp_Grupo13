package com.example.qualifygym_grupo13.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.qualifygym_grupo13.data.domain.ComentarioDomain
import com.example.qualifygym_grupo13.data.domain.PublicacionDomain
import com.example.qualifygym_grupo13.data.repository.UsuarioRepository
import com.example.qualifygym_grupo13.data.storage.ImageStorageManager
import com.example.qualifygym_grupo13.data.repository.ImagenRepository
import com.example.qualifygym_grupo13.ui.screen.OcultarPublicacionDialog
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla que muestra la lista de publicaciones filtradas por tema
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationsListScreen(
    topicId: String,
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null,
    onOpenPost: (String) -> Unit,
    onBack: () -> Unit
) {
    val allPublicaciones by publicacionViewModel?.allPublicaciones?.collectAsState() ?: remember { mutableStateOf(emptyList<PublicacionDomain>()) }
    val allTemas by publicacionViewModel?.allTemas?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val currentUser by authViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    val usuarioRepository = remember { UsuarioRepository() }
    
    // Filtrar publicaciones por tema
    val publicacionesFiltradas = remember(allPublicaciones, topicId, currentUser) {
        val temaIdLong = topicId.toLongOrNull() ?: 0L
        val filtradas = allPublicaciones.filter { it.temaId == temaIdLong }
        
        // Si no es admin, filtrar las ocultas
        if (currentUser?.isAdmin != true && currentUser?.isModerator != true) {
            filtradas.filter { !it.oculta }
        } else {
            filtradas
        }
    }
    
    // Obtener nombre del tema
    val tema = allTemas.find { it.idTema.toString() == topicId }
    val temaNombre = tema?.nombreTema ?: "Tema"
    
    // Mapa para almacenar nombres de autores
    var autoresMap by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    
    LaunchedEffect(publicacionesFiltradas) {
        if (publicacionesFiltradas.isNotEmpty()) {
            val userIds = publicacionesFiltradas.map { it.usuarioId }.distinct()
            val namesMap = mutableMapOf<Long, String>()
            
            userIds.forEach { userId ->
                val userResult = usuarioRepository.fetchUsuarioById(userId)
                val userName = userResult.getOrNull()?.username ?: "Usuario"
                namesMap[userId] = userName
            }
            
            autoresMap = namesMap
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicaciones: $temaNombre") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
        if (publicacionesFiltradas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay publicaciones en este tema",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(publicacionesFiltradas) { publicacion ->
                    Card(
                        onClick = { onOpenPost(publicacion.idPublicacion.toString()) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = publicacion.titulo,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "por ${autoresMap[publicacion.usuarioId] ?: "Usuario"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = publicacion.descripcion.take(150) + if (publicacion.descripcion.length > 150) "..." else "",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3
                            )
                            if (publicacion.oculta && (currentUser?.isAdmin == true || currentUser?.isModerator == true)) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.VisibilityOff,
                                        contentDescription = "Oculta",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Publicación oculta",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Pantalla que muestra el detalle de una publicación con sus comentarios
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationDetailScreen(
    postId: String,
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null,
    onBack: () -> Unit,
    onWriteComment: (String) -> Unit = {},
    onPublicationHidden: () -> Unit = {},
    onPublicationDeleted: () -> Unit = {}
) {
    val currentUser by authViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    val context = LocalContext.current
    val usuarioRepository = remember { UsuarioRepository() }
    val scope = rememberCoroutineScope()
    val imageStorageManager = remember { ImageStorageManager(context) }
    val imagenRepository = remember { ImagenRepository(context = context) }
    
    // Estados para la publicación y comentarios
    var publicacion by remember { mutableStateOf<PublicacionDomain?>(null) }
    var autorNombre by remember { mutableStateOf<String?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMotivoDialog by remember { mutableStateOf(false) }
    
    val publicacionId = postId.toLongOrNull() ?: 0L
    
    // Cargar publicación
    LaunchedEffect(postId) {
        if (publicacionId > 0) {
            scope.launch {
                val pub = publicacionViewModel?.getPublicacionById(publicacionId)
                publicacion = pub
                // Cargar nombre del autor
                if (pub != null) {
                    val userResult = usuarioRepository.fetchUsuarioById(pub.usuarioId)
                    autorNombre = userResult.getOrNull()?.username ?: "Usuario"
                }
            }
        }
    }
    
    // Cargar comentarios
    val comentariosFlow = remember(publicacionId) {
        if (publicacionId > 0) {
            publicacionViewModel?.getComentariosByPublicacionId(
                publicacionId,
                incluirOcultos = currentUser?.isAdmin == true || currentUser?.isModerator == true
            )
        } else {
            null
        }
    }
    
    val comentarios by comentariosFlow?.collectAsState() ?: remember { mutableStateOf(emptyList<ComentarioDomain>()) }
    
    // Filtrar comentarios según el rol del usuario
    val comentariosFiltrados = remember(comentarios, currentUser) {
        if (currentUser?.isAdmin == true || currentUser?.isModerator == true) {
            comentarios // Admin/moderador ve todos
        } else {
            comentarios.filter { !it.oculto } // Usuario normal solo ve los no ocultos
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(publicacion?.titulo ?: "Publicación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Menú solo para admin/moderador o si es el autor
                    if ((currentUser?.isAdmin == true || currentUser?.isModerator == true) || 
                        (publicacion != null && currentUser?.id == publicacion?.usuarioId)) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // Opción para ocultar/mostrar (solo admin/moderador)
                            if (currentUser?.isAdmin == true || currentUser?.isModerator == true) {
                                DropdownMenuItem(
                                    text = { Text(if (publicacion?.oculta == true) "Mostrar publicación" else "Ocultar publicación") },
                                    onClick = {
                                        if (publicacion?.oculta == true) {
                                            scope.launch {
                                                publicacionViewModel?.mostrarPublicacion(publicacionId, incluirOcultas = true)
                                            }
                                        } else {
                                            showMotivoDialog = true
                                        }
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            if (publicacion?.oculta == true) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                            
                            // Opción para eliminar (solo si es el autor o admin)
                            if ((publicacion != null && currentUser?.id == publicacion?.usuarioId) || 
                                currentUser?.isAdmin == true) {
                                DropdownMenuItem(
                                    text = { Text("Eliminar publicación") },
                                    onClick = {
                                        showDeleteDialog = true
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onWriteComment(postId) },
                icon = { Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = null) },
                text = { Text("Comentar") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { paddingValues ->
        if (publicacion == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Información de la publicación
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = publicacion!!.titulo,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Autor",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = autorNombre ?: "Usuario",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = publicacion!!.descripcion,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                // Imagen de la publicación (si existe)
                if (publicacion?.imageUrl != null) {
                    item {
                        PublicationImageCard(
                            imageUrl = publicacion!!.imageUrl!!,
                            imagenRepository = imagenRepository,
                            imageStorageManager = imageStorageManager,
                            context = context,
                            scope = scope
                        )
                    }
                }
            
                // Sección de comentarios
                item {
                    Text(
                        text = "Comentarios (${comentariosFiltrados.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (comentariosFiltrados.isEmpty()) {
                    item {
                        Text(
                            text = "No hay comentarios aún",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(comentariosFiltrados) { comentario ->
                        CommentCard(
                            comentarioDomain = comentario,
                            publicacionId = publicacionId,
                            currentUser = currentUser,
                            publicacionViewModel = publicacionViewModel,
                            usuarioRepository = usuarioRepository,
                            scope = scope
                        )
                    }
                }
                
                // Espacio inferior
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
    
    // Diálogo para confirmar eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta publicación? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            publicacionViewModel?.deletePublicacion(publicacionId)
                            onPublicationDeleted()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo para escribir motivo de ocultación
    if (showMotivoDialog) {
        OcultarPublicacionDialog(
            onDismiss = { showMotivoDialog = false },
            onConfirm = { motivo ->
                scope.launch {
                    publicacionViewModel?.ocultarPublicacion(publicacionId, motivo, incluirOcultas = true)
                    onPublicationHidden()
                }
                showMotivoDialog = false
            }
        )
    }
}

/**
 * Componente para mostrar un comentario
 */
@Composable
private fun CommentCard(
    comentarioDomain: ComentarioDomain,
    publicacionId: Long,
    currentUser: com.example.qualifygym_grupo13.data.domain.UserDomain?,
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel?,
    usuarioRepository: UsuarioRepository,
    scope: kotlinx.coroutines.CoroutineScope
) {
    var autorNombre by remember { mutableStateOf<String?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showMotivoDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Cargar nombre del autor
    LaunchedEffect(comentarioDomain.usuarioId) {
        val userResult = usuarioRepository.fetchUsuarioById(comentarioDomain.usuarioId)
        autorNombre = userResult.getOrNull()?.username ?: "Usuario"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (comentarioDomain.oculto) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Autor",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = autorNombre ?: "Usuario",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                // Menú solo para admin/moderador o si es el autor
                if ((currentUser?.isAdmin == true || currentUser?.isModerator == true) || 
                    currentUser?.id == comentarioDomain.usuarioId) {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        // Opción para ocultar/mostrar (solo admin/moderador)
                        if (currentUser?.isAdmin == true || currentUser?.isModerator == true) {
                            DropdownMenuItem(
                                text = { Text(if (comentarioDomain.oculto) "Mostrar comentario" else "Ocultar comentario") },
                                onClick = {
                                    if (comentarioDomain.oculto) {
                                        // Si está oculto, mostrar directamente sin diálogo
                                        scope.launch {
                                            val result = publicacionViewModel?.mostrarComentario(
                                                comentarioDomain.idComentario,
                                                publicacionId
                                            )
                                            result?.onSuccess {
                                                Toast.makeText(
                                                    context,
                                                    "Comentario desocultado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }?.onFailure {
                                                Toast.makeText(
                                                    context,
                                                    "Error: ${it.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        // Si no está oculto, mostrar diálogo para escribir motivo
                                        showMotivoDialog = true
                                    }
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        if (comentarioDomain.oculto) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                        
                        // Opción para eliminar (solo si es el autor o admin)
                        if (currentUser?.id == comentarioDomain.usuarioId || currentUser?.isAdmin == true) {
                            DropdownMenuItem(
                                text = { Text("Eliminar comentario") },
                                onClick = {
                                    scope.launch {
                                        val result = publicacionViewModel?.deleteComentario(
                                            comentarioDomain.idComentario,
                                            publicacionId
                                        )
                                        result?.onSuccess {
                                            Toast.makeText(
                                                context,
                                                "Comentario eliminado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }?.onFailure {
                                            Toast.makeText(
                                                context,
                                                "Error al eliminar comentario: ${it.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (comentarioDomain.oculto) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.VisibilityOff,
                        contentDescription = "Oculto",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Este comentario ha sido ocultado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
                if (comentarioDomain.motivoBaneo != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Motivo: ${comentarioDomain.motivoBaneo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = comentarioDomain.comentario,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
    
    // Diálogo para escribir motivo de ocultación
    if (showMotivoDialog) {
        OcultarComentarioDialog(
            onDismiss = { showMotivoDialog = false },
            onConfirm = { motivo ->
                scope.launch {
                    val result = publicacionViewModel?.ocultarComentario(
                        comentarioDomain.idComentario,
                        motivo,
                        publicacionId
                    )
                    result?.onSuccess {
                        Toast.makeText(
                            context,
                            "Comentario ocultado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }?.onFailure {
                        Toast.makeText(
                            context,
                            "Error: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                showMotivoDialog = false
            }
        )
    }
}

/**
 * Diálogo para ocultar un comentario con motivo
 */
@Composable
private fun OcultarComentarioDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ocultar Comentario",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Por favor, escribe el motivo por el cual se está ocultando este comentario. Este mensaje se enviará como notificación al usuario.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = motivo,
                    onValueChange = {
                        motivo = it
                        error = null
                    },
                    label = { Text("Motivo de ocultación") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    isError = error != null,
                    supportingText = {
                        if (error != null) {
                            Text(
                                text = error ?: "",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (motivo.trim().isEmpty()) {
                        error = "El motivo es obligatorio"
                    } else {
                        onConfirm(motivo.trim())
                    }
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Componente para mostrar la imagen de una publicación
 * Soporta tanto imágenes del microservicio (por ID) como imágenes locales (por path)
 */
@Composable
private fun PublicationImageCard(
    imageUrl: String,
    imagenRepository: ImagenRepository,
    imageStorageManager: ImageStorageManager,
    context: Context,
    scope: kotlinx.coroutines.CoroutineScope
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoadingImage by remember { mutableStateOf(true) }
    
    // Determinar si imageUrl es un ID del microservicio o un path local
    val imagenId = imageUrl.toLongOrNull()
    
    LaunchedEffect(imageUrl) {
        if (imagenId != null) {
            // Es un ID del microservicio, obtener la imagen
            val result = imagenRepository.obtenerImagenPorId(imagenId)
            result.onSuccess { bitmap ->
                if (bitmap != null) {
                    // Guardar temporalmente y crear URI
                    val tempFile = File(context.cacheDir, "temp_publication_${imagenId}.jpg")
                    FileOutputStream(tempFile).use { out ->
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                    }
                    imageUri = Uri.fromFile(tempFile)
                }
                isLoadingImage = false
            }.onFailure {
                isLoadingImage = false
            }
        } else {
            // Es un path local (compatibilidad)
            imageUri = imageStorageManager.pathToUri(imageUrl)
            isLoadingImage = false
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        if (isLoadingImage) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Imagen de la publicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Fit
            )
        }
    }
}
