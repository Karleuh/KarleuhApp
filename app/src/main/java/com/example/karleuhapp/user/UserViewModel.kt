package com.example.karleuhapp.user

import android.content.Context

import android.net.Uri


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karleuhapp.data.Api
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserViewModel : ViewModel() {
    private val webService = Api.userWebService
    fun updateAvatar(uri: Uri, context: Context) {
        val avatarPart = uri.toRequestBody(context)
        viewModelScope.launch {
            webService.updateAvatar(avatarPart)
        }
    }
}

private fun android.net.Uri.toRequestBody(context: Context): MultipartBody.Part {
    val fileInputStream = context.contentResolver.openInputStream(this)!!
    val fileBody = fileInputStream.readBytes().toRequestBody()
    return MultipartBody.Part.createFormData(
        name = "avatar",
        filename = "avatar.jpg",
        body = fileBody
    )
}