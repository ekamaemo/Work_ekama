package ru.myitschool.work.ui.booking

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import android.widget.Toast

import ru.myitschool.work.R
import ru.myitschool.work.data.*
import ru.myitschool.work.data.model.AvailablePlace
import ru.myitschool.work.data.BookingApiService
import java.time.LocalDate
import java.time.format.DateTimeFormatter


enum class BookingScreenState {
    LOADING,    // загрузка
    SUCCESS,    // успех при данных
    EMPTY,      // нет доступного
    ERROR
}

@Composable
fun BookingScreen(
    onBackClick: () -> Unit,
    userCode : String
) {
    var screenState by remember { mutableStateOf(BookingScreenState.LOADING) }

    var selectedDateIndex by remember { mutableStateOf(0) }
    var selectedPlaceIndex by remember { mutableIntStateOf(-1)}

    var availableDates by remember { mutableStateOf<List<BookingDate>>(emptyList()) }
    var availablePlaces by remember {mutableStateOf<List<Place>>(emptyList()) }

    // асинхронка
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val apiService = remember { BookingApiService() }

    // данные с сервера
    var bookingsData by remember { mutableStateOf<Map<String, List<AvailablePlace>>> (emptyMap())}
    var datesList by remember { mutableStateOf<List<String>>(emptyList())}
    var placesList by remember { mutableStateOf<List<AvailablePlace>>(emptyList())}

    // загрузка данных из сервера
    suspend fun loadDataFromServer() {
        screenState = BookingScreenState.LOADING

        try {
            val data = apiService.getAvailableBookings(userCode)
            bookingsData = data

            val dates = data.keys.sorted()
            datesList = dates

            if (dates.isEmpty()) {
                screenState = BookingScreenState.EMPTY
                return
            }

            val firstDate = dates.first()
            val places = data[firstDate] ?: emptyList()
            placesList = places

            selectedPlaceIndex = if (places.isNotEmpty()) 0 else -1
            screenState = BookingScreenState.SUCCESS
        }  catch (e:Exception) {
            screenState = BookingScreenState.ERROR
        }
    }

    // бронирование выбранного дня и места
    fun bookSelectedPlace() {
        if (selectedDateIndex in datesList.indices && selectedPlaceIndex in placesList.indices) {
            val selectedDate = datesList[selectedDateIndex]
            val selectedPlace = placesList[selectedPlaceIndex]

            coroutineScope.launch {
                val success = apiService.createBooking(
                    code = userCode,
                    date = selectedDate,
                    placeId = selectedPlace.id
                )
                if (success) {
                    Toast.makeText(
                        context,
                        "Забронировано: ${selectedPlace.place} на $selectedDate",
                        Toast.LENGTH_LONG
                    ).show()
                    loadDataFromServer()
                } else {
                    Toast.makeText(context, "Место нельзя забронировать", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // обновление мест под дату
    fun updatePlacesForDate(dateIndex: Int) {
        if (dateIndex in availableDates.indices) {
            val date = availableDates[dateIndex].date
            availablePlaces = FakeBookingRepository.getAvailablePlaces(date)
            selectedPlaceIndex = if (availablePlaces.isNotEmpty()) 0 else -1
        }
    }


    LaunchedEffect(Unit) {
        loadDataFromServer()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .testTag("book_back_button")
                    .padding(start = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_previous_button),
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        when (screenState) {
            BookingScreenState.EMPTY -> {
                EmptyView(
                    onReFresh = {
                        coroutineScope.launch {
                            loadDataFromServer()
                        }
                    }
                )
            }

            BookingScreenState.ERROR -> {
                ErrorView(
                    onReFresh = {
                        coroutineScope.launch {
                            loadDataFromServer()
                        }
                    }
                )
            }

            BookingScreenState.LOADING -> {
                LoadingView()
            }

            BookingScreenState.SUCCESS -> {
                SuccessView(
                    dates = datesList,
                    places = placesList,
                    selectedDateIndex = selectedDateIndex,
                    selectedPlaceIndex = selectedPlaceIndex,
                    onDateSelected = { index -> selectedDateIndex = index; updatePlacesForDate(index)},
                    onPlaceSelected = { index -> selectedPlaceIndex = index },
                    onBookClick = { bookSelectedPlace() },
                    isLoading = false
                )
            }
        }
    }
}

@Composable
fun SuccessView(
    dates: List<String>,
    places: List<AvailablePlace>,
    selectedDateIndex: Int,
    selectedPlaceIndex: Int,
    onDateSelected: (Int) -> Unit,
    onPlaceSelected: (Int) -> Unit,
    onBookClick: () -> Unit,
    isLoading: Boolean
) {
    val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val outputFormatter = DateTimeFormatter.ofPattern("dd.MM")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Выберите дату и место для бронирования",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Выберите дату:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        if (dates.isEmpty()) {
            Text(
                text = "Нет доступных дат",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dates.forEachIndexed { index, dateStr ->
                    val date = LocalDate.parse(dateStr, inputFormatter)
                    val displayDate = date.format(outputFormatter)
                    FilterChip(
                        selected = index == selectedDateIndex,
                        onClick = { onDateSelected(index) },
                        label = {
                            Text(
                                displayDate, modifier = Modifier.testTag("book_date")
                            )
                        },
                        modifier = Modifier.testTag("book_date_pos_$index")
                    )
                }
            }
        }
        Text(
            text = "Доступные места:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        if (places.isEmpty()) {
            Text(
                text = "Нет свободных мест",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                places.forEachIndexed { index, place ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("book_place_pos_$index"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = index == selectedPlaceIndex,
                            onClick = { onPlaceSelected(index) },
                            modifier = Modifier.testTag("book_place_selector")
                        )

                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = place.place,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.testTag("book_place_text")
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBookClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .testTag("book_book_button"),
            enabled = selectedPlaceIndex != -1 && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Забронировать",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(onReFresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ошибка при загрузке данных",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .testTag("book_error")
        )

        Button(
            onClick = onReFresh,
            modifier = Modifier.testTag("book_refresh_button")
        ) {
            Text("Обновить")
        }
    }
}

// Экран, когда все забронировано
@Composable
fun EmptyView(onReFresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Всё забронировано",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .testTag("book_empty")
        )

        Button(
            onClick = onReFresh,
            modifier = Modifier.testTag("book_refresh_button")
        ) {
            Text("Обновить")
        }
    }
}