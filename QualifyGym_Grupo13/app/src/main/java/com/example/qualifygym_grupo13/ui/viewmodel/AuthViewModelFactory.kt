package com.example.qualifygym_grupo13.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qualifygym_grupo13.data.repository.UserRepository

// Factory simple para crear AuthViewModel con su UserRepository.
class AuthViewModelFactory(
    private val repository: UserRepository                       // Dependencia que inyectaremos
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si solicitan AuthViewModel, lo creamos con el repo.
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        //Si piden otra clase, lanzamos error descriptivo.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}