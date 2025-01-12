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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
            val takePicture =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture())
                { success ->
                    if (success) uri = captureUri
                    val context: Context = applicationContext
                    uri?.let { viewModel.updateAvatar(it, context) }

                }

            //PhotoPicker
            val pickMedia =
                rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { selectedUri ->
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

            // Compose UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
                    model = uri,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    onClick = {
                        captureUri?.let { takePicture.launch(it) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Take picture", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bouton pour choisir une image dans la galerie
                Button(
                    onClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Pick photo", color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section pour modifier le nom d'utilisateur
                Text(
                    text = "Nom d'utilisateur :",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Champ de texte pour modifier le nom d'utilisateur
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Modifier votre nom") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),

                    )

                Spacer(modifier = Modifier.height(16.dp))

                // Bouton pour mettre à jour le nom d'utilisateur
                Button(
                    onClick = {
                        if (username.isNotEmpty()) {
                            viewModel.updateName(username)
                        }
                        finish()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Mettre à jour", color = Color.White)
                }
            }
        }

    }
}
