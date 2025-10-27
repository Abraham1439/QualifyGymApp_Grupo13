package com.example.qualifygym_grupo13.data.local.tema

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TemaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tema: TemaEntity): Long
    
    @Update
    suspend fun update(tema: TemaEntity)
    
    @Query("SELECT * FROM temas")
    fun getAll(): Flow<List<TemaEntity>>
    
    @Query("SELECT * FROM temas WHERE id_tema = :id LIMIT 1")
    suspend fun getById(id: Long): TemaEntity?
    
    @Query("SELECT * FROM temas WHERE nombre_tema LIKE '%' || :query || '%'")
    fun searchByNombre(query: String): Flow<List<TemaEntity>>
}

