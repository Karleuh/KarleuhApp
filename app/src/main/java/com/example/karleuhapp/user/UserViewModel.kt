package com.example.karleuhapp.user

import android.content.Context

import android.net.Uri
import android.util.Log


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karleuhapp.data.Api
import com.example.karleuhapp.data.Api.userWebService
import com.example.karleuhapp.data.Args
import com.example.karleuhapp.data.Command
import com.example.karleuhapp.data.Commands
import kotlinx.coroutines.launch

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

class UserViewModel : ViewModel() {
    private val webService = Api.userWebService
    fun updateAvatar(uri: Uri, context: Context) {
        val avatarPart = uri.toRequestBody(context)
        viewModelScope.launch {
            webService.updateAvatar(avatarPart)
        }
    }

    fun updateName(newUsername: String) {
        viewModelScope.launch {

            val userUpdate = Command(
                type = "user_update",
                uuid = UUID.randomUUID().toString(),
                args = Args(
                    username = newUsername
                )
            )

            val commandsRequest = Commands(commands = listOf(userUpdate))


            val response = userWebService.updateName(commandsRequest)

            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.message()}")
                return@launch
            }
        }
    }
}

private fun Uri.toRequestBody(context: Context): MultipartBody.Part {
    val fileInputStream = context.contentResolver.openInputStream(this)!!
    val fileBody = fileInputStream.readBytes().toRequestBody()
    return MultipartBody.Part.createFormData(
        name = "avatar",
        filename = "avatar.jpg",
        body = fileBody
    )
}