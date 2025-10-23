package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreatePublicationScreen(
    onPublished: () -> Unit
) {
    val (title, setTitle) = remember { mutableStateOf("") }
    val (desc, setDesc) = remember { mutableStateOf("") }
    val (topic, setTopic) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
        Button(
            onClick = onPublished,
            enabled = title.isNotBlank() && topic.isNotBlank() && desc.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar")
        }
    }
}

