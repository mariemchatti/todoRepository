package com.example.toDoMariem.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toDoMariem.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Collections.emptyList

class TasksListViewModel : ViewModel() {
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
            tasksStateFlow.value = fetchedTasks // on modifie le flow, ce qui déclenche ses observer

        }
    }
    fun update(task: Task) {
        viewModelScope.launch {
            val response = webService.update(task)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.raw()}")
                return@launch
            }

            val updatedTask = response.body()!!
            val updatedList = tasksStateFlow.value.map {
                if (it.id == updatedTask.id) updatedTask else it
            }
            tasksStateFlow.value = updatedList
        }
    }

    // Suppression d'une tâche sur le serveur
    fun delete(task: Task) {
        viewModelScope.launch {
            val response = task?.id?.let { webService.delete(it) }
            if (!response?.isSuccessful!!) {
                // Gestion des erreurs
                return@launch
            }

            val updatedList = tasksStateFlow.value.filterNot { it.id == task.id }
            tasksStateFlow.value = updatedList
        }
    }

    // Ajout d'une nouvelle tâche sur le serveur
    fun add(task: Task) {
        viewModelScope.launch {
            val response = webService.create(task)
            if (!response.isSuccessful) {
                return@launch
            }

            val createdTask = response.body() ?: return@launch
            val updatedList = tasksStateFlow.value + createdTask
            tasksStateFlow.value = updatedList
        }
    }
}