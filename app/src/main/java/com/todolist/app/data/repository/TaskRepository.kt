package com.todolist.app.data.repository

import com.todolist.app.data.dao.TaskDao
import com.todolist.app.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun getTaskById(id: Int): TaskEntity? = taskDao.getTaskById(id)

    suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
}
