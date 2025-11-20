package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.qualifygym_grupo13.data.domain.PublicacionDomain
import com.example.qualifygym_grupo13.data.domain.TemaDomain
import com.example.qualifygym_grupo13.data.domain.UserDomain
import com.example.qualifygym_grupo13.data.repository.UsuarioRepository
import com.example.qualifygym_grupo13.data.repository.toUserDomain
import kotlinx.coroutines.launch

// Enum para las pestañas del admin
enum class AdminTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Panel de Control", Icons.Default.Dashboard),
    USERS("Usuarios", Icons.Default.People),
    PUBLICATIONS("Publicaciones", Icons.Default.Article),
    THEMES("Temas", Icons.Default.Category),
    STATISTICS("Estadísticas", Icons.Default.BarChart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null,
    onBack: () -> Unit,
    onViewPostDetail: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(AdminTab.DASHBOARD) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de pestañas deslizable
            ScrollableTabRow(
                selectedTabIndex = AdminTab.values().indexOf(selectedTab),
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                edgePadding = 0.dp
            ) {
                AdminTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        text = { Text(tab.title) }
                    )
                }
            }
            
            // Contenido según la pestaña seleccionada
            when (selectedTab) {
                AdminTab.DASHBOARD -> AdminDashboardContent(
                    publicacionViewModel = publicacionViewModel,
                    authViewModel = authViewModel
                )
                AdminTab.USERS -> ManageUsersContent(
                    authViewModel = authViewModel
                )
                AdminTab.PUBLICATIONS -> ManagePublicationsContent(
                    publicacionViewModel = publicacionViewModel,
                    onViewDetail = onViewPostDetail
                )
                AdminTab.THEMES -> ManageThemesContent(
                    publicacionViewModel = publicacionViewModel
                )
                AdminTab.STATISTICS -> AdminStatisticsContent(
                    publicacionViewModel = publicacionViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
fun AdminDashboardContent(
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null
) {
    val publicaciones by publicacionViewModel?.allPublicaciones?.collectAsState() ?: remember { mutableStateOf(emptyList<PublicacionDomain>()) }
    val temas by publicacionViewModel?.allTemas?.collectAsState() ?: remember { mutableStateOf(emptyList<TemaDomain>()) }
    
    val usuarioRepository = remember { UsuarioRepository() }
    var totalUsuarios by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        publicacionViewModel?.loadAllPublicacionesIncluyendoOcultas()
        publicacionViewModel?.let { vm ->
            // Cargar usuarios
            val result = usuarioRepository.fetchUsuarios()
            result.onSuccess { usuarios ->
                totalUsuarios = usuarios.size
                isLoading = false
            }.onFailure {
                isLoading = false
            }
        } ?: run { isLoading = false }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Estadísticas Generales",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Usuarios",
                    value = if (isLoading) "..." else totalUsuarios.toString(),
                    icon = Icons.Default.People,
                    color = Color(0xFF424242),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Posts",
                    value = publicaciones.size.toString(),
                    icon = Icons.Default.Article,
                    color = Color(0xFF424242),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Temas",
                    value = temas.size.toString(),
                    icon = Icons.Default.Category,
                    color = Color(0xFF424242),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Comentarios",
                    value = "0", // TODO: Obtener de comentarios
                    icon = Icons.Default.Comment,
                    color = Color(0xFF424242),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun ManageUsersContent(
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null
) {
    val usuarioRepository = remember { UsuarioRepository() }
    var usuarios by remember { mutableStateOf<List<UserDomain>>(emptyList()) }
    var usuariosDto by remember { mutableStateOf<List<com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedUserForProfile by remember { mutableStateOf<com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto?>(null) }
    var selectedUserForRoleChange by remember { mutableStateOf<com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto?>(null) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            val result = usuarioRepository.fetchUsuarios()
            result.onSuccess { dtos ->
                usuariosDto = dtos
                usuarios = dtos.map { it.toUserDomain() }
                isLoading = false
            }.onFailure {
                isLoading = false
            }
        }
    }
    
    // Función para recargar usuarios
    fun reloadUsers() {
        scope.launch {
            val result = usuarioRepository.fetchUsuarios()
            result.onSuccess { dtos ->
                usuariosDto = dtos
                usuarios = dtos.map { it.toUserDomain() }
            }
        }
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(usuariosDto.size) { index ->
                val usuarioDto = usuariosDto[index]
                val usuario = usuarios[index]
                UserAdminCard(
                    usuario = usuario,
                    usuarioDto = usuarioDto,
                    onViewProfile = { selectedUserForProfile = usuarioDto },
                    onChangeRole = { selectedUserForRoleChange = usuarioDto },
                    onUserUpdated = { reloadUsers() }
                )
            }
        }
    }
    
    // Diálogo para ver perfil
    selectedUserForProfile?.let { usuario ->
        UserProfileDialog(
            usuario = usuario,
            onDismiss = { selectedUserForProfile = null }
        )
    }
    
    // Diálogo para cambiar rol
    selectedUserForRoleChange?.let { usuario ->
        ChangeRoleDialog(
            usuario = usuario,
            usuarioRepository = usuarioRepository,
            onDismiss = { selectedUserForRoleChange = null },
            onRoleChanged = {
                selectedUserForRoleChange = null
                reloadUsers()
            }
        )
    }
}

@Composable
fun UserAdminCard(
    usuario: UserDomain,
    usuarioDto: com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto,
    onViewProfile: () -> Unit,
    onChangeRole: () -> Unit,
    onUserUpdated: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = usuario.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = usuario.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = usuarioDto.rol?.nombre ?: "Usuario",
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            usuario.isAdmin -> MaterialTheme.colorScheme.primary
                            usuarioDto.rol?.nombre?.equals("Moderador", ignoreCase = true) == true -> Color(0xFF7B1FA2)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono para Moderadores
                    if (usuarioDto.rol?.nombre?.equals("Moderador", ignoreCase = true) == true) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Moderador",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    // Icono para Administradores
                    if (usuario.isAdmin || usuarioDto.rol?.nombre?.equals("Administrador", ignoreCase = true) == true) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Administrador",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Ver perfil") },
                                onClick = {
                                    showMenu = false
                                    onViewProfile()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cambiar rol") },
                                onClick = {
                                    showMenu = false
                                    onChangeRole()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.SwapHoriz,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManagePublicationsContent(
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    onViewDetail: (String) -> Unit
) {
    val allPublicaciones by publicacionViewModel?.allPublicaciones?.collectAsState() ?: remember { mutableStateOf(emptyList<PublicacionDomain>()) }
    val isLoading by publicacionViewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        publicacionViewModel?.loadAllPublicacionesIncluyendoOcultas()
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (allPublicaciones.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay publicaciones",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allPublicaciones) { publicacion ->
                var showMotivoDialog by remember { mutableStateOf(false) }
                
                PublicationAdminCard(
                    publicacion = publicacion,
                    onViewDetail = { onViewDetail(publicacion.idPublicacion.toString()) },
                    onToggleVisibility = {
                        if (publicacion.oculta) {
                            // Si está oculta, mostrar directamente sin diálogo
                            publicacionViewModel?.let { vm ->
                                scope.launch {
                                    vm.mostrarPublicacion(publicacion.idPublicacion, incluirOcultas = true)
                                }
                            }
                        } else {
                            // Si no está oculta, mostrar diálogo para escribir motivo
                            showMotivoDialog = true
                        }
                    }
                )
                
                // Diálogo para escribir motivo de ocultación
                if (showMotivoDialog) {
                    OcultarPublicacionDialog(
                        onDismiss = { showMotivoDialog = false },
                        onConfirm = { motivo ->
                            publicacionViewModel?.let { vm ->
                                scope.launch {
                                    vm.ocultarPublicacion(publicacion.idPublicacion, motivo, incluirOcultas = true)
                                }
                            }
                            showMotivoDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ManageThemesContent(
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null
) {
    val temas by publicacionViewModel?.allTemas?.collectAsState() ?: remember { mutableStateOf(emptyList<TemaDomain>()) }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(temas) { tema ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = tema.nombreTema,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AdminStatisticsContent(
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null
) {
    val publicaciones by publicacionViewModel?.allPublicaciones?.collectAsState() ?: remember { mutableStateOf(emptyList<PublicacionDomain>()) }
    val usuarioRepository = remember { UsuarioRepository() }
    var usuarios by remember { mutableStateOf<List<UserDomain>>(emptyList()) }
    var usuariosDto by remember { mutableStateOf<List<com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        publicacionViewModel?.loadAllPublicacionesIncluyendoOcultas()
        scope.launch {
            val result = usuarioRepository.fetchUsuarios()
            result.onSuccess { dtos ->
                usuariosDto = dtos
                usuarios = dtos.map { it.toUserDomain() }
                isLoading = false
            }.onFailure {
                isLoading = false
            }
        }
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Estadísticas Detalladas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Sección de Usuarios
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Usuarios",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailedStatCard(
                            value = usuarios.size.toString(),
                            label = "Total",
                            color = Color(0xFF424242),
                            icon = Icons.Default.People,
                            modifier = Modifier.weight(1f)
                        )
                        DetailedStatCard(
                            value = usuarios.count { it.isAdmin }.toString(),
                            label = "Admins",
                            color = Color(0xFF1976D2),
                            icon = Icons.Default.AdminPanelSettings,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailedStatCard(
                            value = usuarios.size.toString(), // TODO: Filtrar activos
                            label = "Activos",
                            color = Color(0xFF388E3C),
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                        DetailedStatCard(
                            value = usuariosDto.count { it.rol?.nombre?.equals("Moderador", ignoreCase = true) == true }.toString(),
                            label = "Moderadores",
                            color = Color(0xFF7B1FA2),
                            icon = Icons.Default.Security,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Sección de Publicaciones
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publicaciones",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailedStatCard(
                            value = publicaciones.count { !it.oculta }.toString(),
                            label = "Total Posts",
                            color = Color(0xFF424242),
                            icon = Icons.Default.Article,
                            modifier = Modifier.weight(1f)
                        )
                        DetailedStatCard(
                            value = "0", // TODO: Obtener de comentarios
                            label = "Total Comentarios",
                            color = Color(0xFF424242),
                            icon = Icons.Default.Comment,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailedStatCard(
                        value = publicaciones.count { it.oculta }.toString(),
                        label = "Publicaciones Ocultas",
                        color = Color(0xFFD32F2F),
                        icon = Icons.Default.VisibilityOff,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun DetailedStatCard(
    value: String,
    label: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OcultarPublicacionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ocultar Publicación",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Por favor, escribe el motivo por el cual se está ocultando esta publicación. Este mensaje se enviará como notificación al usuario.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { 
                        motivo = it
                        error = null
                    },
                    label = { Text("Motivo de ocultación") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    isError = error != null,
                    supportingText = {
                        if (error != null) {
                            Text(
                                text = error ?: "",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (motivo.trim().isEmpty()) {
                        error = "El motivo es obligatorio"
                    } else {
                        onConfirm(motivo.trim())
                    }
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun PublicationAdminCard(
    publicacion: PublicacionDomain,
    onViewDetail: () -> Unit,
    onToggleVisibility: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (publicacion.oculta) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            if (publicacion.oculta) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Oculta",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "OCULTA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Text(
                text = publicacion.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = publicacion.descripcion.take(100) + if (publicacion.descripcion.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onViewDetail) {
                    Text("Ver detalle")
                }
                TextButton(
                    onClick = onToggleVisibility,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (publicacion.oculta) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                ) {
                    Text(if (publicacion.oculta) "Mostrar" else "Ocultar")
                }
            }
        }
    }
}

@Composable
fun UserProfileDialog(
    usuario: com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Perfil de Usuario",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileInfoRow("Nombre", usuario.username)
                ProfileInfoRow("Email", usuario.email)
                ProfileInfoRow("Teléfono", usuario.phone ?: "No proporcionado")
                ProfileInfoRow("Rol", usuario.rol?.nombre ?: "Usuario")
                ProfileInfoRow("ID", usuario.id.toString())
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ChangeRoleDialog(
    usuario: com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto,
    usuarioRepository: UsuarioRepository,
    onDismiss: () -> Unit,
    onRoleChanged: () -> Unit
) {
    val context = LocalContext.current
    var selectedRole by remember { mutableStateOf(usuario.rol?.nombre ?: "Usuario") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    
    // Roles disponibles (ajusta según tu microservicio)
    val roles = listOf("Usuario", "Moderador", "Administrador")
    
    // Mapeo de nombres de roles a IDs (ajusta según tu microservicio)
    // Nota: Estos IDs pueden variar según tu base de datos
    val roleIdMap = mapOf(
        "Usuario" to 2L,
        "Moderador" to 3L,
        "Administrador" to 1L
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cambiar Rol",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Usuario: ${usuario.username}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                roles.forEach { role ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = role,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                TextButton(
                    onClick = {
                        if (selectedRole != usuario.rol?.nombre) {
                            isLoading = true
                            errorMessage = null
                            scope.launch {
                                try {
                                    // Obtener el usuario completo para actualizar
                                    val currentUserResult = usuarioRepository.fetchUsuarioById(usuario.id)
                                    val currentUser = currentUserResult.getOrNull()
                                    
                                    if (currentUser != null) {
                                        // Nota: Para cambiar el rol, necesitamos la contraseña actual
                                        // Como no la tenemos, intentaremos usar una contraseña temporal
                                        // Esto puede requerir ajustes en el microservicio
                                        val updateDto = com.example.qualifygym_grupo13.data.remote.dto.UsuarioCreateDto(
                                            username = currentUser.username,
                                            password = "temp", // El microservicio requiere password
                                            email = currentUser.email,
                                            phone = currentUser.phone ?: "",
                                            rolId = roleIdMap[selectedRole]
                                        )
                                        
                                        val result = usuarioRepository.update(usuario.id, updateDto)
                                        result.fold(
                                            onSuccess = {
                                                isLoading = false
                                                Toast.makeText(context, "Cambio de rol exitoso", Toast.LENGTH_SHORT).show()
                                                onRoleChanged()
                                            },
                                            onFailure = { error ->
                                                isLoading = false
                                                errorMessage = "Error al cambiar rol: ${error.message}"
                                            }
                                        )
                                    } else {
                                        isLoading = false
                                        errorMessage = "No se pudo obtener la información del usuario"
                                    }
                                } catch (e: Exception) {
                                    isLoading = false
                                    errorMessage = "Error: ${e.message}"
                                }
                            }
                        } else {
                            onDismiss()
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
