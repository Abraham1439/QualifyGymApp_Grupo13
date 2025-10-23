package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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

@Composable
fun PublicationDetailScreen(
    postId: String,
    onBack: () -> Unit
) {
    Scaffold { inner ->
        Column(Modifier.padding(inner).padding(16.dp)) {
            Text("Detalle de publicación", style = MaterialTheme.typography.headlineSmall)
            Text("ID: $postId", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.padding(8.dp))
            Text("Contenido demo...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
