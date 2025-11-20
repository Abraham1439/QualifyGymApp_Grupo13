package com.example.qualifygym_grupo13.data.remote.dto

import com.example.qualifygym_grupo13.data.domain.ComentarioDomain
import com.example.qualifygym_grupo13.data.domain.PublicacionDomain
import com.example.qualifygym_grupo13.data.domain.TemaDomain
import java.text.SimpleDateFormat
import java.util.Locale

// Función helper para convertir PublicacionDto a PublicacionDomain
fun PublicacionDto.toPublicacionDomain(): PublicacionDomain {
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
    
    return PublicacionDomain(
        idPublicacion = idPublicacion,
        titulo = titulo,
        fecha = fechaTimestamp,
        descripcion = descripcion,
        oculta = oculta,
        fechaBaneo = fechaBaneoTimestamp,
        motivoBaneo = motivoBaneo,
        usuarioId = usuarioId,
        temaId = temaId,
        imageUrl = imageUrl
    )
}

// Función helper para convertir TemaDto a TemaDomain
fun TemaDto.toTemaDomain(): TemaDomain {
    return TemaDomain(
        idTema = idTema,
        nombreTema = nombreTema,
        estadoId = estadoId
    )
}

// Función helper para convertir ComentarioDto a ComentarioDomain
fun ComentarioDto.toComentarioDomain(): ComentarioDomain {
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
    
    return ComentarioDomain(
        idComentario = idComentario,
        comentario = comentario,
        fechaRegistro = fechaTimestamp,
        oculto = oculto,
        fechaBaneo = fechaBaneoTimestamp,
        motivoBaneo = motivoBaneo,
        usuarioId = usuarioId,
        publicacionId = publicacionId
    )
}

