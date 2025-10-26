package com.example.qualifygym_grupo13.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qualifygym_grupo13.data.repository.ComentarioRepository
import com.example.qualifygym_grupo13.data.repository.PublicacionRepository
import com.example.qualifygym_grupo13.data.repository.TemaRepository

class PublicacionViewModelFactory(
    private val publicacionRepository: PublicacionRepository,
    private val temaRepository: TemaRepository,
    private val comentarioRepository: ComentarioRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublicacionViewModel::class.java)) {
            return PublicacionViewModel(
                publicacionRepository,
                temaRepository,
                comentarioRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

