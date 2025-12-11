package ru.myitschool.work.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.myitschool.work.domain.auth.CheckAndSaveAuthCodeUseCase

class AuthViewModel(
    private val checkAndSaveAuthCodeUseCase: CheckAndSaveAuthCodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Data(""))
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _actionFlow: MutableSharedFlow<AuthAction> = MutableSharedFlow()
    val actionFlow: SharedFlow<AuthAction> = _actionFlow

    // состояние для ошибки
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Send -> {
                sendCode(intent.text)
            }
            is AuthIntent.TextInput -> {
                _uiState.value = AuthState.Data(intent.text)
                // сброс ошибки при изменении текста
                if (_errorMessage.value != null) {
                    _errorMessage.value = null
                }
            }
        }
    }

    private fun sendCode(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = AuthState.Loading

            checkAndSaveAuthCodeUseCase(code).fold(
                onSuccess = {
                    // успешная авторизация
                    _actionFlow.emit(AuthAction.NavigateToMain)
                },
                onFailure = { error ->
                    // ошибка
                    _uiState.value = AuthState.Data(code)
                    _errorMessage.value = when {
                        error.message?.contains("сеть", ignoreCase = true) == true ->
                            "Ошибка сети. Проверьте подключение."
                        error.message?.contains("Неверный", ignoreCase = true) == true ->
                            "Неверный код авторизации"
                        else -> "Ошибка: ${error.message ?: "Неизвестная ошибка"}"
                    }
                }
            )
        }
    }
}

sealed class AuthAction {
    object NavigateToMain : AuthAction()
}