package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qualifygym_grupo13.data.local.database.AppDatabase
import com.example.qualifygym_grupo13.data.model.Publicacion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PublicationsListScreen(
    topicId: String,
    onOpenPost: (String) -> Unit,
    onCreateNew: () -> Unit
) {
    val sample = remember(topicId) {
        // Datos de demo temporales
        List(8) { idx ->
            val id = "$topicId-post-$idx"
            id to "Título $idx del tema $topicId"
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNew) {
                Icon(Icons.Default.Add, contentDescription = "Nueva publicación")
            }
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sample) { (postId, title) ->
                ElevatedCard(onClick = { onOpenPost(postId) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Autor demo", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationDetailScreen(
    postId: String,
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null,
    onBack: () -> Unit,
    onWriteComment: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()
    
    // Estados para la publicación y comentarios
    var publicacion by remember { mutableStateOf<com.example.qualifygym_grupo13.data.local.publicacion.PublicacionEntity?>(null) }
    var autorName by remember { mutableStateOf("Usuario") }
    var isLoading by remember { mutableStateOf(true) }
    
    // Obtener comentarios de la base de datos
    val comentariosDb = publicacionViewModel?.getComentariosByPublicacionId(postId.toLongOrNull() ?: 0)?.collectAsState()?.value ?: emptyList()
    
    // Cargar publicación y autor
    LaunchedEffect(postId) {
        isLoading = true
        try {
            val pubId = postId.toLongOrNull()
            if (pubId != null) {
                // Obtener publicación
                publicacion = publicacionViewModel?.getPublicacionById(pubId)
                
                // Obtener nombre del autor
                publicacion?.let { pub ->
                    val user = db.userDao().getById(pub.Usuarios_id_usuario)
                    autorName = user?.name ?: "Usuario"
                }
            }
        } catch (e: Exception) {
            // Error al cargar
        } finally {
            isLoading = false
        }
    }
    
    // Función para formatear fecha
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Publicación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onWriteComment(postId) },
                icon = { Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = null) },
                text = { Text("Escribir Comentario") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            // Mostrar indicador de carga
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (publicacion == null) {
            // Mostrar mensaje de error si no se encontró la publicación
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Publicación no encontrada",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                
                // Título de la publicación
                item {
                    Text(
                        text = publicacion!!.titulo,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Autor y fecha
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "por $autorName",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• ${formatDate(publicacion!!.fecha)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Contenido de la publicación
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = publicacion!!.descripcion,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Sección de comentarios
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Comentarios (${comentariosDb.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Mostrar comentarios o mensaje si no hay
                if (comentariosDb.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Comment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No hay comentarios aún",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Sé el primero en comentar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // Lista de comentarios
                    items(comentariosDb) { comentarioEntity ->
                        var comentarioAutorName by remember { mutableStateOf("Usuario") }
                        
                        LaunchedEffect(comentarioEntity.Usuarios_id_usuario) {
                            val user = db.userDao().getById(comentarioEntity.Usuarios_id_usuario)
                            comentarioAutorName = user?.name ?: "Usuario"
                        }
                        
                        CommentCard(
                            comentario = comentarioEntity.comentario,
                            autor = comentarioAutorName,
                            fecha = formatDate(comentarioEntity.fecha_registro)
                        )
                    }
                }
                
                // Espacio al final para el FAB
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun CommentCard(
    comentario: String,
    autor: String,
    fecha: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Autor del comentario y fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = autor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                if (fecha.isNotEmpty()) {
                    Text(
                        text = fecha,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Contenido del comentario
            Text(
                text = comentario,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
