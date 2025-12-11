package ru.myitschool.work.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.myitschool.work.data.local.AuthManager
import ru.myitschool.work.ui.booking.BookingScreen
import ru.myitschool.work.ui.screen.auth.AuthScreen
import ru.myitschool.work.ui.screen.main.MainScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }

    // Определяем маршруты
    val authRoute = "auth"
    val mainRoute = "main"
    val bookingRoute = "booking"

    // Проверяем, авторизован ли пользователь
    val startDestination = if (authManager.isAuthenticated()) {
        mainRoute
    } else {
        authRoute
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        // Экран авторизации
        composable(authRoute) {
            AuthScreen(
                navController = navController
            )
        }

        // Главный экран
        composable(mainRoute) {
            MainScreen(
                onBookingClick = {
                    navController.navigate(bookingRoute)
                },
                onLogoutClick = {
                    // Выход из аккаунта
                    authManager.logout()
                    navController.navigate(authRoute) {
                        popUpTo(mainRoute)
                    }
                }
            )
        }

        // Экран бронирования
        composable(bookingRoute) {
            val userCode = authManager.getUserCode() ?: ""

            BookingScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                userCode = userCode
            )
        }
    }
}