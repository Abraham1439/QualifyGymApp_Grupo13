package com.example.qualifygym_grupo13.data.remote.dto

import com.example.qualifygym_grupo13.data.local.comentario.ComentarioEntity
import com.example.qualifygym_grupo13.data.local.publicacion.PublicacionEntity
import com.example.qualifygym_grupo13.data.local.tema.TemaEntity
import java.text.SimpleDateFormat
import java.util.Locale

// Función helper para convertir PublicacionDto a PublicacionEntity
fun PublicacionDto.toPublicacionEntity(): PublicacionEntity {
    // Convertir fecha de String "dd-MM-yyyy HH:mm" a timestamp
    val fechaTimestamp = try {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        sdf.parse(fecha)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
    
    val fechaBaneoTimestamp = fechaBaneo?.let {
        try {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            sdf.parse(it)?.time
        } catch (e: Exception) {
            null
        }
    }
    
    return PublicacionEntity(
        id_publicacion = idPublicacion,
        titulo = titulo,
        fecha = fechaTimestamp,
        descripcion = descripcion,
        oculta = oculta,
        fecha_baneo = fechaBaneoTimestamp,
        motivo_baneo = motivoBaneo,
        Usuarios_id_usuario = usuarioId,
        Tema_id_tema = temaId,
        imageUrl = imageUrl
    )
}

// Función helper para convertir TemaDto a TemaEntity
fun TemaDto.toTemaEntity(): TemaEntity {
    return TemaEntity(
        id_tema = idTema,
        nombre_tema = nombreTema,
        Estado_id_estado = estadoId
    )
}

// Función helper para convertir ComentarioDto a ComentarioEntity
fun ComentarioDto.toComentarioEntity(): ComentarioEntity {
    // Convertir fecha de String "dd-MM-yyyy HH:mm" a timestamp
    val fechaTimestamp = try {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        sdf.parse(fechaRegistro)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
    
    val fechaBaneoTimestamp = fechaBaneo?.let {
        try {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            sdf.parse(it)?.time
        } catch (e: Exception) {
            null
        }
    }
    
    return ComentarioEntity(
        id_comentario = idComentario,
        comentario = comentario,
        fecha_registro = fechaTimestamp,
        oculto = oculto,
        fecha_baneo = fechaBaneoTimestamp,
        motivo_baneo = motivoBaneo,
        Usuarios_id_usuario = usuarioId,
        Publicacion_id_publicacion = publicacionId
    )
}

