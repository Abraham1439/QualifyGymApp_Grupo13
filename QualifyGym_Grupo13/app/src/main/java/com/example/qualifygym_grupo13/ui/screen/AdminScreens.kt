package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminDashboardScreen(
    onManagePosts: () -> Unit,
    onManageUsers: () -> Unit,
    onManageThemes: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Panel de administrador", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onManagePosts, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Gestionar publicaciones") }
        Button(onClick = onManageUsers, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Gestionar usuarios") }
        Button(onClick = onManageThemes, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Gestionar temas") }
    }
}

@Composable
fun ManagePublicationsScreen() {
    val itemsData = remember { List(10) { idx -> "Publicación #$idx" } }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(itemsData) { title ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Text("[Ver detalle]  [Banear/Desbanear]", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ManageUsersScreen() {
    val users = remember { List(10) { idx -> "Usuario$idx | email$idx@demo.com | rol: user | activo" } }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { u ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(u, style = MaterialTheme.typography.bodyMedium)
                    Text("[Hacer admin]  [Banear]", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ManageThemesScreen() {
    val themes = remember { List(6) { idx -> "Tema $idx" } }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(themes) { t ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(t, style = MaterialTheme.typography.titleMedium)
                    Text("[Editar]  [Eliminar]", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

