package com.example.qualifygym_grupo13.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

//clase para definir los ítems de la barra de navegación
// Clase sellada para definir cada item de la barra de navegación
sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    // Definimos las tres pantallas principales de la barra
    object Home : BottomNavItem("Inicio", Icons.Default.Home, "home_bottom")
    object Search : BottomNavItem("Buscar", Icons.Default.Search, "search_bottom")
    object Profile : BottomNavItem("Perfil", Icons.Default.Person, "profile_bottom")

}