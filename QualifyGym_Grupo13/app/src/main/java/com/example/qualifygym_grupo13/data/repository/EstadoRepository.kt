package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.local.estado.EstadoDao
import com.example.qualifygym_grupo13.data.local.estado.EstadoEntity
import kotlinx.coroutines.flow.Flow

class EstadoRepository(private val estadoDao: EstadoDao) {
    
    fun getAllEstados(): Flow<List<EstadoEntity>> = estadoDao.getAll()
    
    suspend fun getEstadoById(id: Long): EstadoEntity? = estadoDao.getById(id)
    
    suspend fun getEstadoByNombre(nombre: String): EstadoEntity? = estadoDao.getByNombre(nombre)
    
    suspend fun insertEstado(nombre: String): Long {
        val estado = EstadoEntity(nombre = nombre)
        return estadoDao.insert(estado)
    }
    
    suspend fun getOrCreateEstado(nombre: String): Long {
        val existing = estadoDao.getByNombre(nombre)
        return existing?.id_estado ?: insertEstado(nombre)
    }
}

