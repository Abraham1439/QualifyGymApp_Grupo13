package com.example.qualifygym_grupo13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.qualifygym_grupo13.data.repository.ComentarioRepository
import com.example.qualifygym_grupo13.data.repository.NotificacionRepository
import com.example.qualifygym_grupo13.data.repository.PublicacionRepository
import com.example.qualifygym_grupo13.data.repository.TemaRepository
import com.example.qualifygym_grupo13.data.repository.UsuarioRepository
import com.example.qualifygym_grupo13.data.remote.RemoteModule
import com.example.qualifygym_grupo13.data.preferences.SessionManager
import com.example.qualifygym_grupo13.navigation.AppNavGraph
import com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel
import com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModelFactory
import com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModel
import com.example.qualifygym_grupo13.ui.viewmodel.PublicacionViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

/*ab
* En Compose, Surface es un contenedor visual que viene de Material 3.Crea un bloque
*  que puedes personalizar con color, forma, sombra (elevación).
Sirve para aplicar un fondo (color, borde, elevación, forma) siguiendo las guías de diseño
* de Material.
Piensa en él como una “lona base” sobre la cual vas a pintar tu UI.
* Si cambias el tema a dark mode, colorScheme.background
* cambia automáticamente y el Surface pinta la pantalla con el nuevo color.
* */

@Composable
fun AppRoot() { // Raíz de la app para separar responsabilidades (se conserva)
    // ====== NUEVO: construcción de dependencias (Composition Root) ======
    val context = LocalContext.current.applicationContext

    // NUEVO: Usamos UsuarioRepository que se conecta a las APIs
    val usuarioRepository = UsuarioRepository()
    // Repositorio que encapsula la lógica de login/registro contra APIs.

    val sessionManager = SessionManager(context)
    // Gestor de sesión simple con SharedPreferences.

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(usuarioRepository, sessionManager)
    )
    // Creamos el ViewModel con factory para inyectar las dependencias.

    // Creamos los repositorios remotos para publicaciones, temas y comentarios
    val publicacionRepository = PublicacionRepository()
    val temaRepository = TemaRepository()
    val comentarioRepository = ComentarioRepository()

    val publicacionViewModel: PublicacionViewModel = viewModel(
        factory = PublicacionViewModelFactory(
            publicacionRepository,
            temaRepository,
            comentarioRepository
        )
    )

    // Repositorio y ViewModel de notificaciones
    val notificacionRepository = NotificacionRepository(RemoteModule.publicacionApi)
    val notificacionViewModel: com.example.qualifygym_grupo13.ui.viewmodel.NotificacionViewModel = viewModel(
        factory = com.example.qualifygym_grupo13.ui.viewmodel.NotificacionViewModelFactory(notificacionRepository)
    )

    val navController = rememberNavController() // Controlador de navegación (igual que antes)
    MaterialTheme { // Provee colores/tipografías Material 3 (igual que antes)
        Surface(color = MaterialTheme.colorScheme.background) { // Fondo general (igual que antes)

            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                publicacionViewModel = publicacionViewModel,
                notificacionViewModel = notificacionViewModel
            )

        }
    }
}