package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
// Importamos los modelos y items que acabamos de crear
    import com.example.qualifygym_grupo13.data.model.Publicacion
import com.example.qualifygym_grupo13.data.model.Tema
import com.example.qualifygym_grupo13.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // Estas son las acciones que tu NavGraph le pasará
    // (Tu HomeScreen antiguo solo tenía onGoLogin y onGoRegister)
    onSearchClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onPublicationClick: (String) -> Unit,
    onCreatePublicationClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    // --- DATOS DE EJEMPLO ---
    // (En el futuro, esto vendrá de tu ViewModel)
    val sampleThemes = remember {
        listOf(
            Tema(
                id = "1",
                nombre = "Rutinas de Fuerza",
                descripcion = "Gimnasio moderno con equipamiento de última generación y personal capacitado para rutinas de fuerza y musculación.",
                ubicacion = "Las Condes, Av. Principal 123",
                numeroPublicaciones = 156
            ),
            Tema(
                id = "2",
                nombre = "Nutrición y Suplementos",
                descripcion = "Centro especializado en nutrición deportiva y suplementación para optimizar tu rendimiento físico.",
                ubicacion = "Providencia, Av. Libertador 456",
                numeroPublicaciones = 89
            ),
            Tema(
                id = "3",
                nombre = "Cardio y Resistencia",
                descripcion = "Instalaciones equipadas con las mejores máquinas cardiovasculares y programas de entrenamiento de resistencia.",
                ubicacion = "Ñuñoa, Av. Irarrázaval 789",
                numeroPublicaciones = 67
            )
        )
    }
    val samplePosts = remember {
        listOf(
            Publicacion(id = "101", titulo = "¿Mejor rutina para pecho?", autor = "user123", contenido = "Llevo 3 meses y no veo progreso..."),
            Publicacion(id = "102", titulo = "Opiniones Creatina Monohidratada", autor = "ana_fit", contenido = "¿Realmente funciona? ¿Qué marcas recomiendan?")
        )
    }
    // --- FIN DATOS DE EJEMPLO ---


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
                SectionTitle(text = "Últimas Publicaciones", icon = Icons.Default.Article)
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
                    Icons.Default.Article,
                    contentDescription = "Publicaciones",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${tema.numeroPublicaciones}",
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