package ru.myitschool.work.data
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object AppHttpClient {
    private const val BASE_URL = "http://10.0.2.2:8080" // TODO

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // чтобы без лишних полей
                prettyPrint = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10000
        }

        expectSuccess = true
    }

    fun buildUrl(endpoint: String): String {
        return "$BASE_URL/$endpoint"
    }
}