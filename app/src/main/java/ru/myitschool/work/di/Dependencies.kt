// di/Dependencies.kt
package ru.myitschool.work.di

import android.content.Context
import ru.myitschool.work.data.BookingApiService
import ru.myitschool.work.data.local.AuthManager
import ru.myitschool.work.data.repo.AuthRepository
import ru.myitschool.work.data.repo.AuthRepositoryImpl
import ru.myitschool.work.data.repo.BookingRepository
import ru.myitschool.work.domain.auth.CheckAndSaveAuthCodeUseCase

object Dependencies {
    lateinit var appContext: Context
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val authManager: AuthManager by lazy {
        AuthManager(appContext)
    }

    val bookingApiService: BookingApiService by lazy {
        BookingApiService()
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            apiService = bookingApiService,
            authManager = authManager
        )
    }

    val bookingRepository: BookingRepository by lazy {
        BookingRepository(bookingApiService)
    }

    val checkAndSaveAuthCodeUseCase: CheckAndSaveAuthCodeUseCase by lazy {
        CheckAndSaveAuthCodeUseCase(authRepository)
    }
}