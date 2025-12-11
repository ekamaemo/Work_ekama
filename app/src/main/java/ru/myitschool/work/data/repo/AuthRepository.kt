package ru.myitschool.work.data.repo

import ru.myitschool.work.data.source.NetworkDataSource

interface AuthRepository {
    suspend fun checkAndSaveAuthCode(code: String): Result<Unit>
    fun isAuthenticated(): Boolean
    fun getSavedCode(): String?
    fun logout()
}