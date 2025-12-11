package ru.myitschool.work.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.myitschool.work.di.Dependencies

class AuthViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(Dependencies.checkAndSaveAuthCodeUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}