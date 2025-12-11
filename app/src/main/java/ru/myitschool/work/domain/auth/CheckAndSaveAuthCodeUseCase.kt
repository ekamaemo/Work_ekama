package ru.myitschool.work.domain.auth

import ru.myitschool.work.data.repo.AuthRepository
import ru.myitschool.work.data.repo.AuthRepositoryImpl

class CheckAndSaveAuthCodeUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke (
        code: String
    ): Result<Unit> {
        // для валидности кода
        if (code.length != 4) {
            return Result.failure(IllegalArgumentException("Код должен содержать 4 символа"))
        }

        if (!code.all { it.isLetterOrDigit() }) {
            return Result.failure(IllegalArgumentException("Только латинские буквы и цифры"))
        }

        return repository.checkAndSaveAuthCode(code)
    }
}