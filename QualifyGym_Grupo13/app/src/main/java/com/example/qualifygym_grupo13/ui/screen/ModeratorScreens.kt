package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.qualifygym_grupo13.data.domain.PublicacionDomain
import com.example.qualifygym_grupo13.data.domain.UserDomain
import com.example.qualifygym_grupo13.data.repository.UsuarioRepository
import com.example.qualifygym_grupo13.data.repository.toUserDomain
import kotlinx.coroutines.launch

//Pantalla de moderador


// Enum para las pestañas del moderador
enum class ModeratorTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Panel de Control", Icons.Default.Dashboard),
    USERS("Usuarios", Icons.Default.People),
    PUBLICATIONS("Publicaciones", Icons.Default.Article),
    STATISTICS("Estadísticas", Icons.Default.BarChart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorMainScreen(
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null,
    onBack: () -> Unit,
    onViewPostDetail: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(ModeratorTab.DASHBOARD) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Moderador") },
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
                selectedTabIndex = ModeratorTab.values().indexOf(selectedTab),
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                edgePadding = 0.dp
            ) {
                ModeratorTab.values().forEach { tab ->
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
                ModeratorTab.DASHBOARD -> ModeratorDashboardContent(
                    publicacionViewModel = publicacionViewModel,
                    authViewModel = authViewModel
                )
                ModeratorTab.USERS -> ModeratorManageUsersContent(
                    authViewModel = authViewModel
                )
                ModeratorTab.PUBLICATIONS -> ModeratorManagePublicationsContent(
                    publicacionViewModel = publicacionViewModel,
                    onViewDetail = onViewPostDetail
                )
                ModeratorTab.STATISTICS -> ModeratorStatisticsContent(
                    publicacionViewModel = publicacionViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
fun ModeratorDashboardContent(
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel? = null,
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null
) {
    val publicaciones by publicacionViewModel?.allPublicaciones?.collectAsState() ?: remember { mutableStateOf(emptyList<PublicacionDomain>()) }
    
    val usuarioRepository = remember { UsuarioRepository() }
    var totalUsuarios by remember { mutableStateOf(0) }
    var totalComentarios by remember { mutableStateOf(0) }
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
        // TODO: Obtener comentarios del microservicio
        totalComentarios = 0
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
            StatCard(
                title = "Comentarios",
                value = totalComentarios.toString(),
                icon = Icons.Default.Comment,
                color = Color(0xFF424242),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ModeratorManageUsersContent(
    authViewModel: com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel? = null
) {
    val usuarioRepository = remember { UsuarioRepository() }
    var usuarios by remember { mutableStateOf<List<UserDomain>>(emptyList()) }
    var usuariosDto by remember { mutableStateOf<List<com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedUserForProfile by remember { mutableStateOf<com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto?>(null) }
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
                ModeratorUserCard(
                    usuario = usuario,
                    usuarioDto = usuarioDto,
                    onViewProfile = { selectedUserForProfile = usuarioDto }
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
}

@Composable
fun ModeratorUserCard(
    usuario: UserDomain,
    usuarioDto: com.example.qualifygym_grupo13.data.remote.dto.UsuarioDto,
    onViewProfile: () -> Unit
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
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModeratorManagePublicationsContent(
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
fun ModeratorStatisticsContent(
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

