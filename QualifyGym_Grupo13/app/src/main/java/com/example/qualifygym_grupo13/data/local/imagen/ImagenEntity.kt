package com.example.qualifygym_grupo13.data.local.imagen

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.qualifygym_grupo13.data.local.estado.EstadoEntity
import com.example.qualifygym_grupo13.data.local.publicacion.PublicacionEntity

@Entity(
    tableName = "imagenes",
    foreignKeys = [
        ForeignKey(
            entity = PublicacionEntity::class,
            parentColumns = ["id_publicacion"],
            childColumns = ["Publicacion_id_publicacion"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EstadoEntity::class,
            parentColumns = ["id_estado"],
            childColumns = ["Estado_id_estado"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("Publicacion_id_publicacion"), Index("Estado_id_estado")]
)
data class ImagenEntity(
    @PrimaryKey(autoGenerate = true)
    val id_imagen: Long = 0,
    val nombre_imagen: String,
    val imagen: ByteArray, // BLOB para almacenar la imagen
    val Publicacion_id_publicacion: Long,
    val Estado_id_estado: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImagenEntity

        if (id_imagen != other.id_imagen) return false
        if (nombre_imagen != other.nombre_imagen) return false
        if (!imagen.contentEquals(other.imagen)) return false
        if (Publicacion_id_publicacion != other.Publicacion_id_publicacion) return false
        if (Estado_id_estado != other.Estado_id_estado) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id_imagen.hashCode()
        result = 31 * result + nombre_imagen.hashCode()
        result = 31 * result + imagen.contentHashCode()
        result = 31 * result + Publicacion_id_publicacion.hashCode()
        result = 31 * result + Estado_id_estado.hashCode()
        return result
    }
}

