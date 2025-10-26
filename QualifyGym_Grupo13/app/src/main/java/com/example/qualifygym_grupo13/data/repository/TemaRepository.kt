package com.example.qualifygym_grupo13.data.repository

import com.example.qualifygym_grupo13.data.local.tema.TemaDao
import com.example.qualifygym_grupo13.data.local.tema.TemaEntity
import kotlinx.coroutines.flow.Flow

class TemaRepository(private val temaDao: TemaDao) {
    
    fun getAllTemas(): Flow<List<TemaEntity>> = temaDao.getAll()
    
    suspend fun getTemaById(id: Long): TemaEntity? = temaDao.getById(id)
    
    fun searchTemas(query: String): Flow<List<TemaEntity>> = temaDao.searchByNombre(query)
    
    suspend fun insertTema(nombreTema: String, estadoId: Long): Long {
        val tema = TemaEntity(
            nombre_tema = nombreTema,
            Estado_id_estado = estadoId
        )
        return temaDao.insert(tema)
    }
    
    suspend fun updateTema(tema: TemaEntity) {
        temaDao.update(tema)
    }
}

