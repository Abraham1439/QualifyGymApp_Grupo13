package com.example.qualifygym_grupo13.data.local.tema

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.qualifygym_grupo13.data.local.estado.EstadoEntity

@Entity(
    tableName = "temas",
    foreignKeys = [
        ForeignKey(
            entity = EstadoEntity::class,
            parentColumns = ["id_estado"],
            childColumns = ["Estado_id_estado"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("Estado_id_estado")]
)
data class TemaEntity(
    @PrimaryKey(autoGenerate = true)
    val id_tema: Long = 0,
    val nombre_tema: String,
    val Estado_id_estado: Long
)

