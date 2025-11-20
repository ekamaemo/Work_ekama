package ru.myitschool.work.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.myitschool.work.data.repo.AuthRepository
import ru.myitschool.work.domain.auth.CheckAndSaveAuthCodeUseCase

class AuthViewModel : ViewModel() {
    private val checkAndSaveAuthCodeUseCase by lazy { CheckAndSaveAuthCodeUseCase(AuthRepository) }
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Data)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _actionFlow: MutableSharedFlow<Unit> = MutableSharedFlow()
    val actionFlow: SharedFlow<Unit> = _actionFlow

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Send -> {
                viewModelScope.launch(Dispatchers.Default) {
                    _uiState.update { AuthState.Loading }
                    checkAndSaveAuthCodeUseCase.invoke("9999").fold(
                        onSuccess = {
                            _actionFlow.emit(Unit)
                        },
                        onFailure = { error ->
                            error.printStackTrace()
                            _actionFlow.emit(Unit)
                        }
                    )
                }
            }
            is AuthIntent.TextInput -> Unit
        }
    }
}