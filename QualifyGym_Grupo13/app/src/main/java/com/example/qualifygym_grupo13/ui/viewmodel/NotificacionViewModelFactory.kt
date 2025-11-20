package com.example.qualifygym_grupo13.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qualifygym_grupo13.data.repository.NotificacionRepository

class NotificacionViewModelFactory(
    private val notificacionRepository: NotificacionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificacionViewModel(notificacionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

