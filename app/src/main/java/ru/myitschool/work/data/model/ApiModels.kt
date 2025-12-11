package ru.myitschool.work.data.model

import kotlinx.serialization.Serializable

// ответ на запрос /api/{code}/auth
@Serializable
data class AuthResponse(
    val success: Boolean? = null,
    val message: String? = null
)

// ответ на /api/{code}/info
@Serializable
data class UserInfoResponse(
    val name: String,
    val photoUrl: String,
    val booking: Map<String, BookingInfo>
)

@Serializable
data class BookingInfo(
    val id: Int,
    val place: String
)

typealias AvailableBookingsResponse = Map<String, List<AvailablePlace>>

@Serializable
data class AvailablePlace(
    val id: Int,
    val place: String
)

// запрос post для /api/{code}/book
@Serializable
data class CreateBookingRequest(
    val date: String,
    val placeId: Int
)

@Serializable
data class CreateBookingResponse(
    val success: Boolean,
    val bookingId: Int? = null,
    val error: String? = null
)

