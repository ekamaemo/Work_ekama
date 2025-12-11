package ru.myitschool.work.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

// дата брони с доступными местами
data class BookingDate(
    val date: LocalDate,
    val availablePlaces: List<Place>
)

// место
data class Place(
    val id: Int,
    val name: String,
    val description: String = ""
)

// бронирование
data class Booking(
    val id: Int,
    val date: LocalDate,
    val place: Place,
    val userId: String
)

object FakeBookingRepository {

    // Список всех дат с местами
    private val allDates = mutableListOf<BookingDate>()

    // Список бронирований
    private val bookings = mutableListOf<Booking>()

    // Инициализация тестовыми данными
    init {
        initializeTestData()
    }

    private fun initializeTestData() {
        // Создаем даты на ближайшие 7 дней
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM")

        // Названия мест для разных дат
        val placeNamesByDay = mapOf(
            0 to listOf("Место 1 (окно)", "Место 2 (центр)", "Место 3 (кулер)"),
            1 to listOf("Место 4 (тихая зона)", "Место 5 (розетка)"),
            2 to listOf("Место 1 (окно)", "Место 6 (премиум)"),
            3 to listOf("Место 2 (центр)", "Место 7 (рядом с лифтом)"),
            4 to listOf("Место 8 (угловое)"),
            5 to listOf("Место 9 (с видом)"),
            6 to listOf("Нет мест") // Для теста пустого дня
        )

        for (i in 0..6) {
            val date = today.plusDays(i.toLong())
            val dayIndex = i % 7

            // Берем места для этого дня, если есть, иначе пустой список
            val placeNames = placeNamesByDay[dayIndex] ?: emptyList()

            // Создаем места для этой даты
            val places = placeNames.mapIndexed { placeIndex, name ->
                Place(
                    id = (dayIndex * 10) + placeIndex + 1, // Уникальный ID
                    name = name,
                    description = when {
                        name.contains("окно") -> "С естественным освещением"
                        name.contains("премиум") -> "Просторное место"
                        name.contains("кулер") -> "Рядом с водой"
                        name.contains("розетка") -> "Много розеток"
                        else -> "Стандартное место"
                    }
                )
            }

            allDates.add(BookingDate(date, places))

            // ЗАБРОНИРУЕМ НЕКОТОРЫЕ МЕСТА ДЛЯ ТЕСТА
            // Бронируем часть мест для первых трех дней
            if (i < 3 && places.isNotEmpty()) {
                // Бронируем первое место в каждом из первых трех дней
                bookings.add(
                    Booking(
                        id = bookings.size + 1,
                        date = date,
                        place = places.first(),
                        userId = "user1"
                    )
                )
            }
        }
    }

    // Получить доступные даты
    fun getAvailableDates(): List<BookingDate> {
        return allDates.filter { date ->
            // Фильтруем даты, у которых есть свободные места
            date.availablePlaces.any { place ->
                !isPlaceBooked(date.date, place.id)
            }
        }
    }

    // Получить доступные места для даты
    fun getAvailablePlaces(date: LocalDate): List<Place> {
        return allDates
            .firstOrNull { it.date == date }
            ?.availablePlaces
            ?.filter { place -> !isPlaceBooked(date, place.id) }
            ?: emptyList()
    }

    // Забронировать место
    fun bookPlace(date: LocalDate, placeId: Int, userId: String = "test_user"): Boolean {
        val place = allDates
            .firstOrNull { it.date == date }
            ?.availablePlaces
            ?.firstOrNull { it.id == placeId }
            ?: return false

        // Проверяем, свободно ли место
        if (isPlaceBooked(date, placeId)) {
            return false
        }

        // Создаем бронирование
        val booking = Booking(
            id = bookings.size + 1,
            date = date,
            place = place,
            userId = userId
        )

        bookings.add(booking)
        return true
    }

    // Проверить, забронировано ли место
    private fun isPlaceBooked(date: LocalDate, placeId: Int): Boolean {
        return bookings.any { it.date == date && it.place.id == placeId }
    }

    // Получить все бронирования пользователя
    fun getUserBookings(userId: String = "test_user"): List<Booking> {
        return bookings.filter { it.userId == userId }
    }

    // Очистить все бронирования (для теста)
    fun clearAllBookings() {
        bookings.clear()
    }
}