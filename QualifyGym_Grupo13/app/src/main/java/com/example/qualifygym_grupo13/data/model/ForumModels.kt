package com.example.qualifygym_grupo13.data.model

//====Nuevo ver si esta bn=====

// Representa una categoría del foro
data class Tema(
    val id: String, // el id lo ponemos como un String ya que para el backend es mas beneficioso
    val nombre: String,
    val descripcion: String = "",
    val ubicacion: String = "",
    val numeroPublicaciones: Int = 0
)

//Representa una publicacion dentro de un tema
data class Publicacion(
    val id: String,
    val titulo: String,
    val autor: String,
    val contenido: String
)