package com.todolist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.todolist.app.data.database.AppDatabase
import com.todolist.app.data.entity.Priority
import com.todolist.app.data.entity.TaskEntity
import com.todolist.app.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val allTasks: StateFlow<List<TaskEntity>>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private val _currentTask = MutableStateFlow<TaskEntity?>(null)
    val currentTask = _currentTask.asStateFlow()

    fun getTaskById(id: Int) {
        viewModelScope.launch {
            _currentTask.value = repository.getTaskById(id)
        }
    }

    fun insertTask(
        title: String,
        description: String,
        priority: Priority,
        dueDate: Long?
    ) {
        viewModelScope.launch {
            val newTask = TaskEntity(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate
            )
            repository.insertTask(newTask)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun updateExistingTask(
        id: Int,
        title: String,
        description: String,
        priority: Priority,
        dueDate: Long?
    ) {
        viewModelScope.launch {
            val task = repository.getTaskById(id)
            task?.let {
                val updatedTask = it.copy(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate
                )
                repository.updateTask(updatedTask)
            }
        }
    }
}
