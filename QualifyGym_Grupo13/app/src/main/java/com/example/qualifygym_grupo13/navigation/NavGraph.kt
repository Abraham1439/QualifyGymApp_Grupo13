package com.example.qualifygym_grupo13.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(navController: NavHostController,
                authViewModel: AuthViewModel // <-- 1.- NUEVO: recibimos el VM inyectado desde MainActivity

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
            AppDrawer( // Nuestro componente Drawer
                currentRoute = currentRoute, // Ruta actual
                items = defaultDrawerItems( // Lista estándar
                    onHome = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goHome() // Navega a Home
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
                userName = "Usuario Demo", // TODO: Obtener del ViewModel
                userEmail = "usuario@demo.com", // TODO: Obtener del ViewModel
                userPhotoUri = null // TODO: Obtener del ViewModel
            )
        }
    ) {
        Scaffold (
            topBar = { // Barra superior con íconos/menú
                //Mostrar la topBar solo si la ruta actual no esta en la lista
                if (!routesWithoutBars.contains(currentRoute)) {
                    AppTopBar(
                        onOpenDrawer = { scope.launch { drawerState.open() } }, // Abre drawer
                        onHome = goHome,     // Botón Home
                        onLogin = goLogin,   // Botón Login
                        onRegister = goRegister // Botón Registro
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
                    SplashScreen(
                        onFinished = {
                            // Por ahora vamos a Home. Si tuvieras auth persistido, puedes decidir aquí
                            navController.navigate(Route.Login.path) {
                                popUpTo(Route.Splash.path) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Route.Home.path) { // Destino Home
                    HomeScreen(
                        // Aquí conectamos las acciones a tus rutas de navegación
                        onSearchClick = goSearch, // Navega a la pantalla de búsqueda
                        onTopicClick = { topicId -> openTopicDetail(topicId) }, // Navega al detalle del tema
                        onPublicationClick = { postId -> openPost(postId) }, // Esto también (usa tu "openPost")
                        onCreatePublicationClick = { navController.navigate(Route.CreatePublication.path) },
                        onProfileClick = { navController.navigate(Route.Profile.path) }
                    )
                }
                composable(Route.Search.path) { // Destino Búsqueda
                    SearchScreen(
                        onBack = { navController.popBackStack() },
                        onTopicClick = { topicId -> openTopicDetail(topicId) }
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
                        onOpenPost = openPost,
                        onCreateNew = { navController.navigate(Route.CreatePublication.path) }
                    )
                }
                composable(Route.PublicationDetail.path) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString(Route.PublicationDetail.ARG_POST_ID).orEmpty()
                    PublicationDetailScreen(
                        postId = postId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Route.CreatePublication.path) {
                    CreatePublicationScreen(
                        onPublished = { title, topic, description, photoUris ->
                            // Aquí puedes procesar los datos de la publicación
                            // Por ahora solo navegamos de vuelta
                            navController.popBackStack()
                        }
                    )
                }
                composable(Route.Profile.path) {
                    // Idealmente, el nombre y email vendrían de tu AuthViewModel o un nuevo ProfileViewModel
                    val userName = "Usuario Demo" // authViewModel.currentUser.name
                    val userEmail = "usuario@demo.com" // authViewModel.currentUser.email

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
                        }
                    )
                }
                composable(Route.EditProfile.path) {
                    // Idealmente estos valores vendrían del ViewModel/base de datos
                    EditProfileScreen(
                        currentName = "Usuario Demo",
                        currentPhone = "",
                        currentEmail = "usuario@demo.com",
                        currentGender = "",
                        currentPhotoUri = null,
                        onSaved = { name, phone, email, gender, photoUri ->
                            // TODO: Guardar los cambios en la base de datos/ViewModel
                            // Por ahora solo regresamos a la pantalla anterior
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
                    // Datos de ejemplo para el tema y sus publicaciones
                    val tema = when(topicId) {
                        "1" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "1",
                            nombre = "Rutinas de Fuerza",
                            descripcion = "Gimnasio moderno con equipamiento de última generación y personal capacitado para rutinas de fuerza y musculación.",
                            ubicacion = "Las Condes, Av. Principal 123",
                            numeroPublicaciones = 156
                        )
                        "2" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "2",
                            nombre = "Nutrición y Suplementos",
                            descripcion = "Centro especializado en nutrición deportiva y suplementación para optimizar tu rendimiento físico.",
                            ubicacion = "Providencia, Av. Libertador 456",
                            numeroPublicaciones = 89
                        )
                        "3" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "3",
                            nombre = "Cardio y Resistencia",
                            descripcion = "Instalaciones equipadas con las mejores máquinas cardiovasculares y programas de entrenamiento de resistencia.",
                            ubicacion = "Ñuñoa, Av. Irarrázaval 789",
                            numeroPublicaciones = 67
                        )
                        else -> com.example.qualifygym_grupo13.data.model.Tema(id = topicId, nombre = "Tema desconocido")
                    }

                    val publicaciones = listOf(
                        com.example.qualifygym_grupo13.data.model.Publicacion(
                            id = "101",
                            titulo = "¿Mejor rutina para pecho?",
                            autor = "user123",
                            contenido = "Llevo 3 meses entrenando pecho y no veo progreso significativo. ¿Alguien puede recomendarme una rutina efectiva?"
                        ),
                        com.example.qualifygym_grupo13.data.model.Publicacion(
                            id = "102",
                            titulo = "Opiniones sobre Creatina Monohidratada",
                            autor = "ana_fit",
                            contenido = "¿Realmente funciona la creatina? ¿Qué marcas recomiendan? He leído opiniones muy divididas."
                        )
                    )

                    TopicDetailScreen(
                        tema = tema,
                        publicaciones = publicaciones,
                        onBackClick = { navController.popBackStack() },
                        onPublicationClick = { postId -> openPost(postId) },
                        onCreatePublicationClick = { openWriteComment(topicId) }
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
                            numeroPublicaciones = 156
                        )
                        "2" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "2",
                            nombre = "Fitness Co",
                            descripcion = "Centro especializado en nutrición deportiva y suplementación para optimizar tu rendimiento físico.",
                            ubicacion = "Av. Libertador 456, Providencia",
                            numeroPublicaciones = 89
                        )
                        "3" -> com.example.qualifygym_grupo13.data.model.Tema(
                            id = "3",
                            nombre = "Cardio Center",
                            descripcion = "Instalaciones equipadas con las mejores máquinas cardiovasculares y programas de entrenamiento de resistencia.",
                            ubicacion = "Av. Irarrázaval 789, Ñuñoa",
                            numeroPublicaciones = 67
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
                        onPasswordChanged = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}