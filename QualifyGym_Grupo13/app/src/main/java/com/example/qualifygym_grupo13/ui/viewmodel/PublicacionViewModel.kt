package com.example.qualifygym_grupo13.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qualifygym_grupo13.data.local.publicacion.PublicacionEntity
import com.example.qualifygym_grupo13.data.local.tema.TemaEntity
import com.example.qualifygym_grupo13.data.repository.ComentarioRepository
import com.example.qualifygym_grupo13.data.repository.PublicacionRepository
import com.example.qualifygym_grupo13.data.repository.TemaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PublicacionViewModel(
    private val publicacionRepository: PublicacionRepository,
    private val temaRepository: TemaRepository,
    private val comentarioRepository: ComentarioRepository
) : ViewModel() {

    // Lista de todas las publicaciones
    val allPublicaciones: StateFlow<List<PublicacionEntity>> = 
        publicacionRepository.getAllPublicaciones()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Lista de todos los temas
    val allTemas: StateFlow<List<TemaEntity>> = 
        temaRepository.getAllTemas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Mensaje de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Mensaje de éxito
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    // Publicaciones del usuario actual
    private val _userPublicaciones = MutableStateFlow<List<PublicacionEntity>>(emptyList())
    val userPublicaciones: StateFlow<List<PublicacionEntity>> = _userPublicaciones

    // Crear una nueva publicación
    suspend fun createPublicacion(
        titulo: String,
        descripcion: String,
        userId: Long,
        temaId: Long
    ): Result<Long> {
        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null

        val result = publicacionRepository.insertPublicacion(
            titulo = titulo,
            descripcion = descripcion,
            userId = userId,
            temaId = temaId
        )

        _isLoading.value = false

        if (result.isSuccess) {
            _successMessage.value = "Publicación creada exitosamente"
        } else {
            _errorMessage.value = result.exceptionOrNull()?.message ?: "Error al crear la publicación"
        }

        return result
    }

    // Obtener publicaciones de un usuario específico
    fun loadUserPublicaciones(userId: Long) {
        viewModelScope.launch {
            publicacionRepository.getPublicacionesByUserId(userId)
                .collect { publicaciones ->
                    _userPublicaciones.value = publicaciones
                }
        }
    }

    // Buscar publicaciones por query
    fun searchPublicaciones(query: String): StateFlow<List<PublicacionEntity>> {
        return publicacionRepository.searchPublicaciones(query)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // Obtener publicaciones por tema
    fun getPublicacionesByTema(temaId: Long): StateFlow<List<PublicacionEntity>> {
        return publicacionRepository.getPublicacionesByTemaId(temaId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // Obtener una publicación específica por ID
    suspend fun getPublicacionById(id: Long): PublicacionEntity? {
        return publicacionRepository.getPublicacionById(id)
    }

    // Obtener un tema específico por ID
    suspend fun getTemaById(id: Long): TemaEntity? {
        return temaRepository.getTemaById(id)
    }

    // Eliminar una publicación
    suspend fun deletePublicacion(id: Long) {
        _isLoading.value = true
        try {
            publicacionRepository.deletePublicacion(id)
            _successMessage.value = "Publicación eliminada"
        } catch (e: Exception) {
            _errorMessage.value = "Error al eliminar: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    // Crear un comentario
    suspend fun createComentario(
        comentario: String,
        userId: Long,
        publicacionId: Long
    ): Result<Long> {
        return comentarioRepository.insertComentario(
            comentario = comentario,
            userId = userId,
            publicacionId = publicacionId
        )
    }

    // Obtener comentarios de una publicación específica
    fun getComentariosByPublicacionId(publicacionId: Long) = 
        comentarioRepository.getComentariosByPublicacionId(publicacionId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Limpiar mensajes
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}

