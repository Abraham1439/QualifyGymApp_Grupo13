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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qualifygym_grupo13.data.model.Publicacion

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
    onBack: () -> Unit,
    onWriteComment: (String) -> Unit = {}
) {
    // Datos de ejemplo basados en el postId
    val publicacion = remember(postId) {
        when {
            postId.contains("101") || postId.contains("pecho") -> Publicacion(
                id = "101",
                titulo = "¿Mejor rutina para pecho?",
                autor = "user123",
                contenido = "Llevo 3 meses entrenando pecho y no veo progreso significativo. ¿Alguien puede recomendarme una rutina efectiva? He probado con press de banca, aperturas con mancuernas y fondos, pero siento que no estoy haciendo algo bien. ¿Cuántas series y repeticiones recomiendan? ¿Es mejor entrenar pecho dos veces por semana o solo una vez?"
            )
            postId.contains("102") || postId.contains("Creatina") -> Publicacion(
                id = "102",
                titulo = "Opiniones sobre Creatina Monohidratada",
                autor = "ana_fit",
                contenido = "¿Realmente funciona la creatina? ¿Qué marcas recomiendan? He leído opiniones muy divididas. Algunos dicen que es el mejor suplemento después de la proteína, otros dicen que no hace nada. Me gustaría saber sus experiencias personales. ¿Cuánto tiempo tardaron en ver resultados? ¿Tuvieron efectos secundarios?"
            )
            else -> Publicacion(
                id = postId,
                titulo = "Publicación de ejemplo",
                autor = "usuario_demo",
                contenido = "Este es el contenido de la publicación. Aquí se mostrará toda la información detallada que el usuario escribió cuando creó esta publicación. Puede ser un texto largo con múltiples párrafos explicando su duda o compartiendo su experiencia."
            )
        }
    }
    
    // Comentarios de ejemplo
    val comentarios = remember {
        listOf(
            "Gran pregunta! Yo recomendaría enfocarte en el press inclinado." to "carlos_trainer",
            "La creatina es excelente, yo uso la de MyProtein." to "fitness_pro",
            "Deberías consultar con un entrenador profesional." to "maria_sport"
        )
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
                    text = publicacion.titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Autor
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
                        text = "por ${publicacion.autor}",
                        style = MaterialTheme.typography.bodyMedium,
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
                        text = publicacion.contenido,
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
                    text = "Comentarios (${comentarios.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Lista de comentarios
            items(comentarios.size) { index ->
                val (comentario, autor) = comentarios[index]
                CommentCard(
                    comentario = comentario,
                    autor = autor
                )
            }
            
            // Espacio al final para el FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CommentCard(
    comentario: String,
    autor: String
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
            // Autor del comentario
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
