package ru.myitschool.work.ui.screen.auth

sealed interface AuthState {
    object Loading: AuthState
    data class Data(val inputText : String): AuthState
    data class Error(val message: String) : AuthState
}