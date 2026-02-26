package com.todolist.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddEditTask : Screen("add_edit_task?taskId={taskId}") {
        fun passTaskId(taskId: Int = -1): String {
            return "add_edit_task?taskId=$taskId"
        }
    }
}
