package com.example.qualifygym_grupo13.ui.screen

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.example.qualifygym_grupo13.data.model.Publicacion
import com.example.qualifygym_grupo13.data.storage.ImageStorageManager
import com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel
import com.example.qualifygym_grupo13.domain.validation.validateNameLettersOnly
import com.example.qualifygym_grupo13.domain.validation.validateEmail
import com.example.qualifygym_grupo13.domain.validation.validatePhoneDigitsOnly
import kotlinx.coroutines.launch
import java.io.File

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
    onPublicationClick: (String) -> Unit, // Para ver el detalle de una publicación
    // ViewModels
    authViewModel: AuthViewModel,
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel
) {
    // Estado para saber qué pestaña está seleccionada (0 = Reseñas, 1 = Configuración)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Mis publicaciones", "Configuración")
    
    // Obtener el usuario actual y sus publicaciones
    val currentUser by authViewModel.currentUser.collectAsState()
    val userPublicaciones by publicacionViewModel.userPublicaciones.collectAsState()
    
    // Cargar publicaciones del usuario cuando se monta el componente
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            publicacionViewModel.loadUserPublicaciones(userId)
        }
    }

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
            0 -> MyReviewsContent(
                userPublicaciones = userPublicaciones,
                isAdmin = currentUser?.isAdmin == true,
                onPublicationClick = onPublicationClick
            )
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
private fun MyReviewsContent(
    userPublicaciones: List<com.example.qualifygym_grupo13.data.domain.PublicacionDomain>,
    isAdmin: Boolean,
    onPublicationClick: (String) -> Unit
) {
    // Repositorio para obtener usuarios desde el microservicio
    val usuarioRepository = remember { com.example.qualifygym_grupo13.data.repository.UsuarioRepository() }
    
    // Filtrar publicaciones: Si es admin, ver todas. Si no, solo las no ocultas
    val publicacionesFiltradas = if (isAdmin) {
        userPublicaciones
    } else {
        userPublicaciones.filter { !it.oculta }
    }
    
    // Mapa para almacenar los nombres de autores
    var autoresMap by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    
    // Cargar los nombres de los autores desde el microservicio
    LaunchedEffect(publicacionesFiltradas) {
        val userIds = publicacionesFiltradas.map { it.usuarioId }.distinct()
        val namesMap = mutableMapOf<Long, String>()
        
        userIds.forEach { userId ->
            // Obtener usuario desde el microservicio
            val userResult = usuarioRepository.fetchUsuarioById(userId)
            val userName = userResult.getOrNull()?.username ?: "Usuario"
            namesMap[userId] = userName
        }
        
        autoresMap = namesMap
    }
    
    // Convertir PublicacionDomain a Publicacion (modelo de UI)
    val publicacionesUI = publicacionesFiltradas.map { domain ->
        Publicacion(
            id = domain.idPublicacion.toString(),
            titulo = domain.titulo,
            autor = "tú",
            contenido = domain.descripcion
        )
    }

    if (publicacionesUI.isEmpty()) {
        // Mostrar mensaje cuando no hay publicaciones
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Article,
                    contentDescription = "Sin publicaciones",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No has creado publicaciones aún",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(publicacionesUI) { post ->
                // Reutilizamos el card de la HomeScreen para consistencia
                PublicationCard(publicacion = post, onClick = { onPublicationClick(post.id) })
            }
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
    val context = LocalContext.current
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
            onClick = {
                //Toast para notificar cierre de sesión en configuracion
                Toast.makeText(context, "Se cerro session correctamente,", Toast.LENGTH_SHORT).show()
                onLogout()},
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

// =====================Pantalla de edición de perfil =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    currentName: String = "",
    currentPhone: String = "",
    currentEmail: String = "",
    currentGender: String = "",
    currentPhotoUri: Uri? = null,
    authViewModel: AuthViewModel? = null,
    onSaved: (name: String, phone: String, email: String, gender: String, photoUri: Uri?) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageStorageManager = remember { ImageStorageManager(context) }
    
    // Obtener usuario actual del ViewModel para cargar su foto
    val currentUser = authViewModel?.currentUser?.collectAsState()?.value
    
    // Estados para los campos del formulario
    var name by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }
    var email by remember { mutableStateOf(currentEmail) }
    var gender by remember { mutableStateOf(currentGender) }
    var photoUri by remember { mutableStateOf<Uri?>(currentPhotoUri) }
    
    // Cargar la foto guardada del usuario al iniciar
    LaunchedEffect(currentUser?.photoUrl) {
        if (currentPhotoUri == null && currentUser?.photoUrl != null) {
            photoUri = imageStorageManager.pathToUri(currentUser.photoUrl)
        }
    }
    
    // Estados para manejo de errores y carga
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    
    // Estado para el diálogo de selección de foto
    var showPhotoDialog by remember { mutableStateOf(false) }
    
    // Función para crear el archivo temporal para la cámara
    // IMPORTANTE: Esta función debe definirse ANTES de los launchers que la usan
    fun createImageFile(): Uri {
        val imageFile = File(context.cacheDir, "images")
        imageFile.mkdirs()
        val file = File(imageFile, "profile_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    
    // Launcher para seleccionar de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { photoUri = it }
    }
    
    // Launcher para tomar foto con cámara
    val cameraUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri.value?.let { photoUri = it }
        }
    }
    
    // Launcher para solicitar permisos de cámara
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Si se otorgó el permiso, proceder a tomar la foto
            val uri = createImageFile()
            cameraUri.value = uri
            cameraLauncher.launch(uri)
        } else {
            // Si se denegó el permiso, mostrar mensaje
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Foto de Perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            // Sección de foto de perfil
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUri),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Sin foto",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botones de cámara y galería
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Cámara
                OutlinedButton(
                    onClick = {
                        // Verificar si ya tiene el permiso de cámara
                        val cameraPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        )
                        
                        if (cameraPermission == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            // Si ya tiene el permiso, abrir la cámara directamente
                            val uri = createImageFile()
                            cameraUri.value = uri
                            cameraLauncher.launch(uri)
                        } else {
                            // Si no tiene el permiso, solicitarlo
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cámara")
                }
                
                // Botón Galería
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galería")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sección de información personal
            Text(
                text = "Información Personal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            // Campo: Nombre completo
            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    // Filtrar solo letras y espacios (igual que en el registro)
                    val filtered = newValue.filter { it.isLetter() || it.isWhitespace() }
                    name = filtered
                    // Validar usando la función de validación
                    nameError = validateNameLettersOnly(filtered)
                },
                label = { Text("Nombre completo") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) {
                        Text(
                            text = nameError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo: Teléfono
            OutlinedTextField(
                value = phone,
                onValueChange = { newValue ->
                    // Filtrar solo dígitos (igual que en el registro)
                    val digitsOnly = newValue.filter { it.isDigit() }
                    phone = digitsOnly
                    // Validar usando la función de validación
                    phoneError = validatePhoneDigitsOnly(digitsOnly)
                },
                label = { Text("Teléfono") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = phoneError != null,
                supportingText = {
                    if (phoneError != null) {
                        Text(
                            text = phoneError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo: Correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { newValue ->
                    email = newValue
                    // Validar usando la función de validación
                    emailError = validateEmail(newValue)
                },
                label = { Text("Correo electrónico") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(
                            text = emailError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar mensaje de error si existe
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Cancelar
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
                
                // Botón Guardar Cambios
                Button(
                    onClick = {
                        // Limpiar mensajes anteriores
                        errorMessage = null
                        
                        // Validar todos los campos antes de guardar
                        nameError = validateNameLettersOnly(name)
                        emailError = validateEmail(email)
                        phoneError = validatePhoneDigitsOnly(phone)
                        
                        // Si hay algún error, mostrar mensaje y no guardar
                        if (nameError != null || emailError != null || phoneError != null) {
                            errorMessage = "Debe rellenar todos los campos del formulario"
                            return@Button
                        }
                        
                        // Actualizar perfil a través del ViewModel
                        scope.launch {
                            isLoading = true
                            
                            // 1. Guardar la foto de perfil si hay una nueva
                            var savedPhotoPath: String? = currentUser?.photoUrl // Mantener la actual
                            
                            // Solo guardar nueva foto si se cambió (es diferente a la actual)
                            if (photoUri != null && photoUri != imageStorageManager.pathToUri(currentUser?.photoUrl)) {
                                val newPhotoPath = imageStorageManager.saveProfileImage(
                                    photoUri!!,
                                    currentUser?.id ?: 0L
                                )
                                
                                if (newPhotoPath != null) {
                                    // Si se guardó exitosamente, usar la nueva ruta
                                    savedPhotoPath = newPhotoPath
                                } else {
                                    errorMessage = "Error al guardar la foto de perfil"
                                    isLoading = false
                                    return@launch
                                }
                            }
                            
                            // 2. Actualizar información del perfil
                            // La contraseña es opcional - si no se proporciona, solo se actualizan nombre, email y teléfono
                            val result = authViewModel?.updateUserProfile(name, email, phone, null)
                            
                            // 3. Actualizar foto de perfil en la base de datos
                            if (result?.isSuccess == true && savedPhotoPath != currentUser?.photoUrl) {
                                authViewModel.updateUserProfilePhoto(savedPhotoPath)
                            }
                            
                            isLoading = false
                            
                            if (result?.isSuccess == true) {
                                // Mostrar Toast de éxito
                                Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                                onSaved(name, phone, email, gender, photoUri)
                            } else {
                                val errorMsg = result?.exceptionOrNull()?.message ?: "Error al actualizar el perfil"
                                if (errorMsg.contains("correo ya está siendo utilizado")) {
                                    emailError = errorMsg
                                    // No mostrar este error en el cuadro rojo, solo debajo del campo de email
                                    errorMessage = null
                                } else {
                                    // Solo mostrar otros errores en el cuadro rojo
                                    errorMessage = errorMsg
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar Cambios")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

