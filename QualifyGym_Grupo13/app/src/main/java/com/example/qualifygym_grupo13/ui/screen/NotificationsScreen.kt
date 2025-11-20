package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.qualifygym_grupo13.data.domain.NotificacionDomain
import com.example.qualifygym_grupo13.ui.viewmodel.NotificacionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notificacionViewModel: NotificacionViewModel? = null,
    currentUserId: Long? = null,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val notificaciones by notificacionViewModel?.notificaciones?.collectAsState() 
        ?: remember { mutableStateOf(emptyList<NotificacionDomain>()) }
    val isLoading by notificacionViewModel?.isLoading?.collectAsState() 
        ?: remember { mutableStateOf(false) }
    val errorMessage by notificacionViewModel?.errorMessage?.collectAsState() 
        ?: remember { mutableStateOf<String?>(null) }
    
    // Cargar notificaciones al iniciar
    LaunchedEffect(currentUserId) {
        if (currentUserId != null && currentUserId > 0) {
            notificacionViewModel?.loadNotificaciones(currentUserId)
            notificacionViewModel?.updateCountNoLeidas(currentUserId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (currentUserId != null && currentUserId > 0) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    notificacionViewModel?.marcarTodasComoLeidas(currentUserId)
                                    Toast.makeText(context, "Todas las notificaciones marcadas como leídas", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Marcar todas como leídas"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && notificaciones.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (notificaciones.isEmpty()) {
                EmptyNotificationsView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notificaciones) { notificacion ->
                        NotificationCard(
                            notificacion = notificacion,
                            onMarkAsRead = {
                                if (currentUserId != null) {
                                    scope.launch {
                                        notificacionViewModel?.marcarComoLeida(notificacion.idNotificacion, currentUserId)
                                    }
                                }
                            },
                            onDelete = {
                                if (currentUserId != null) {
                                    scope.launch {
                                        notificacionViewModel?.eliminarNotificacion(notificacion.idNotificacion, currentUserId)
                                    }
                                }
                            }
                        )
                    }
                }
            }
            
            // Mostrar mensaje de error si existe
            errorMessage?.let { error ->
                LaunchedEffect(error) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    notificacionViewModel?.clearMessages()
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notificacion: NotificacionDomain,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (!notificacion.leida) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (!notificacion.leida) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = "No leída",
                                modifier = Modifier.size(8.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Nueva",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Text(
                        text = notificacion.mensaje,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (!notificacion.leida) FontWeight.Bold else FontWeight.Normal
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Fecha: ${notificacion.fechaCreacion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!notificacion.leida) {
                    TextButton(onClick = onMarkAsRead) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Marcar como leída")
                    }
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No tienes notificaciones",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

