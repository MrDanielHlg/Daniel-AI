package com.example.todolist.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.Todo
import com.example.todolist.repo.TodoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TodoViewModel(private val repo: TodoRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // Combined UI stream: if query is empty observeAll, else search
    val todos = _query.flatMapLatest { q ->
        if (q.isBlank()) repo.observeAll()
        else repo.search(q)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(q: String) {
        _query.value = q
    }

    fun addTodo(title: String, description: String = "", dueAt: Long? = null) {
        viewModelScope.launch {
            val todo = Todo(title = title, description = description, dueAt = dueAt)
            repo.add(todo)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch { repo.update(todo) }
    }

    fun toggleComplete(todo: Todo) {
        viewModelScope.launch { repo.update(todo.copy(completed = !todo.completed)) }
    }

    fun delete(todo: Todo) {
        viewModelScope.launch { repo.delete(todo) }
    }

    fun exportToJsonString(onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val list = repo.observeAll().first()
            // Simple JSON using Kotlin's standard library
            val items = list.map {
                mapOf(
                    "id" to it.id,
                    "title" to it.title,
                    "description" to it.description,
                    "createdAt" to it.createdAt,
                    "dueAt" to it.dueAt,
                    "completed" to it.completed
                )
            }
            val json = kotlinx.serialization.json.Json { prettyPrint = true }.encodeToString(
                kotlinx.serialization.json.JsonArray.serializer(),
                kotlinx.serialization.json.JsonArray(items.map { kotlinx.serialization.json.JsonObject(it.mapValues { v -> kotlinx.serialization.json.JsonPrimitive(v.value?.toString() ?: "") }) })
            )
            onComplete(json)
        }
    }

    fun importFromJsonString(json: String) {
        viewModelScope.launch {
            try {
                val parsed = kotlinx.serialization.json.Json.parseToJsonElement(json)
                if (parsed is kotlinx.serialization.json.JsonArray) {
                    parsed.forEach { elem ->
                        if (elem is kotlinx.serialization.json.JsonObject) {
                            val title = elem["title"]?.jsonPrimitive?.content ?: ""
                            val description = elem["description"]?.jsonPrimitive?.content ?: ""
                            val completed = elem["completed"]?.jsonPrimitive?.booleanOrNull ?: false
                            val dueAt = elem["dueAt"]?.jsonPrimitive?.longOrNull
                            repo.add(Todo(title = title, description = description, dueAt = dueAt, completed = completed))
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            val repo = TodoRepository.create(context)
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoViewModel(repo) as T
                }
            }
        }
    }
}