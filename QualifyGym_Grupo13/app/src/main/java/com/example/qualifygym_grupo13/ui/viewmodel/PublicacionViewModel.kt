package com.example.qualifygym_grupo13.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qualifygym_grupo13.data.domain.ComentarioDomain
import com.example.qualifygym_grupo13.data.domain.PublicacionDomain
import com.example.qualifygym_grupo13.data.domain.TemaDomain
import com.example.qualifygym_grupo13.data.remote.dto.toComentarioDomain
import com.example.qualifygym_grupo13.data.remote.dto.toPublicacionDomain
import com.example.qualifygym_grupo13.data.remote.dto.toTemaDomain
import com.example.qualifygym_grupo13.data.repository.ComentarioRepository
import com.example.qualifygym_grupo13.data.repository.PublicacionRepository
import com.example.qualifygym_grupo13.data.repository.TemaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class PublicacionViewModel(
    private val publicacionRepository: PublicacionRepository,
    private val temaRepository: TemaRepository,
    private val comentarioRepository: ComentarioRepository
) : ViewModel() {

    // Lista de todas las publicaciones
    private val _allPublicaciones = MutableStateFlow<List<PublicacionDomain>>(emptyList())
    val allPublicaciones: StateFlow<List<PublicacionDomain>> = _allPublicaciones

    // Lista de todos los temas
    private val _allTemas = MutableStateFlow<List<TemaDomain>>(emptyList())
    val allTemas: StateFlow<List<TemaDomain>> = _allTemas
    
    init {
        loadAllPublicaciones()
        loadAllTemas()
    }
    
    private fun loadAllPublicaciones() {
        viewModelScope.launch {
            val result = publicacionRepository.fetchPublicaciones(incluirOcultas = false)
            result.onSuccess { dtos ->
                _allPublicaciones.value = dtos.map { it.toPublicacionDomain() }
            }.onFailure {
                _errorMessage.value = "Error al cargar publicaciones: ${it.message}"
            }
        }
    }
    
    // Cargar todas las publicaciones incluyendo las ocultas (para admin)
    fun loadAllPublicacionesIncluyendoOcultas() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = publicacionRepository.fetchPublicaciones(incluirOcultas = true)
            result.onSuccess { dtos ->
                _allPublicaciones.value = dtos.map { it.toPublicacionDomain() }
            }.onFailure {
                _errorMessage.value = "Error al cargar publicaciones: ${it.message}"
            }
            _isLoading.value = false
        }
    }
    
    private fun loadAllTemas() {
        viewModelScope.launch {
            val result = temaRepository.fetchTemas()
            result.onSuccess { dtos ->
                _allTemas.value = dtos.map { it.toTemaDomain() }
            }.onFailure {
                _errorMessage.value = "Error al cargar temas: ${it.message}"
            }
        }
    }

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
    private val _userPublicaciones = MutableStateFlow<List<PublicacionDomain>>(emptyList())
    val userPublicaciones: StateFlow<List<PublicacionDomain>> = _userPublicaciones

    // Mapa para mantener StateFlows de comentarios por publicaciónId
    private val _comentariosFlows = ConcurrentHashMap<Long, MutableStateFlow<List<ComentarioDomain>>>()

    // Crear una nueva publicación
    suspend fun createPublicacion(
        titulo: String,
        descripcion: String,
        userId: Long,
        temaId: Long,
        imageUrl: String? = null
    ): Result<Long> {
        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null

        val createDto = com.example.qualifygym_grupo13.data.remote.dto.PublicacionCreateDto(
            titulo = titulo,
            descripcion = descripcion,
            usuarioId = userId,
            temaId = temaId,
            imageUrl = imageUrl
        )

        val result = publicacionRepository.create(createDto)
        
        _isLoading.value = false

        return result.fold(
            onSuccess = { dto ->
                _successMessage.value = "Publicación creada exitosamente"
                loadAllPublicaciones() // Recargar lista
                Result.success(dto.idPublicacion)
            },
            onFailure = { error ->
                _errorMessage.value = error.message ?: "Error al crear la publicación"
                Result.failure(error)
            }
        )
    }
    
    // Actualizar la imagen de una publicación
    suspend fun updatePublicacionImage(publicacionId: Long, imageUrl: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = publicacionRepository.updateImagen(publicacionId, imageUrl ?: "")
            result.onSuccess {
                loadAllPublicaciones() // Recargar lista
            }.onFailure {
                _errorMessage.value = "Error al actualizar imagen: ${it.message}"
            }
            _isLoading.value = false
        }
    }

    // Obtener publicaciones de un usuario específico
    fun loadUserPublicaciones(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = publicacionRepository.fetchPublicacionesPorUsuario(userId)
            result.onSuccess { dtos ->
                _userPublicaciones.value = dtos.map { it.toPublicacionDomain() }
            }.onFailure {
                _errorMessage.value = "Error al cargar publicaciones: ${it.message}"
            }
            _isLoading.value = false
        }
    }

    // Buscar publicaciones por query
    fun searchPublicaciones(query: String): StateFlow<List<PublicacionDomain>> {
        val flow = MutableStateFlow<List<PublicacionDomain>>(emptyList())
        viewModelScope.launch {
            val result = publicacionRepository.buscarPublicaciones(query)
            result.onSuccess { dtos ->
                flow.value = dtos.map { it.toPublicacionDomain() }
            }
        }
        return flow
    }

    // Obtener publicaciones por tema
    fun getPublicacionesByTema(temaId: Long): StateFlow<List<PublicacionDomain>> {
        val flow = MutableStateFlow<List<PublicacionDomain>>(emptyList())
        viewModelScope.launch {
            val result = publicacionRepository.fetchPublicacionesPorTema(temaId)
            result.onSuccess { dtos ->
                flow.value = dtos.map { it.toPublicacionDomain() }
            }
        }
        return flow
    }

    // Obtener una publicación específica por ID
    suspend fun getPublicacionById(id: Long): PublicacionDomain? {
        val result = publicacionRepository.fetchPublicacionById(id)
        return result.getOrNull()?.toPublicacionDomain()
    }

    // Obtener un tema específico por ID
    suspend fun getTemaById(id: Long): TemaDomain? {
        val result = temaRepository.fetchTemaById(id)
        return result.getOrNull()?.toTemaDomain()
    }

    // Eliminar una publicación
    suspend fun deletePublicacion(id: Long) {
        _isLoading.value = true
        val result = publicacionRepository.delete(id)
        result.onSuccess {
            _successMessage.value = "Publicación eliminada"
            loadAllPublicaciones() // Recargar lista
        }.onFailure {
            _errorMessage.value = "Error al eliminar: ${it.message}"
        }
        _isLoading.value = false
    }

    // Crear un comentario
    suspend fun createComentario(
        comentario: String,
        userId: Long,
        publicacionId: Long
    ): Result<Long> {
        val createDto = com.example.qualifygym_grupo13.data.remote.dto.ComentarioCreateDto(
            comentario = comentario,
            usuarioId = userId,
            publicacionId = publicacionId
        )
        
        val result = comentarioRepository.create(createDto)
        return result.fold(
            onSuccess = { dto -> 
                // Recargar comentarios de esta publicación después de crear uno nuevo
                reloadComentariosForPublicacion(publicacionId)
                Result.success(dto.idComentario) 
            },
            onFailure = { error -> Result.failure(error) }
        )
    }

    // Obtener comentarios de una publicación específica
    // Nota: El parámetro incluirOcultos se usa para la carga inicial, pero el StateFlow siempre
    // contendrá todos los comentarios. El filtrado se hace en la UI según el rol del usuario.
    fun getComentariosByPublicacionId(publicacionId: Long, incluirOcultos: Boolean = false): StateFlow<List<ComentarioDomain>> {
        // Obtener o crear el StateFlow para esta publicación
        val flow = _comentariosFlows.getOrPut(publicacionId) {
            MutableStateFlow<List<ComentarioDomain>>(emptyList())
        }
        
        // Cargar comentarios si el StateFlow está vacío o si necesitamos refrescar
        // Si el StateFlow ya tiene datos, no recargamos para evitar sobrecarga
        // Si está vacío, cargamos con incluirOcultos según el parámetro
        if (flow.value.isEmpty()) {
            viewModelScope.launch {
                val result = comentarioRepository.fetchComentariosPorPublicacion(publicacionId, incluirOcultos)
                result.onSuccess { dtos ->
                    flow.value = dtos.map { it.toComentarioDomain() }
                }
            }
        }
        
        return flow
    }
    
    // Recargar comentarios para una publicación específica
    // Siempre carga con incluirOcultos=true para que el StateFlow tenga todos los comentarios
    // El filtrado se hace en la UI según el rol del usuario
    private fun reloadComentariosForPublicacion(publicacionId: Long) {
        val flow = _comentariosFlows[publicacionId]
        if (flow != null) {
            viewModelScope.launch {
                // Siempre cargar con incluirOcultos=true para tener todos los comentarios
                // El filtrado se hace en la UI
                val result = comentarioRepository.fetchComentariosPorPublicacion(publicacionId, incluirOcultos = true)
                result.onSuccess { dtos ->
                    flow.value = dtos.map { it.toComentarioDomain() }
                }
            }
        }
    }
    
    // Ocultar un comentario
    suspend fun ocultarComentario(id: Long, motivoBaneo: String, publicacionId: Long): Result<Unit> {
        _isLoading.value = true
        val result = comentarioRepository.ocultar(id, motivoBaneo)
        val finalResult = result.fold(
            onSuccess = {
                _successMessage.value = "Comentario ocultado"
                // Recargar comentarios de la publicación
                reloadComentariosForPublicacion(publicacionId)
                Result.success(Unit)
            },
            onFailure = { error ->
                _errorMessage.value = "Error al ocultar comentario: ${error.message}"
                Result.failure(error)
            }
        )
        _isLoading.value = false
        return finalResult
    }
    
    // Mostrar un comentario (desocultar)
    suspend fun mostrarComentario(id: Long, publicacionId: Long): Result<Unit> {
        _isLoading.value = true
        val result = comentarioRepository.mostrar(id)
        val finalResult = result.fold(
            onSuccess = {
                _successMessage.value = "Comentario desocultado"
                // Recargar comentarios de la publicación
                reloadComentariosForPublicacion(publicacionId)
                Result.success(Unit)
            },
            onFailure = { error ->
                _errorMessage.value = "Error al mostrar comentario: ${error.message}"
                Result.failure(error)
            }
        )
        _isLoading.value = false
        return finalResult
    }
    
    // Eliminar un comentario
    suspend fun deleteComentario(id: Long, publicacionId: Long): Result<Unit> {
        _isLoading.value = true
        val result = comentarioRepository.delete(id)
        val finalResult = result.fold(
            onSuccess = {
                _successMessage.value = "Comentario eliminado"
                // Recargar comentarios de la publicación
                reloadComentariosForPublicacion(publicacionId)
                Result.success(Unit)
            },
            onFailure = { error ->
                _errorMessage.value = "Error al eliminar comentario: ${error.message}"
                Result.failure(error)
            }
        )
        _isLoading.value = false
        return finalResult
    }

    // Ocultar una publicación
    suspend fun ocultarPublicacion(id: Long, motivoBaneo: String, incluirOcultas: Boolean = false): Result<Unit> {
        _isLoading.value = true
        val result = publicacionRepository.ocultar(id, motivoBaneo)
        val finalResult = result.fold(
            onSuccess = {
                _successMessage.value = "Publicación ocultada"
                // Recargar lista con el mismo filtro que se estaba usando
                if (incluirOcultas) {
                    loadAllPublicacionesIncluyendoOcultas()
                } else {
                    loadAllPublicaciones()
                }
                Result.success(Unit)
            },
            onFailure = { error ->
                _errorMessage.value = "Error al ocultar: ${error.message}"
                Result.failure(error)
            }
        )
        _isLoading.value = false
        return finalResult
    }

    // Mostrar una publicación (desocultar)
    suspend fun mostrarPublicacion(id: Long, incluirOcultas: Boolean = false): Result<Unit> {
        _isLoading.value = true
        val result = publicacionRepository.mostrar(id)
        val finalResult = result.fold(
            onSuccess = {
                _successMessage.value = "Publicación mostrada"
                // Recargar lista con el mismo filtro que se estaba usando
                if (incluirOcultas) {
                    loadAllPublicacionesIncluyendoOcultas()
                } else {
                    loadAllPublicaciones()
                }
                Result.success(Unit)
            },
            onFailure = { error ->
                _errorMessage.value = "Error al mostrar: ${error.message}"
                Result.failure(error)
            }
        )
        _isLoading.value = false
        return finalResult
    }

    // Limpiar mensajes
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}

