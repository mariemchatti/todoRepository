package com.example.toDoMariem.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.toDoMariem.Task
import java.util.*


class DetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val taskToEdit = intent.getParcelableExtra<Task>("TASK_TO_EDIT")
                Detail(onValidate = { newTask ->
                    val returnIntent = Intent()
                    returnIntent.putExtra("task", newTask)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }, initialTask = taskToEdit)
            }
        }
    }
}

@Composable
fun Detail(onValidate: (Task) -> Unit, initialTask: Task?) {
    var taskInitialized= Task(
        id = UUID.randomUUID().toString(),
        title = "New Task !",
        description = "Describe your task..."
    )
    var task by remember {mutableStateOf(initialTask ?: taskInitialized)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Task Detail",
            style = MaterialTheme.typography.h1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = task.title ?: "",
            onValueChange = { task = task.copy(title = it) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = task.description ?: "",
            onValueChange = { task = task.copy(description = it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )


        Button(
            onClick = {
                onValidate(task)
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Save")
        }
    }
}
@Preview
@Composable
fun DetailPreview() {
    Detail(onValidate= {}, initialTask = null)
}

