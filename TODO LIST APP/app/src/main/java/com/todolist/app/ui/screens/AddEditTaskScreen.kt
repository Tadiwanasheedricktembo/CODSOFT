package com.todolist.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.todolist.app.data.entity.Priority
import com.todolist.app.ui.theme.HighPriority
import com.todolist.app.ui.theme.LowPriority
import com.todolist.app.ui.theme.MediumPriority
import com.todolist.app.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: Int = -1,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.LOW) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    
    val isEditing = taskId != -1
    val taskState by viewModel.currentTask.collectAsState()

    LaunchedEffect(taskId) {
        if (isEditing) {
            viewModel.getTaskById(taskId)
        }
    }

    LaunchedEffect(taskState) {
        if (isEditing && taskState != null) {
            title = taskState!!.title
            description = taskState!!.description
            priority = taskState!!.priority
            dueDate = taskState!!.dueDate
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate ?: System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Task" else "New Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Priority", style = MaterialTheme.typography.titleMedium)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PriorityButton(
                    label = "Low",
                    color = LowPriority,
                    isSelected = priority == Priority.LOW,
                    onClick = { priority = Priority.LOW }
                )
                PriorityButton(
                    label = "Medium",
                    color = MediumPriority,
                    isSelected = priority == Priority.MEDIUM,
                    onClick = { priority = Priority.MEDIUM }
                )
                PriorityButton(
                    label = "High",
                    color = HighPriority,
                    isSelected = priority == Priority.HIGH,
                    onClick = { priority = Priority.HIGH }
                )
            }

            Text("Due Date", style = MaterialTheme.typography.titleMedium)
            
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dueDate?.let { 
                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it)) 
                        } ?: "Select Due Date"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        if (isEditing) {
                            viewModel.updateExistingTask(taskId, title, description, priority, dueDate)
                        } else {
                            viewModel.insertTask(title, description, priority, dueDate)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (isEditing) "Save Changes" else "Create Task")
            }
        }
    }
}

@Composable
fun PriorityButton(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) color else color.copy(alpha = 0.1f),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, color),
        modifier = Modifier.height(40.dp).width(100.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) Color.White else color,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
