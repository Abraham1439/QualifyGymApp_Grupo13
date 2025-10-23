package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qualifygym_grupo13.data.model.Publicacion // Reutilizamos el modelo de datos

// Pega este código donde estaba tu antiguo ProfileScreen
@Composable
fun ProfileScreen(
    // Datos del usuario (vendrán de un ViewModel)
    name: String,
    email: String,
    // Acciones de navegación
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onHelpAndSupport: () -> Unit,
    onLogout: () -> Unit,
    onPublicationClick: (String) -> Unit // Para ver el detalle de una publicación
) {
    // Estado para saber qué pestaña está seleccionada (0 = Reseñas, 1 = Configuración)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Mis Reseñas", "Configuración")

    Column(modifier = Modifier.fillMaxSize()) {
        // 1. Cabecera con nombre y email (la crearemos en el siguiente paso)
        ProfileHeader(name = name, email = email)

        // 2. Pestañas de selección
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) },
                    icon = {
                        Icon(
                            imageVector = if (index == 0) Icons.Default.Star else Icons.Default.Settings,
                            contentDescription = title
                        )
                    }
                )
            }
        }

        // 3. Contenido dinámico según la pestaña (lo crearemos en el siguiente paso)
        when (selectedTabIndex) {
            0 -> MyReviewsContent(onPublicationClick = onPublicationClick)
            1 -> SettingsContent(
                onEditProfile = onEditProfile,
                onChangePassword = onChangePassword,
                onHelpAndSupport = onHelpAndSupport,
                onLogout = onLogout
            )
        }
    }
}

// Pega todo este bloque al final de tu archivo ProfileScreens.kt

@Composable
private fun ProfileHeader(name: String, email: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MyReviewsContent(onPublicationClick: (String) -> Unit) {
    // Datos de ejemplo. En una app real, vendrían del ViewModel.
    val userPosts = remember {
        listOf(
            Publicacion("201", "Excelente Gimnasio", "tú", "Muy buen equipamiento y personal atento..."),
            Publicacion("202", "Buen Entrenador", "tú", "Muy profesional y conocedor del tema...")
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(userPosts) { post ->
            // Reutilizamos el card de la HomeScreen para consistencia
            PublicationCard(publicacion = post, onClick = { onPublicationClick(post.id) })
        }
    }
}

@Composable
private fun SettingsContent(
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onHelpAndSupport: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Opciones de configuración
        SettingsItem(
            title = "Editar Perfil",
            icon = Icons.Default.Edit,
            onClick = onEditProfile
        )
        SettingsItem(
            title = "Cambiar Contraseña",
            icon = Icons.Default.LockReset,
            onClick = onChangePassword
        )
        SettingsItem(
            title = "Ayuda y Soporte",
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            onClick = onHelpAndSupport
        )

        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón de logout hacia abajo

        // Botón de Cerrar Sesión
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión")
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

