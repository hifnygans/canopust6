package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.RetroAchievementsService
import com.example.data.prefs.SessionManager
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var username by mutableStateOf("")
    var apiKey by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    fun onLoginClick() {
        if (username.isBlank() || apiKey.isBlank()) {
            error = "Please fill all fields"
            return
        }

        isLoading = true
        error = null
        viewModelScope.launch {
            authRepository.login(username, apiKey)
                .onSuccess {
                    loginSuccess = true
                }
                .onFailure {
                    error = it.message ?: "Login failed"
                }
            isLoading = false
        }
    }
}
