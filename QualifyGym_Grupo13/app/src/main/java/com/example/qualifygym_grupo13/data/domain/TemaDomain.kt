package com.example.qualifygym_grupo13.data.domain

// Modelo de dominio para Tema (sin Room)
data class TemaDomain(
    val idTema: Long = 0,
    val nombreTema: String,
    val estadoId: Long
)

