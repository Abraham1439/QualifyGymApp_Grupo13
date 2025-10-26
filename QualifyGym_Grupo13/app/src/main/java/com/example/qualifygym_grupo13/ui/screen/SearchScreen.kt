package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.qualifygym_grupo13.data.model.Tema

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onTopicClick: (String) -> Unit
) {
    // Estado de búsqueda
    var searchQuery by remember { mutableStateOf("") }
    
    // Datos de ejemplo de temas
    val allThemes = remember {
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
            ),
            Tema(
                id = "4",
                nombre = "CrossFit y Funcional",
                descripcion = "Box especializado en entrenamiento funcional y CrossFit con coaches certificados.",
                ubicacion = "Vitacura, Av. Kennedy 1234",
                numeroPublicaciones = 120
            ),
            Tema(
                id = "5",
                nombre = "Yoga y Pilates",
                descripcion = "Estudio especializado en yoga, pilates y entrenamiento de flexibilidad.",
                ubicacion = "Las Condes, Av. Apoquindo 567",
                numeroPublicaciones = 95
            ),
            Tema(
                id = "6",
                nombre = "Entrenamiento Personal",
                descripcion = "Sesiones personalizadas de entrenamiento con profesionales certificados.",
                ubicacion = "Providencia, Av. Suecia 890",
                numeroPublicaciones = 78
            ),
            Tema(
                id = "7",
                nombre = "Spinning y Ciclismo",
                descripcion = "Clases de spinning indoor con música motivacional y entrenadores especializados.",
                ubicacion = "Ñuñoa, Av. Grecia 345",
                numeroPublicaciones = 54
            ),
            Tema(
                id = "8",
                nombre = "Natación",
                descripcion = "Clases de natación para todos los niveles con piscina climatizada.",
                ubicacion = "La Reina, Av. Larraín 678",
                numeroPublicaciones = 43
            ),
            Tema(
                id = "9",
                nombre = "Boxeo y Artes Marciales",
                descripcion = "Gimnasio especializado en boxeo, kickboxing y otras artes marciales.",
                ubicacion = "Santiago Centro, Av. Matta 234",
                numeroPublicaciones = 62
            ),
            Tema(
                id = "10",
                nombre = "Recuperación y Fisioterapia",
                descripcion = "Centro de recuperación física y fisioterapia deportiva.",
                ubicacion = "Las Condes, Av. El Golf 456",
                numeroPublicaciones = 38
            )
        )
    }
    
    // Filtrar temas basado en la búsqueda
    val filteredThemes = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allThemes
        } else {
            allThemes.filter { tema ->
                tema.nombre.contains(searchQuery, ignoreCase = true) ||
                tema.descripcion.contains(searchQuery, ignoreCase = true) ||
                tema.ubicacion.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Temas") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar en los foros...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mostrar número de resultados
            Text(
                text = if (searchQuery.isBlank()) {
                    "Todos los temas (${allThemes.size})"
                } else {
                    "Resultados encontrados: ${filteredThemes.size}"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Lista de resultados
            if (filteredThemes.isEmpty()) {
                // Mensaje cuando no hay resultados
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "No se encontraron temas",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Intenta con otra búsqueda",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Lista de temas filtrados
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredThemes) { tema ->
                        SearchThemeCard(
                            tema = tema,
                            onClick = { onTopicClick(tema.id) }
                        )
                    }
                    
                    // Espaciado final
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SearchThemeCard(
    tema: Tema,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Título del tema
            Text(
                text = tema.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Descripción
            Text(
                text = tema.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Ubicación y número de publicaciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ubicación
                if (tema.ubicacion.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tema.ubicacion.take(30) + if (tema.ubicacion.length > 30) "..." else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                
                // Número de publicaciones
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${tema.numeroPublicaciones} publicaciones",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

