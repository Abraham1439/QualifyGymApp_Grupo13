package com.example.qualifygym_grupo13.data.local.estado

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estados")
data class EstadoEntity(
    @PrimaryKey(autoGenerate = true)
    val id_estado: Long = 0,
    val nombre: String
)

