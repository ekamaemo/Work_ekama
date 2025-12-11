// data/repo/AuthRepositoryImpl.kt
package ru.myitschool.work.data.repo

import ru.myitschool.work.data.local.AuthManager
import ru.myitschool.work.data.BookingApiService

class AuthRepositoryImpl(
    private val apiService: BookingApiService,
    private val authManager: AuthManager
) : AuthRepository {

    override suspend fun checkAndSaveAuthCode(code: String): Result<Unit> {
        return try {
            // Проверяем код через API (в нем уже тестовая логика)
            val isValid = apiService.checkAuth(code)
            if (!isValid) {
                return Result.failure(Exception("Неверный код"))
            }

            // Получаем информацию о пользователе
            val userInfo = apiService.getUserInfo(code)

            // Сохраняем данные авторизации
            authManager.saveAuthData(
                code = code,
                name = userInfo.name,
                photoUrl = userInfo.photoUrl
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isAuthenticated(): Boolean {
        return authManager.isAuthenticated()
    }

    override fun getSavedCode(): String? {
        return authManager.getUserCode()
    }

    override fun logout() {
        authManager.logout()
    }
}