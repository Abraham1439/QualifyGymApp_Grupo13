package com.example.qualifygym_grupo13.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons // Íconos Material
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home // Ícono Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search // Ícono Buscar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield // Ícono Panel de Admin
import androidx.compose.material3.* // Material3
import androidx.compose.runtime.Composable // Marcador composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier // Modificador
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Tipo de ícono
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// Pequeña data class para representar cada opción del drawer
data class DrawerItem( // Estructura de un ítem de menú lateral
    val label: String, // Texto a mostrar
    val icon: ImageVector, // Ícono del ítem
    val onClick: () -> Unit // Acción al hacer click
)

@Composable // Componente Drawer para usar en ModalNavigationDrawer
fun AppDrawer(
    currentRoute: String?, // Ruta actual (para marcar seleccionado si quieres)
    items: List<DrawerItem>, // Lista de ítems a mostrar
    modifier: Modifier = Modifier, // Modificador opcional
    userName: String = "Usuario Demo",
    userEmail: String = "usuario@demo.com",
    userPhotoUri: Uri? = null
) {
    val context = LocalContext.current
    ModalDrawerSheet( // Hoja que contiene el contenido del drawer
        modifier = modifier // Modificador encadenable
    ) {
        // Cabecera del drawer con foto de perfil
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de perfil circular
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (userPhotoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(userPhotoUri),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Sin foto",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Nombre del usuario
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            // Email del usuario
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Recorremos las opciones principales (Inicio y Configuración)
        items.dropLast(1).forEach { item -> // Todos excepto el último (Cerrar Sesión)
            NavigationDrawerItem( // Ítem con estados Material
                label = { Text(item.label) }, // Texto visible
                selected = false, // Puedes usar currentRoute == ... si quieres marcar
                onClick = item.onClick, // Acción al pulsar
                icon = { Icon(item.icon, contentDescription = item.label) }, // Ícono
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                colors = NavigationDrawerItemDefaults.colors() // Estilo por defecto
            )
        }
        
        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón de cerrar sesión hacia abajo
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Botón de Cerrar Sesión al final
        items.lastOrNull()?.let { logoutItem ->
            NavigationDrawerItem(
                label = { Text(logoutItem.label) },
                selected = false,
                onClick = {
                    //Toast para notificar cierre de sesión en el menu de hamburguesa
                    Toast.makeText(context, "Se cerro session correctamente", Toast.LENGTH_SHORT).show()
                    logoutItem.onClick()},
                icon = { Icon(logoutItem.icon, contentDescription = logoutItem.label) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onErrorContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Helper para construir la lista estándar de ítems del drawer
@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,        // Acción Home
    onSearch: () -> Unit,      // Acción Buscar
    onSettings: () -> Unit,    // Acción Configuración
    onLogout: () -> Unit,      // Acción Cerrar Sesión
    onAdminDashboard: (() -> Unit)? = null // Acción Panel de Admin (opcional, solo para admins)
): List<DrawerItem> {
    val items = mutableListOf<DrawerItem>()
    
    items.add(DrawerItem("Inicio", Icons.Filled.Home, onHome))                         // Ítem Home
    items.add(DrawerItem("Buscar", Icons.Filled.Search, onSearch))                     // Ítem Buscar
    
    // Agregar ítem de admin solo si se proporciona el callback
    onAdminDashboard?.let {
        items.add(DrawerItem("Ir a panel de admin", Icons.Filled.Shield, it)) // Ítem Panel de Admin
    }
    
    items.add(DrawerItem("Configuración", Icons.Filled.Settings, onSettings))          // Ítem Configuración
    items.add(DrawerItem("Cerrar Sesión", Icons.AutoMirrored.Filled.ExitToApp, onLogout)) // Ítem Cerrar Sesión
    
    return items
}