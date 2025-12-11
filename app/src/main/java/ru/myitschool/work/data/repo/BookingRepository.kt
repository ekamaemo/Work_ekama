// data/repo/BookingRepository.kt
package ru.myitschool.work.data.repo

import ru.myitschool.work.data.BookingApiService
import ru.myitschool.work.data.model.AvailableBookingsResponse
import ru.myitschool.work.data.model.CreateBookingRequest
import ru.myitschool.work.data.model.CreateBookingResponse

class BookingRepository(
    private val apiService: BookingApiService
) {
    suspend fun getAvailablePlaces(code: String): Result<AvailableBookingsResponse> {
        return try {
            val response = apiService.getAvailableBookings(code)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bookPlace(code: String, date: String, placeId: Int): Result<Int> {
        return try {
            val success = apiService.createBooking(code, date, placeId)

            if (success) {
                Result.success(placeId)
            } else {
                Result.failure(Exception("Не удалось забронировать"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}