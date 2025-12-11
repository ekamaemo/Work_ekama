package ru.myitschool.work.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ru.myitschool.work.data.model.*
import ru.myitschool.work.data.AppHttpClient
import java.io.IOException
import java.time.LocalDate

class BookingApiService {
    private val client = AppHttpClient.client

    suspend fun checkAuth(code: String): Boolean {
        return try {
            val response: HttpResponse = client.get(AppHttpClient.buildUrl("api/$code/auth"))
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }

    // получить инфу о пользователе
    suspend fun getUserInfo(code: String): UserInfoResponse {
        return client.get(AppHttpClient.buildUrl("api/$code/info")).body()
    }

    // получить доступные места для брони
    suspend fun getAvailableBookings(code: String): AvailableBookingsResponse {
        return client.get(AppHttpClient.buildUrl("api/$code/booking")).body()
    }

    // создать бронь
    suspend fun createBooking(
        code: String,
        date: String,
        placeId: Int
    ): Boolean {
        return try {
            val request = CreateBookingRequest(date, placeId)

            val response: HttpResponse = client.post(
                AppHttpClient.buildUrl("api/$code/book")
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.status == HttpStatusCode.Created
        } catch (e: Exception) {
            false
        }
    }
}