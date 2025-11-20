package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
// Importamos los modelos y items que acabamos de crear
import com.example.qualifygym_grupo13.data.model.Publicacion
import com.example.qualifygym_grupo13.data.model.Tema
import com.example.qualifygym_grupo13.navigation.BottomNavItem
import com.example.qualifygym_grupo13.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null,
    onSearchClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onPublicationClick: (String) -> Unit,
    onCreatePublicationClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    // Obtener datos reales de la base de datos
    val publicacionesDb by publicacionViewModel?.allPublicaciones?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val temasDb by publicacionViewModel?.allTemas?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val currentUser by authViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    
    // Repositorio para obtener usuarios desde el microservicio
    val usuarioRepository = remember { UsuarioRepository() }
    
    // Filtrar publicaciones: mostrar ocultas solo si es admin
    val publicacionesFiltradas = remember(publicacionesDb, currentUser) {
        if (currentUser?.isAdmin == true) {
            publicacionesDb // El admin ve todas las publicaciones
        } else {
            publicacionesDb.filter { !it.oculta } // Los usuarios normales solo ven las no ocultas
        }
    }
    
    // Mapa para almacenar nombres de autores
    var autoresMap by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    
    // Cargar nombres de los autores de las últimas publicaciones desde el microservicio
    LaunchedEffect(publicacionesFiltradas) {
        val publicacionesParaMostrar = publicacionesFiltradas.take(10)
        if (publicacionesParaMostrar.isNotEmpty()) {
            val userIds = publicacionesParaMostrar.map { it.usuarioId }.distinct()
            val namesMap = mutableMapOf<Long, String>()
            
            userIds.forEach { userId ->
                // Obtener usuario desde el microservicio
                val userResult = usuarioRepository.fetchUsuarioById(userId)
                val userName = userResult.getOrNull()?.username ?: "Usuario"
                namesMap[userId] = userName
            }
            
            autoresMap = namesMap
        }
    }
    
    // Convertir datos de dominio a modelos de UI
    val sampleThemes = remember(temasDb, publicacionesFiltradas) {
        temasDb.map { temaDomain ->
            Tema(
                id = temaDomain.idTema.toString(),
                nombre = temaDomain.nombreTema,
                descripcion = "Explora publicaciones sobre ${temaDomain.nombreTema}",
                ubicacion = "",
                numeroComentarios = publicacionesFiltradas.count { it.temaId == temaDomain.idTema && !it.oculta }
            )
        }
    }
    
    val samplePosts = remember(publicacionesFiltradas, autoresMap) {
        publicacionesFiltradas.take(10).map { pubDomain ->
            Publicacion(
                id = pubDomain.idPublicacion.toString(),
                titulo = pubDomain.titulo,
                autor = autoresMap[pubDomain.usuarioId] ?: "Usuario",
                contenido = pubDomain.descripcion
            )
        }
    }


    Scaffold(
        // --- BARRA DE NAVEGACIÓN INFERIOR (NavigationBar) ---
        bottomBar = {
            AppBottomNavigation(
                onHomeClick = { /* Ya estamos en Home */ },
                onSearchClick = onSearchClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        // --- CONTENIDO PRINCIPAL CON SCROLL ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Padding del Scaffold para no solapar con la barra
                .padding(horizontal = 16.dp), // Padding lateral para el contenido
            verticalArrangement = Arrangement.spacedBy(24.dp) // Espacio entre cada sección
        ) {
            // Espacio superior
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- BARRA DE BÚSQUEDA (FALSA) ---
            item {
                Card(
                    onClick = onSearchClick, // Navega al hacer clic
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Buscar en los foros...")
                    }
                }
            }

            // --- SECCIÓN DE TEMAS POPULARES ---
            item {
                SectionTitle(text = "Temas Populares", icon = Icons.Default.FitnessCenter) //Icono de la pesa xd
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(sampleThemes) { tema ->
                        TopicCard(tema = tema, onClick = { onTopicClick(tema.id) })
                    }
                }
            }

            // --- BOTÓN CREAR NUEVA PUBLICACIÓN ---
            item {
                Button(
                    onClick = onCreatePublicationClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Crear Nueva Publicación")
                }
            }

            // --- SECCIÓN DE ÚLTIMAS PUBLICACIONES ---
            item {
                SectionTitle(text = "Últimas Publicaciones", icon = Icons.AutoMirrored.Filled.Article)
            }
            items(samplePosts) { post ->
                PublicationCard(publicacion = post, onClick = { onPublicationClick(post.id) })
            }

            // Espacio inferior para que el último item no quede pegado a la barra
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ===================================================================
//  COMPONENTES INTERNOS (AYUDAN A CONSTRUIR LA PANTALLA)
// ===================================================================

/**
 * La barra de navegación inferior con 3 iconos.
 */
@Composable
fun AppBottomNavigation(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // Estado para saber qué ítem está seleccionado
    var selectedItem by remember { mutableStateOf(BottomNavItem.Home.route) }
    val items = listOf(BottomNavItem.Home, BottomNavItem.Search, BottomNavItem.Profile)

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedItem == item.route, // Marca el item como activo
                onClick = {
                    selectedItem = item.route
                    // Llama a la acción correspondiente
                    when(item.route) {
                        BottomNavItem.Home.route -> onHomeClick()
                        BottomNavItem.Search.route -> onSearchClick()
                        BottomNavItem.Profile.route -> onProfileClick()
                    }
                }
            )
        }
    }
}

/**
 * Un título de sección con un icono.
 */
@Composable
fun SectionTitle(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    ) {
        Icon(icon, contentDescription = text, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

/**
 * Tarjeta para mostrar un Tema en la lista horizontal.
 * Diseño mejorado inspirado en las imágenes proporcionadas.
 */
@Composable
fun TopicCard(tema: Tema, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(200.dp)
            .height(160.dp), // Altura fija para consistencia
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Área de imagen placeholder (como en la primera imagen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder para imagen del tema
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = "Imagen del tema",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Nombre del tema
            Text(
                text = tema.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Ubicación si está disponible
            if (tema.ubicacion.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tema.ubicacion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Número de publicaciones
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Comment,
                    contentDescription = "Comentarios",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${tema.numeroComentarios}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Tarjeta para mostrar una Publicación en la lista vertical.
 */
@Composable
fun PublicationCard(publicacion: Publicacion, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(publicacion.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("por ${publicacion.autor}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(publicacion.contenido, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}