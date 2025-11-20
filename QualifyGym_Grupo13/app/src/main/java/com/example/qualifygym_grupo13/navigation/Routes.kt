package com.example.qualifygym_grupo13.navigation

// Clase sellada para rutas: evita "strings mágicos" y facilita refactors
sealed class Route(val path: String) { // Cada objeto representa una pantalla
    // Básicas / auth
    data object Splash   : Route("splash")      // Ruta Splash
    data object Home     : Route("home")        // Ruta Home
    data object Login    : Route("login")       // Ruta Login
    data object Register : Route("register")    // Ruta Registro
    data object Forgot   : Route("forgot")      // Recuperar contraseña
    data object ChangePassword : Route("changePassword") // Cambiar contraseña

    // Foro - navegación con argumentos en la ruta
    data object TopicDetail : Route("topicDetail/{topicId}") { // Detalle del tema
        fun create(topicId: String) = "topicDetail/$topicId"
        const val ARG_TOPIC_ID = "topicId"
    }
    data object PublicationsList : Route("publicationsList/{topicId}") { // Lista por tema
        fun create(topicId: String) = "publicationsList/$topicId"
        const val ARG_TOPIC_ID = "topicId"
    }
    data object PublicationDetail : Route("publicationDetail/{postId}") { // Detalle publicación
        fun create(postId: String) = "publicationDetail/$postId"
        const val ARG_POST_ID = "postId"
    }
    data object CreatePublication : Route("createPublication") // Crear publicación
    data object WriteComment : Route("writeComment/{topicId}") { // Escribir comentario
        fun create(topicId: String) = "writeComment/$topicId"
        const val ARG_TOPIC_ID = "topicId"
    }
    data object WriteCommentOnPost : Route("writeCommentOnPost/{postId}") { // Comentar en publicación
        fun create(postId: String) = "writeCommentOnPost/$postId"
        const val ARG_POST_ID = "postId"
    }

    // Perfil
    data object Profile : Route("profile")
    data object EditProfile : Route("editProfile")
    
    // Búsqueda
    data object Search : Route("search")

    // Admin
    data object AdminDashboard : Route("adminDashboard")
    data object ManagePublications : Route("managePublications")
    data object ManageUsers : Route("manageUsers")
    data object ManageThemes : Route("manageThemes")
    data object AdminStatistics : Route("adminStatistics")
    
    // Moderator
    data object ModeratorDashboard : Route("moderatorDashboard")
}

//NOTA:

/*
* “Strings mágicos” se refiere a cuando pones un texto duro y repetido en varias partes del código,
* Si mañana cambias "home" por "inicio", tendrías que buscar todas las ocurrencias de "home" a mano.
* Eso es frágil y propenso a errores.
La idea es: mejor centralizar esos strings en una sola clase (Route), y usarlos desde ahí.*/