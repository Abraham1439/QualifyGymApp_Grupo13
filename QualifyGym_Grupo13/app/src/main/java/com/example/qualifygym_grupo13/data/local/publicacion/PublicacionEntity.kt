package com.example.qualifygym_grupo13.data.local.publicacion

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.qualifygym_grupo13.data.local.tema.TemaEntity
import com.example.qualifygym_grupo13.data.local.user.UserEntity

@Entity(
    tableName = "publicaciones",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["Usuarios_id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TemaEntity::class,
            parentColumns = ["id_tema"],
            childColumns = ["Tema_id_tema"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("Usuarios_id_usuario"), Index("Tema_id_tema")]
)
data class PublicacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id_publicacion: Long = 0,
    val titulo: String,
    val fecha: Long, // Timestamp en milisegundos
    val descripcion: String,
    val fecha_baneo: Long? = null,
    val motivo_baneo: String? = null,
    val Usuarios_id_usuario: Long,
    val Tema_id_tema: Long
)

