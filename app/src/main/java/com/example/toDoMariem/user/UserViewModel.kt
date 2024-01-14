package com.example.toDoMariem.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    val userStateFlow = MutableStateFlow<User?>(null)
    private val userWebService = Api.userWebService
    fun fetchUser() {
        viewModelScope.launch {
            try {
                val response = userWebService.fetchUser()
                if (response.isSuccessful) {
                    userStateFlow.value = response.body()
                } else {
                    Log.e("UserViewModel", "Error fetching user: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception when fetching user", e)
            }
        }
    }


    fun updateUserName(newName: String) {
        viewModelScope.launch {
            val userUpdate = UserUpdate(newName)
                val response = userWebService.update(userUpdate)
                if (response.isSuccessful) {
                    fetchUser() // Re-fetch user data to get updated info
                } else {
                    Log.e("UserViewModel", "Error updating user: ${response.errorBody()}")
                }
        }
    }
}
