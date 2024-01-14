package com.example.toDoMariem.list

import com.example.toDoMariem.Task

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task:Task)
}