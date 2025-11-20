package com.example.qualifygym_grupo13.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qualifygym_grupo13.data.domain.NotificacionDomain
import com.example.qualifygym_grupo13.data.repository.NotificacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificacionViewModel(
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    private val _notificaciones = MutableStateFlow<List<NotificacionDomain>>(emptyList())
    val notificaciones: StateFlow<List<NotificacionDomain>> = _notificaciones.asStateFlow()

    private val _notificacionesNoLeidas = MutableStateFlow<List<NotificacionDomain>>(emptyList())
    val notificacionesNoLeidas: StateFlow<List<NotificacionDomain>> = _notificacionesNoLeidas.asStateFlow()

    private val _countNoLeidas = MutableStateFlow<Long>(0)
    val countNoLeidas: StateFlow<Long> = _countNoLeidas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Cargar todas las notificaciones de un usuario
    fun loadNotificaciones(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = notificacionRepository.obtenerNotificacionesPorUsuario(usuarioId)
            result.fold(
                onSuccess = { notificaciones ->
                    _notificaciones.value = notificaciones
                },
                onFailure = { error ->
                    _errorMessage.value = "Error al cargar notificaciones: ${error.message}"
                }
            )
            _isLoading.value = false
        }
    }

    // Cargar notificaciones no leídas de un usuario
    fun loadNotificacionesNoLeidas(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = notificacionRepository.obtenerNotificacionesNoLeidasPorUsuario(usuarioId)
            result.fold(
                onSuccess = { notificaciones ->
                    _notificacionesNoLeidas.value = notificaciones
                },
                onFailure = { error ->
                    _errorMessage.value = "Error al cargar notificaciones: ${error.message}"
                }
            )
            _isLoading.value = false
        }
    }

    // Actualizar el conteo de notificaciones no leídas
    fun updateCountNoLeidas(usuarioId: Long) {
        viewModelScope.launch {
            val result = notificacionRepository.contarNotificacionesNoLeidas(usuarioId)
            result.fold(
                onSuccess = { count ->
                    _countNoLeidas.value = count
                },
                onFailure = { error ->
                    _errorMessage.value = "Error al contar notificaciones: ${error.message}"
                }
            )
        }
    }

    // Marcar una notificación como leída
    fun marcarComoLeida(notificacionId: Long, usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = notificacionRepository.marcarComoLeida(notificacionId)
            result.fold(
                onSuccess = {
                    _successMessage.value = "Notificación marcada como leída"
                    // Recargar notificaciones
                    loadNotificaciones(usuarioId)
                    updateCountNoLeidas(usuarioId)
                },
                onFailure = { error ->
                    _errorMessage.value = "Error al marcar como leída: ${error.message}"
                }
            )
            _isLoading.value = false
        }
    }

    // Marcar todas las notificaciones como leídas
    fun marcarTodasComoLeidas(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = notificacionRepository.marcarTodasComoLeidas(usuarioId)
            result.fold(
                onSuccess = {
                    _successMessage.value = "Todas las notificaciones marcadas como leídas"
                    // Recargar notificaciones
                    loadNotificaciones(usuarioId)
                    updateCountNoLeidas(usuarioId)
                },
                onFailure = { error ->
                    _errorMessage.value = "Error al marcar como leídas: ${error.message}"
                }
            )
            _isLoading.value = false
        }
    }

    // Eliminar una notificación
    fun eliminarNotificacion(notificacionId: Long, usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = notificacionRepository.eliminarNotificacion(notificacionId)
            result.fold(
                onSuccess = {
                    _successMessage.value = "Notificación eliminada"
                    // Recargar notificaciones
                    loadNotificaciones(usuarioId)
                    updateCountNoLeidas(usuarioId)
                },
                onFailure = { error ->
                    _errorMessage.value = "Error al eliminar: ${error.message}"
                }
            )
            _isLoading.value = false
        }
    }

    // Limpiar mensajes
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}

