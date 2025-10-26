package com.example.qualifygym_grupo13.data.local.comentario

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.qualifygym_grupo13.data.local.publicacion.PublicacionEntity
import com.example.qualifygym_grupo13.data.local.user.UserEntity

@Entity(
    tableName = "comentarios",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["Usuarios_id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PublicacionEntity::class,
            parentColumns = ["id_publicacion"],
            childColumns = ["Publicacion_id_publicacion"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("Usuarios_id_usuario"), Index("Publicacion_id_publicacion")]
)
data class ComentarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id_comentario: Long = 0,
    val comentario: String,
    val fecha_registro: Long, // Timestamp en milisegundos
    val fecha_baneo: Long? = null,
    val motivo_baneo: String? = null,
    val Usuarios_id_usuario: Long,
    val Publicacion_id_publicacion: Long
)

