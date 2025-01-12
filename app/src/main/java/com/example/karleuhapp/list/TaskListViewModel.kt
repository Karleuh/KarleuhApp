package com.example.karleuhapp.list


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karleuhapp.data.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {
    private val webService = Api.tasksWebService

    public val tasksStateFlow = MutableStateFlow<List<Task>>(emptyList())

    fun refresh() {
        viewModelScope.launch {
            val response = webService.fetchTasks() // Call HTTP (opération longue)
            if (!response.isSuccessful) { // à cette ligne, on a reçu la réponse de l'API
                Log.e("Network", "Error: ${response.message()}")
                return@launch
            }
            val fetchedTasks = response.body()!!
            tasksStateFlow.value = fetchedTasks // on modifie le flow, ce qui déclenche ses observers
        }
    }

    // Pas sûr de l'utilité des refresh mais ça semble aider (d'un pdv ressources c'est très suspect)
    fun add(task: Task) {
        viewModelScope.launch {
            webService.create(task)
        }

        refresh()
    }
    fun edit(task: Task) {
        viewModelScope.launch {
            webService.update(task)
        }

        refresh()
    }
    fun remove(task: Task) {
        viewModelScope.launch {
            webService.delete(task.id)
        }

        refresh()
    }

}