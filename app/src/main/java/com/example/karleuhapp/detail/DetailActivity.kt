package com.example.karleuhapp.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.karleuhapp.detail.ui.theme.KarleuhAppTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.karleuhapp.list.Task
import java.util.UUID

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KarleuhAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Detail(
                        modifier = Modifier.padding(innerPadding),
                        onValidate = { validatedTask ->

                            val intent = Intent().apply {
                                putExtra("task", validatedTask)
                            }
                            setResult(RESULT_OK, intent)
                            finish()

                        },
                        currentTask = intent.getSerializableExtra("task") as? Task
                    )
                }
            }
        }
    }
}

@Composable
fun Detail(
    modifier: Modifier = Modifier,
    onValidate: (Task) -> Unit,
    currentTask : Task?
) {

    val task = remember { mutableStateOf(currentTask ?: Task(id = UUID.randomUUID().toString(), title = "", description = "")) }

    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Task Detail",
            style = MaterialTheme.typography.headlineLarge,
        )

        OutlinedTextField(
            value = task.value.title,
            onValueChange = { task.value = task.value.copy(title = it) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(

            value = task.value.description ?: "",
            onValueChange = { task.value = task.value.copy(description = it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onValidate(task.value)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Validate")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    KarleuhAppTheme {
        Detail(
            onValidate = { validatedTask ->
                Log.d("DetailActivity", "Task validated: ${validatedTask.title}")
            },
            currentTask = Task(id = "", title = "", description = "")
        )
    }
}
