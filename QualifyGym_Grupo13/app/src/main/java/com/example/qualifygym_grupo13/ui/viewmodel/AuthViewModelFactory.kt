package com.example.qualifygym_grupo13.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qualifygym_grupo13.data.repository.UserRepository
import com.example.qualifygym_grupo13.data.preferences.SessionManager

// Factory simple para crear AuthViewModel con sus dependencias.
class AuthViewModelFactory(
    private val repository: UserRepository,         // Dependencia UserRepository
    private val sessionManager: SessionManager      // Dependencia SessionManager
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si solicitan AuthViewModel, lo creamos con ambas dependencias.
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository, sessionManager) as T
        }
        //Si piden otra clase, lanzamos error descriptivo.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}