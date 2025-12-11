// data/local/AuthManager.kt
package ru.myitschool.work.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AuthManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun isAuthenticated(): Boolean {
        return prefs.getString("user_code", null) != null
    }

    fun getUserCode(): String? {
        return prefs.getString("user_code", null)
    }

    fun getUserName(): String? {
        return prefs.getString("user_name", null)
    }

    fun getUserPhotoUrl(): String? {
        return prefs.getString("user_photo", null)
    }

    fun saveAuthData(code: String, name: String, photoUrl: String) {
        prefs.edit {
            putString("user_code", code)
            putString("user_name", name)
            putString("user_photo", photoUrl)
        }
    }

    fun logout() {
        prefs.edit {
            clear()
        }
    }
}