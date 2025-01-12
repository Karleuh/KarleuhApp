package com.example.karleuhapp.user

import android.graphics.Bitmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karleuhapp.data.Api
import com.example.karleuhapp.data.toRequestBody
import kotlinx.coroutines.launch
class UserViewModel : ViewModel() {
    private val webService = Api.userWebService
    fun updateAvatar(bitmap:Bitmap) {
        val avatarPart = bitmap.toRequestBody()
        viewModelScope.launch {
            webService.updateAvatar(avatarPart)
        }
    }
}