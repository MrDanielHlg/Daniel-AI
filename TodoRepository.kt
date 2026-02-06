package com.example.todolist.repo

import android.content.Context
import com.example.todolist.data.AppDatabase
import com.example.todolist.data.Todo
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val db: AppDatabase) {

    private val dao = db.todoDao()

    fun observeAll(): Flow<List<Todo>> = dao.observeAll()

    fun search(q: String): Flow<List<Todo>> = dao.search("%$q%")

    suspend fun add(todo: Todo): Long = dao.insert(todo)

    suspend fun update(todo: Todo) = dao.update(todo)

    suspend fun delete(todo: Todo) = dao.delete(todo)

    suspend fun deleteAll() = dao.deleteAll()

    companion object {
        fun create(context: Context): TodoRepository {
            return TodoRepository(AppDatabase.getInstance(context))
        }
    }
}