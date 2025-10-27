package com.example.qualifygym_grupo13.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.example.qualifygym_grupo13.data.local.database.AppDatabase
import com.example.qualifygym_grupo13.data.model.Publicacion
import com.example.qualifygym_grupo13.data.storage.ImageStorageManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationsListScreen(
    topicId: String,
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null,
    onOpenPost: (String) -> Unit,
    onBack: () -> Unit,
    onCreateNew: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    
    // Obtener publicaciones del tema desde la base de datos
    val publicacionesDb by publicacionViewModel?.allPublicaciones?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val currentUser by authViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    
    // Obtener el nombre del tema
    var temaNombre by remember { mutableStateOf("Publicaciones") }
    
    LaunchedEffect(topicId) {
        val temaId = topicId.toLongOrNull()
        if (temaId != null) {
            val tema = db.temaDao().getById(temaId)
            temaNombre = tema?.nombre_tema ?: "Publicaciones"
        }
    }
    
    // Filtrar publicaciones por tema y por visibilidad
    val publicacionesFiltradas = publicacionesDb.filter { 
        val esMismoTema = it.Tema_id_tema == topicId.toLongOrNull()
        val esAdmin = currentUser?.isAdmin == true
        
        // Si es admin, ver todas del tema. Si no, solo las no ocultas
        esMismoTema && (esAdmin || !it.oculta)
    }
    
    // Cargar nombres de autores
    var autoresMap by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    
    LaunchedEffect(publicacionesFiltradas) {
        val userIds = publicacionesFiltradas.map { it.Usuarios_id_usuario }.distinct()
        val namesMap = mutableMapOf<Long, String>()
        
        userIds.forEach { userId ->
            val user = db.userDao().getById(userId)
            namesMap[userId] = user?.name ?: "Usuario"
        }
        
        autoresMap = namesMap
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(temaNombre) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a búsqueda"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { inner ->
        if (publicacionesFiltradas.isEmpty()) {
            // Mostrar mensaje cuando no hay publicaciones
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No hay publicaciones en este tema",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Sé el primero en publicar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                items(publicacionesFiltradas) { publicacion ->
                    val autorNombre = autoresMap[publicacion.Usuarios_id_usuario] ?: "Usuario"
                    
                    ElevatedCard(
                        onClick = { onOpenPost(publicacion.id_publicacion.toString()) }, 
                        modifier = Modifier.fillMaxWidth()
                    ) {
                    Column(Modifier.padding(16.dp)) {
                            Text(
                                text = publicacion.titulo, 
                                style = MaterialTheme.typography.titleMedium, 
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "por $autorNombre", 
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = publicacion.descripcion,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
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
    onWriteComment: (String) -> Unit = {},
    onPublicationHidden: () -> Unit = {},
    onPublicationDeleted: () -> Unit = {}
) {
    val currentUser by authViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()
    val imageStorageManager = remember { ImageStorageManager(context) }
    
    // Estados para la publicación y comentarios
    var publicacion by remember { mutableStateOf<com.example.qualifygym_grupo13.data.local.publicacion.PublicacionEntity?>(null) }
    var autorName by remember { mutableStateOf("Usuario") }
    var isLoading by remember { mutableStateOf(true) }
    var showAdminMenu by remember { mutableStateOf(false) }
    
    // Obtener comentarios de la base de datos
    val comentariosDb = publicacionViewModel?.getComentariosByPublicacionId(postId.toLongOrNull() ?: 0)?.collectAsState(initial = emptyList())?.value ?: emptyList()
    
    // Mapa para almacenar nombres de usuarios (key: userId, value: userName)
    var userNamesMap by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    
    // Cargar publicación, autor y nombres de usuarios de los comentarios
    LaunchedEffect(postId) {
        isLoading = true
        try {
            val pubId = postId.toLongOrNull()
            if (pubId != null) {
                // Obtener publicación
                publicacion = publicacionViewModel?.getPublicacionById(pubId)
                
                // Obtener nombre del autor de la publicación
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
    
    // Cargar nombres de todos los usuarios que comentaron (solo cuando cambian los comentarios)
    LaunchedEffect(comentariosDb) {
        if (comentariosDb.isNotEmpty()) {
            val userIds = comentariosDb.map { it.Usuarios_id_usuario }.distinct()
            val namesMap = mutableMapOf<Long, String>()
            
            userIds.forEach { userId ->
                val user = db.userDao().getById(userId)
                namesMap[userId] = user?.name ?: "Usuario"
            }
            
            userNamesMap = namesMap
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
                title = { 
                    Column {
                        Text("Detalle de Publicación")
                        // Indicador de administrador
                        if (currentUser?.isAdmin == true) {
                            Text(
                                text = "👑 Modo Administrador",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                },
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
            
                // Título de la publicación con menú de administrador
            item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                Text(
                                text = publicacion!!.titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                            
                            // Indicador de publicación oculta (solo visible para admin)
                            if (currentUser?.isAdmin == true && publicacion!!.oculta) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    tonalElevation = 2.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Text(
                                            text = "Publicación oculta",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Menú de administrador (solo visible para admins)
                        if (currentUser?.isAdmin == true) {
                            Box {
                                IconButton(onClick = { showAdminMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Opciones de administrador",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                DropdownMenu(
                                    expanded = showAdminMenu,
                                    onDismissRequest = { showAdminMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Ocultar publicación") },
                                        onClick = {
                                            scope.launch {
                                                publicacion?.let { pub ->
                                                    val updatedPub = pub.copy(oculta = true)
                                                    db.publicacionDao().update(updatedPub)
                                                    Toast.makeText(context, "Publicación ocultada", Toast.LENGTH_SHORT).show()
                                                    onPublicationHidden()
                                                }
                                            }
                                            showAdminMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.VisibilityOff,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Mostrar publicación") },
                                        onClick = {
                                            scope.launch {
                                                publicacion?.let { pub ->
                                                    val updatedPub = pub.copy(oculta = false)
                                                    db.publicacionDao().update(updatedPub)
                                                    Toast.makeText(context, "Publicación mostrada", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            showAdminMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Visibility,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Borrar publicación") },
                                        onClick = {
                                            scope.launch {
                                                publicacion?.let { pub ->
                                                    db.publicacionDao().deleteById(pub.id_publicacion)
                                                    Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show()
                                                    onPublicationDeleted()
                                                }
                                            }
                                            showAdminMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        },
                                        colors = MenuDefaults.itemColors(
                                            textColor = MaterialTheme.colorScheme.error
                                        )
                                    )
                                }
                            }
                        }
                    }
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
                
                // Imagen de la publicación (si existe)
                if (publicacion?.imageUrl != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            AsyncImage(
                                model = imageStorageManager.pathToUri(publicacion!!.imageUrl),
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
                        // Obtener el nombre del autor del mapa pre-cargado
                        val comentarioAutorName = userNamesMap[comentarioEntity.Usuarios_id_usuario] ?: "Usuario"
                        
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
