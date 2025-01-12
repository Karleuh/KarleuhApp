package com.example.karleuhapp.user

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope

import coil3.compose.AsyncImage
import com.example.karleuhapp.data.Api
import kotlinx.coroutines.launch

class UserActivity : ComponentActivity() {

    private val captureUri by lazy {
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var uri: Uri? by remember { mutableStateOf(null) }
            var username by remember { mutableStateOf("") }
            val viewModel: UserViewModel by viewModels()


            //TakePicture et reconversion to bitmap (sus)
            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture())
            { success ->
                if (success) uri = captureUri
                val context: Context = applicationContext
                uri?.let { viewModel.updateAvatar(it, context) }

            }

            //PhotoPicker
            val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { selectedUri ->
                if (selectedUri != null) {
                    uri = selectedUri
                    Log.d("PhotoPicker", "Selected URI: $selectedUri")
                    lifecycleScope.launch {
                        val context: Context = applicationContext
                        val user = Api.userWebService.fetchUser().body()!!
                        viewModel.updateAvatar(selectedUri, context)
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = uri,
                    contentDescription = null
                )
                Button(
                    onClick = {
                        captureUri?.let { takePicture.launch(it) }
                    },
                    content = { Text("Take picture") }
                )
                Button(
                    onClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

                    },
                    content = { Text("Pick photo") }
                )

                Text(text = "Nom d'utilisateur :")

                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Modifier votre nom") }
                )

                Button(onClick = {
                    if (username.isNotEmpty()) {
                        viewModel.updateName(username)
                    }
                }) {
                    Text("Mettre à jour")
                }

            }
        }

    }

}

