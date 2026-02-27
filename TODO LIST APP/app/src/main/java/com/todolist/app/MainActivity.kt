package com.todolist.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.todolist.app.ui.navigation.Screen
import com.todolist.app.ui.screens.AddEditTaskScreen
import com.todolist.app.ui.screens.HomeScreen
import com.todolist.app.ui.theme.TodoListTheme
import com.todolist.app.ui.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoListTheme {
                val navController = rememberNavController()
                val viewModel: TaskViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            viewModel = viewModel,
                            onAddTaskClick = {
                                navController.navigate(Screen.AddEditTask.passTaskId())
                            },
                            onEditTaskClick = { taskId ->
                                navController.navigate(Screen.AddEditTask.passTaskId(taskId))
                            }
                        )
                    }
                    composable(
                        route = Screen.AddEditTask.route,
                        arguments = listOf(
                            navArgument("taskId") {
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
                        AddEditTaskScreen(
                            taskId = taskId,
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
