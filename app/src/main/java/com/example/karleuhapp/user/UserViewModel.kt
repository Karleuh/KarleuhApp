package com.example.karleuhapp.user

import android.content.Context

import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karleuhapp.data.Api
import com.example.karleuhapp.data.Args
import com.example.karleuhapp.data.Command
import com.example.karleuhapp.data.Commands
import kotlinx.coroutines.launch

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import java.util.UUID

class UserViewModel : ViewModel() {

    private var webService = Api.userWebService
    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> get() = _navigateToLogin

    private val _authResult = MutableLiveData<AuthResult?>()
    val authResult: LiveData<AuthResult?> get() = _authResult

    lateinit var codeAuthFlowFactory: AndroidCodeAuthFlowFactory
    lateinit var oAuthClient: OpenIdConnectClient

    init {
        codeAuthFlowFactory = AndroidCodeAuthFlowFactory()
        oAuthClient = OpenIdConnectClient {
            endpoints {
                authorizationEndpoint = "https://todoist.com/oauth/authorize"
                tokenEndpoint = "https://todoist.com/oauth/access_token"
            }

            clientId = "a9400191c656400fb31a043e530c5522"
            clientSecret = "198815c02ac94f029a00338135a45123"
            scope = "task:add,data:read_write,data:delete"
            redirectUri = "https://cyrilfind.kodo/redirect_uri"
        }
    }

    fun checkTokenAndNavigate() {
        if (Api.TOKEN == null) {
            _navigateToLogin.value = true
        }
        else {
            _navigateToLogin.value = false
        }
    }

    fun login(activity: ComponentActivity) {
        viewModelScope.launch {

            try {

                val tokenResponse = codeAuthFlowFactory.createAuthFlow(oAuthClient).getAccessToken()
                Api.TOKEN = tokenResponse.access_token
                _authResult.value = AuthResult(success = true, accessToken = Api.TOKEN)

            } catch (e: Exception) {
                Log.e("LoginError", "Login failed: ${e.message}")
                _authResult.value = AuthResult(success = false, accessToken = null)
            }
        }
    }

    data class AuthResult(val success: Boolean, val accessToken: String?)

    fun updateAvatar(uri: Uri, context: Context) {
        val avatarPart = uri.toRequestBody(context)
        viewModelScope.launch {
            try {
                val response = webService.updateAvatar(avatarPart)
                if (!response.isSuccessful) {
                    Log.e("Network", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("Network", "Error updating avatar: ${e.message}")
            }
        }
    }

    fun updateName(newUsername: String) {
        viewModelScope.launch {
            try {
                val userUpdate = Command(
                    type = "user_update",
                    uuid = UUID.randomUUID().toString(),
                    args = Args(
                        username = newUsername
                    )
                )

                val commandsRequest = Commands(commands = listOf(userUpdate))

                val response = webService.updateName(commandsRequest)

                if (!response.isSuccessful) {
                    Log.e("Network", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("Network", "Error updating name: ${e.message}")
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