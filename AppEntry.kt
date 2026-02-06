package com.example.todolist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.data.Todo
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AppEntry(vm: TodoViewModel) {
    val todos by vm.todos.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Todo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Todo List") }, actions = {
                IconButton(onClick = { /* placeholder for export/import */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                onQueryChanged = { vm.setQuery(it) }
            )
            TodoList(
                todos = todos,
                onToggle = { vm.toggleComplete(it) },
                onDelete = { vm.delete(it) },
                onEdit = { editing = it }
            )
        }
    }

    if (showAdd) {
        AddEditTodoDialog(
            onDismiss = { showAdd = false },
            onSave = { title, desc, due ->
                vm.addTodo(title, desc, due)
                showAdd = false
            }
        )
    }

    editing?.let { t ->
        AddEditTodoDialog(
            todo = t,
            onDismiss = { editing = null },
            onSave = { title, desc, due ->
                vm.updateTodo(t.copy(title = title, description = desc, dueAt = due))
                editing = null
            }
        )
    }
}

@Composable
fun SearchBar(onQueryChanged: (String) -> Unit) {
    var q by remember { mutableStateOf("") }
    OutlinedTextField(
        value = q,
        onValueChange = {
            q = it
            onQueryChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        placeholder = { Text("Search...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
    )
}

@Composable
fun TodoList(todos: List<Todo>, onToggle: (Todo) -> Unit, onDelete: (Todo) -> Unit, onEdit: (Todo) -> Unit) {
    if (todos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tasks yet. Tap + to add.")
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(todos) { todo ->
            TodoItem(todo = todo, onToggle = onToggle, onDelete = onDelete, onEdit = onEdit)
        }
    }
}

@Composable
fun TodoItem(todo: Todo, onToggle: (Todo) -> Unit, onDelete: (Todo) -> Unit, onEdit: (Todo) -> Unit) {
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .clickable { onEdit(todo) }
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = todo.completed, onCheckedChange = { onToggle(todo) })
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(todo.title, style = MaterialTheme.typography.subtitle1)
                if (todo.description.isNotBlank()) {
                    Text(todo.description, style = MaterialTheme.typography.body2, maxLines = 2)
                }
                todo.dueAt?.let { due ->
                    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        .withZone(ZoneId.systemDefault())
                    Text("Due: ${fmt.format(Instant.ofEpochMilli(due))}", style = MaterialTheme.typography.caption)
                }
            }
            IconButton(onClick = { onDelete(todo) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun AddEditTodoDialog(todo: Todo? = null, onDismiss: () -> Unit, onSave: (String, String, Long?) -> Unit) {
    var title by remember { mutableStateOf(todo?.title ?: "") }
    var desc by remember { mutableStateOf(todo?.description ?: "") }
    var dueStr by remember { mutableStateOf(todo?.dueAt?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (todo == null) "Add Todo" else "Edit Todo") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
                OutlinedTextField(value = dueStr, onValueChange = { dueStr = it }, label = { Text("Due (epoch ms) optional") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val due = dueStr.toLongOrNull()
                if (title.isNotBlank()) onSave(title.trim(), desc.trim(), due)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}