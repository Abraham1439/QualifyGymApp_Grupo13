package com.example.qualifygym_grupo13.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.qualifygym_grupo13.ui.components.AppDrawer
import com.example.qualifygym_grupo13.ui.components.AppTopBar
import com.example.qualifygym_grupo13.ui.components.defaultDrawerItems
import com.example.qualifygym_grupo13.ui.screen.HomeScreen
import com.example.qualifygym_grupo13.ui.screen.SplashScreen
import com.example.qualifygym_grupo13.ui.screen.TopicDetailScreen
import com.example.qualifygym_grupo13.ui.screen.WriteCommentScreen
import com.example.qualifygym_grupo13.ui.screen.PublicationsListScreen
import com.example.qualifygym_grupo13.ui.screen.PublicationDetailScreen
import com.example.qualifygym_grupo13.ui.screen.CreatePublicationScreen
import com.example.qualifygym_grupo13.ui.screen.ProfileScreen
import com.example.qualifygym_grupo13.ui.screen.EditProfileScreen
import com.example.qualifygym_grupo13.ui.screen.SearchScreen
import com.example.qualifygym_grupo13.ui.screen.AdminDashboardScreen
import com.example.qualifygym_grupo13.ui.screen.ManagePublicationsScreen
import com.example.qualifygym_grupo13.ui.screen.ManageUsersScreen
import com.example.qualifygym_grupo13.ui.screen.ManageThemesScreen
import com.example.qualifygym_grupo13.ui.screen.ForgotPasswordScreen
import com.example.qualifygym_grupo13.ui.screen.ChangePasswordScreen
import com.example.qualifygym_grupo13.ui.screen.LoginScreenVm
import com.example.qualifygym_grupo13.ui.screen.RegisterScreenVm
import com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel
import com.example.qualifygym_grupo13.data.storage.ImageStorageManager
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    publicacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel
) { // Recibe el controlador
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Estado del drawer
    val scope = rememberCoroutineScope() // Necesario para abrir/cerrar drawer

    //Obtener la ruta actual
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //Definir las rutas que no tendran el topbar/drawer
    val routesWithoutBars = listOf(
        Route.Splash.path,
        Route.Login.path,
        Route.Register.path,
        Route.Forgot.path,
        Route.ChangePassword.path
    )

    // Helpers de navegación (reutilizamos en topbar/drawer/botones)
    val goHome: () -> Unit    = { navController.navigate(Route.Home.path) }    // Ir a Home
    val goLogin: () -> Unit   = { navController.navigate(Route.Login.path) }   // Ir a Login
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } // Ir a Registro
    val goForgot: () -> Unit = { navController.navigate(Route.Forgot.path) } // Ir a recuperar
    val goProfile: () -> Unit = { navController.navigate(Route.Profile.path) } // Ir a Perfil/Configuración
    val goSearch: () -> Unit = { navController.navigate(Route.Search.path) } // Ir a Búsqueda
    val openTopicDetail: (String) -> Unit = { topicId -> navController.navigate(Route.TopicDetail.create(topicId)) }
    val openTopic: (String) -> Unit = { topicId -> navController.navigate(Route.PublicationsList.create(topicId)) }
    val openPost: (String) -> Unit = { postId -> navController.navigate(Route.PublicationDetail.create(postId)) }
    val openWriteComment: (String) -> Unit = { topicId -> navController.navigate(Route.WriteComment.create(topicId)) }
    val goChangePassword: () -> Unit = { navController.navigate(Route.ChangePassword.path) }
    
    // Acción de cerrar sesión
    val logout: () -> Unit = {
        // Limpiar los datos del login
        authViewModel.clearLoginData()
        // Navegar a Login y limpiar todo el historial anterior
        navController.navigate(Route.Login.path) {
            // Limpia toda la pila de navegación
            popUpTo(0) { inclusive = true }
        }
    }

    ModalNavigationDrawer( // Capa superior con drawer lateral

        drawerState = drawerState, //estado del drawer

        //Desabilitar el gesto de abrir el drawer en las pantallas sin barrra
        gesturesEnabled = !routesWithoutBars.contains(currentRoute),

        drawerContent = { // Contenido del drawer (menú)
            // Obtener usuario actual del ViewModel
            val currentUserDrawer by authViewModel.currentUser.collectAsState()
            val context = LocalContext.current
            val imageStorageManager = remember { ImageStorageManager(context) }
            
            AppDrawer( // Nuestro componente Drawer
                currentRoute = currentRoute, // Ruta actual
                items = defaultDrawerItems( // Lista estándar
                    onHome = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goHome() // Navega a Home
                    },
                    onSearch = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goSearch() // Navega a Búsqueda
                    },
                    onSettings = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goProfile() // Navega a Perfil/Configuración
                    },
                    onLogout = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        logout() // Cierra sesión
                    }
                ),
                userName = currentUserDrawer?.name ?: "Usuario Demo",
                userEmail = currentUserDrawer?.email ?: "usuario@demo.com",
                userPhotoUri = imageStorageManager.pathToUri(currentUserDrawer?.photoUrl)
            )
        }
    ) {
        // Obtener usuario actual para toda la barra superior
        val currentUser by authViewModel.currentUser.collectAsState()
        
        Scaffold (
            topBar = { // Barra superior con íconos/menú
                //Mostrar la topBar solo si la ruta actual no esta en la lista
                if (!routesWithoutBars.contains(currentRoute)) {
                    AppTopBar(
                        onOpenDrawer = { scope.launch { drawerState.open() } }, // Abre drawer
                        onHome = goHome,     // Botón Home
                        onLogin = goLogin,   // Botón Login
                        onRegister = goRegister, // Botón Registro
                        currentUser = currentUser // Pasar usuario actual para mostrar indicador de admin
                    )
                }
            }
        ){ innerPadding -> // Padding que evita solapar contenido
            NavHost( // Contenedor de destinos navegables
                navController = navController, // Controlador
                startDestination = Route.Splash.path, // Inicio: El Splash como la "intro" xd
                modifier = Modifier.padding(innerPadding) // Respeta topBar

            ) {

                composable(Route.Splash.path) {
                    // Observar el estado de verificación de sesión
                    val isCheckingSession by authViewModel.isCheckingSession.collectAsState()
                    val currentUser by authViewModel.currentUser.collectAsState()
                    
                    // Cuando termine de verificar la sesión, navegar según el resultado
                    LaunchedEffect(isCheckingSession) {
                        if (!isCheckingSession) {
                            // Si ya terminó de verificar
                            if (currentUser != null) {
                                // Si hay usuario logueado, ir a Home
                                navController.navigate(Route.Home.path) {
                                    popUpTo(Route.Splash.path) { inclusive = true }
                                }
                            } else {
                                // Si no hay usuario, ir a Login
                                navController.navigate(Route.Login.path) {
                                    popUpTo(Route.Splash.path) { inclusive = true }
                                }
                            }
                        }
                    }
                    
                    SplashScreen(
                        onFinished = {
                            // Este callback ya no es necesario, pero lo mantenemos por compatibilidad
                            // La navegación ahora se maneja en el LaunchedEffect anterior
                        }
                    )
                }

                composable(Route.Home.path) { // Destino Home
                    HomeScreen(
                        publicacionViewModel = publicacionViewModel,
                        authViewModel = authViewModel,
                        onSearchClick = goSearch,
                        onTopicClick = { topicId -> openTopic(topicId) }, // Cambiado a openTopic para mostrar publicaciones
                        onPublicationClick = { postId -> openPost(postId) },
                        onCreatePublicationClick = { navController.navigate(Route.CreatePublication.path) },
                        onProfileClick = { navController.navigate(Route.Profile.path) }
                    )
                }
                composable(Route.Search.path) { // Destino Búsqueda
                    SearchScreen(
                        publicacionViewModel = publicacionViewModel,
                        onBack = { navController.popBackStack() },
                        onTopicClick = { topicId -> openTopic(topicId) }, // Cambiado a openTopic para mostrar publicaciones
                        onPublicationClick = { postId -> openPost(postId) }
                    )
                }
                composable(Route.Login.path) { // Destino Login
                    //Limpiar formulario al navegar manualmente al login
                    LaunchedEffect(Unit) {
                        authViewModel.clearLoginData()
                    }

                    //1 modificamos el acceso a la pagina
                    // Usamos la versión con ViewModel (LoginScreenVm) para formularios/validación en tiempo real
                    LoginScreenVm(
                        vm = authViewModel,            // <-- NUEVO: pasamos VM inyectado
                        onLoginOkNavigateHome = goHome,            // Si el VM marca success=true, navegamos a Home
                        onGoRegister = goRegister                  // Enlace para ir a la pantalla de Registro
                    )
                }
                composable(Route.Register.path) { // Destino Registro

                    //Limpiar el formulario del register
                    LaunchedEffect(Unit) {
                        authViewModel.clearAllAuthData()
                    }

                    //2 modificamos el acceso a la pagina
                    // Usamos la versión con ViewModel (RegisterScreenVm) para formularios/validación en tiempo real
                    RegisterScreenVm(
                        vm = authViewModel,            // <-- NUEVO: pasamos VM inyectado
                        onRegisteredNavigateLogin = goLogin,       // Si el VM marca success=true, volvemos a Login
                        onGoLogin = goLogin                        // Botón alternativo para ir a Login
                    )
                }
                composable(Route.PublicationsList.path) { backStackEntry ->
                    val topicId = backStackEntry.arguments?.getString(Route.PublicationsList.ARG_TOPIC_ID).orEmpty()
                    PublicationsListScreen(
                        topicId = topicId,
                        publicacionViewModel = publicacionViewModel,
                        authViewModel = authViewModel,
                        onOpenPost = openPost,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Route.PublicationDetail.path) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString(Route.PublicationDetail.ARG_POST_ID).orEmpty()
                    PublicationDetailScreen(
                        postId = postId,
                        publicacionViewModel = publicacionViewModel,
                        authViewModel = authViewModel,
                        onBack = { navController.popBackStack() },
                        onWriteComment = { postId -> 
                            navController.navigate(Route.WriteCommentOnPost.create(postId))
                        }
                    )
                }
                composable(Route.CreatePublication.path) {
                    CreatePublicationScreen(
                        authViewModel = authViewModel,
                        publicacionViewModel = publicacionViewModel,
                        onPublished = {
                            navController.popBackStack()
                        },
                        onCancel = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(Route.Profile.path) {
                    // Obtener el usuario actual del ViewModel
                    val currentUser by authViewModel.currentUser.collectAsState()
                    val userName = currentUser?.name ?: "Usuario Demo"
                    val userEmail = currentUser?.email ?: "usuario@demo.com"

                    ProfileScreen(
                        name = userName,
                        email = userEmail,
                        onEditProfile = { navController.navigate(Route.EditProfile.path) },
                        onChangePassword = goChangePassword,
                        onHelpAndSupport = { /* TODO: Navegar a una pantalla de ayuda */ },
                        onPublicationClick = { postId ->
                            // Reutilizamos la navegación que ya tenías para ver el detalle de un post
                            openPost(postId)
                        },
                        onLogout = {
                            //Limpiar los datos del login
                            authViewModel.clearLoginData()

                            // Lógica para cerrar sesión
                            // 1. (Opcional) Limpiar estado en el ViewModel: authViewModel.logout()
                            // 2. Navegar a Login y limpiar todo el historial anterior
                            navController.navigate(Route.Login.path) {
                                // Limpia toda la pila de navegación para que el usuario no pueda volver atrás
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        },
                        authViewModel = authViewModel,
                        publicacionViewModel = publicacionViewModel
                    )
                }
                composable(Route.EditProfile.path) {
                    // Obtener datos del usuario actual
                    val currentUser by authViewModel.currentUser.collectAsState()
                    
                    EditProfileScreen(
                        currentName = currentUser?.name ?: "",
                        currentPhone = currentUser?.phone ?: "",
                        currentEmail = currentUser?.email ?: "",
                        currentGender = "",
                        currentPhotoUri = null,
                        authViewModel = authViewModel,
                        onSaved = { name, phone, email, gender, photoUri ->
                            // La lógica de guardar ahora se maneja dentro de EditProfileScreen
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Route.AdminDashboard.path) {
                    AdminDashboardScreen(
                        onManagePosts = { navController.navigate(Route.ManagePublications.path) },
                        onManageUsers = { navController.navigate(Route.ManageUsers.path) },
                        onManageThemes = { navController.navigate(Route.ManageThemes.path) }
                    )
                }
                composable(Route.TopicDetail.path) { backStackEntry ->
                    val topicId = backStackEntry.arguments?.getString(Route.TopicDetail.ARG_TOPIC_ID) ?: ""
                    val currentUser by authViewModel.currentUser.collectAsState()
                    val context = LocalContext.current
                    val scope = rememberCoroutineScope()
                    
                    // Datos de ejemplo para el tema y sus comentarios
                    val tema = when(topicId) {
                        "1" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "1",
                            nombre = "Rutinas de Fuerza",
                            descripcion = "Gimnasio moderno con equipamiento de última generación y personal capacitado para rutinas de fuerza y musculación.",
                            ubicacion = "Las Condes, Av. Principal 123",
                            numeroComentarios = 156
                        )
                        "2" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "2",
                            nombre = "Nutrición y Suplementos",
                            descripcion = "Centro especializado en nutrición deportiva y suplementación para optimizar tu rendimiento físico.",
                            ubicacion = "Providencia, Av. Libertador 456",
                            numeroComentarios = 89
                        )
                        "3" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "3",
                            nombre = "Cardio y Resistencia",
                            descripcion = "Instalaciones equipadas con las mejores máquinas cardiovasculares y programas de entrenamiento de resistencia.",
                            ubicacion = "Ñuñoa, Av. Irarrázaval 789",
                            numeroComentarios = 67
                        )
                        else -> com.example.qualifygym_grupo13.data.model.Tema(id = topicId, nombre = "Tema desconocido")
                    }

                    val comentarios = listOf(
                        com.example.qualifygym_grupo13.data.model.Comentario(
                            id = "101",
                            autor = "user123",
                            contenido = "Llevo 3 meses entrenando pecho y no veo progreso significativo. ¿Alguien puede recomendarme una rutina efectiva?",
                            fecha = "Hace 2 horas"
                        ),
                        com.example.qualifygym_grupo13.data.model.Comentario(
                            id = "102",
                            autor = "ana_fit",
                            contenido = "¿Realmente funciona la creatina? ¿Qué marcas recomiendan? He leído opiniones muy divididas.",
                            fecha = "Hace 5 horas"
                        )
                    )

                    TopicDetailScreen(
                        tema = tema,
                        comentarios = comentarios,
                        currentUser = currentUser,
                        onBackClick = { navController.popBackStack() },
                        onCommentClick = { commentId -> /* Navegar al detalle del comentario si es necesario */ },
                        onCreateCommentClick = { openWriteComment(topicId) },
                        onHideComment = { commentId ->
                            scope.launch {
                                // TODO: Implementar cuando se tenga el publicacionViewModel con comentarios reales
                                Toast.makeText(context, "Comentario ocultado", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onShowComment = { commentId ->
                            scope.launch {
                                // TODO: Implementar cuando se tenga el publicacionViewModel con comentarios reales
                                Toast.makeText(context, "Comentario mostrado", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDeleteComment = { commentId ->
                            scope.launch {
                                // TODO: Implementar cuando se tenga el publicacionViewModel con comentarios reales
                                Toast.makeText(context, "Comentario eliminado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
                composable(Route.WriteComment.path) { backStackEntry ->
                    val topicId = backStackEntry.arguments?.getString(Route.WriteComment.ARG_TOPIC_ID) ?: ""
                    // Datos de ejemplo para el tema
                    val tema = when(topicId) {
                        "1" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "1",
                            nombre = "Gym Power",
                            descripcion = "Gimnasio moderno con equipamiento de última generación y personal capacitado para rutinas de fuerza y musculación.",
                            ubicacion = "Av. Principal 123, Las Condes",
                            numeroComentarios = 156
                        )
                        "2" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "2",
                            nombre = "Fitness Co",
                            descripcion = "Centro especializado en nutrición deportiva y suplementación para optimizar tu rendimiento físico.",
                            ubicacion = "Av. Libertador 456, Providencia",
                            numeroComentarios = 89
                        )
                        "3" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "3",
                            nombre = "Cardio Center",
                            descripcion = "Instalaciones equipadas con las mejores máquinas cardiovasculares y programas de entrenamiento de resistencia.",
                            ubicacion = "Av. Irarrázaval 789, Ñuñoa",
                            numeroComentarios = 67
                        )
                        else -> com.example.qualifygym_grupo13.data.model.Tema(id = topicId, nombre = "Tema desconocido")
                    }

                    WriteCommentScreen(
                        tema = tema,
                        onBackClick = { navController.popBackStack() },
                        onPublishClick = { title, comment, photos ->
                            // TODO: Implementar lógica para publicar comentario
                            navController.popBackStack()
                        }
                    )
                }
                composable(Route.WriteCommentOnPost.path) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString(Route.WriteCommentOnPost.ARG_POST_ID) ?: ""
                    val currentUser by authViewModel.currentUser.collectAsState()
                    val context = LocalContext.current
                    val scope = rememberCoroutineScope()
                    
                    // Obtener información de la publicación para mostrar en la pantalla
                    var publicacionInfo by remember { mutableStateOf<com.example.qualifygym_grupo13.data.local.publicacion.PublicacionEntity?>(null) }
                    
                    LaunchedEffect(postId) {
                        val pubId = postId.toLongOrNull()
                        if (pubId != null) {
                            publicacionInfo = publicacionViewModel.getPublicacionById(pubId)
                        }
                    }
                    
                    // Crear tema con información de la publicación
                    val tema = remember(publicacionInfo) {
                        publicacionInfo?.let { pub ->
                            com.example.qualifygym_grupo13.data.model.Tema(
                                id = pub.id_publicacion.toString(),
                                nombre = pub.titulo,
                                descripcion = pub.descripcion,
                                ubicacion = "Publicación",
                                numeroComentarios = 0
                            )
                        } ?: com.example.qualifygym_grupo13.data.model.Tema(
                            id = postId,
                            nombre = "Publicación",
                            descripcion = "Comentando en publicación",
                            ubicacion = "Foro",
                            numeroComentarios = 0
                        )
                    }
                    
                    WriteCommentScreen(
                        tema = tema,
                        onBackClick = { navController.popBackStack() },
                        onPublishClick = { title, comment, photos ->
                            scope.launch {
                                if (currentUser == null) {
                                    Toast.makeText(context, "Debe iniciar sesión para comentar", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                
                                if (comment.isBlank()) {
                                    Toast.makeText(context, "El comentario no puede estar vacío", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                
                                val pubId = postId.toLongOrNull()
                                if (pubId == null) {
                                    Toast.makeText(context, "Error: ID de publicación inválido", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                
                                // Guardar el comentario en la base de datos
                                val result = publicacionViewModel.createComentario(
                                    comentario = comment,
                                    userId = currentUser!!.id,
                                    publicacionId = pubId
                                )
                                
                                if (result.isSuccess) {
                                    Toast.makeText(context, "Comentario publicado exitosamente", Toast.LENGTH_SHORT).show()
                                    // Esperar un momento para que Room actualice el Flow antes de volver
                                    kotlinx.coroutines.delay(300)
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Error al publicar comentario: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }
                composable(Route.ManagePublications.path) { ManagePublicationsScreen() }
                composable(Route.ManageUsers.path) { ManageUsersScreen() }
                composable(Route.ManageThemes.path) { ManageThemesScreen() }
                composable(Route.Forgot.path) {
                    ForgotPasswordScreen(
                        onBackToLogin = { navController.popBackStack() }
                    )
                }
                composable(Route.ChangePassword.path) {
                    ChangePasswordScreen(
                        authViewModel = authViewModel,
                        onPasswordChanged = { navController.popBackStack() },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}