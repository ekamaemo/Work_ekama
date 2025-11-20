package ru.myitschool.work.data.repo

import ru.myitschool.work.data.source.NetworkDataSource

object AuthRepository {

    private var codeCache: String? = null

    suspend fun checkAndSave(text: String): Result<Boolean> {
        return NetworkDataSource.checkAuth(text).onSuccess { success ->
            if (success) {
                codeCache = text
            }
        }
    }
}