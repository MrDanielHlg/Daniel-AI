package com.example.todolist.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TodoDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: TodoDao

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java).build()
        dao = db.todoDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndObserve() = runBlocking {
        val t = Todo(title = "Test", description = "desc")
        val id = dao.insert(t)
        val list = dao.observeAll().first()
        assertTrue(list.any { it.title == "Test" })
    }

    @Test
    fun updateAndDelete() = runBlocking {
        val t = Todo(title = "ToUpdate")
        val id = dao.insert(t)
        val inserted = dao.observeAll().first().first { it.title == "ToUpdate" }
        val updated = inserted.copy(title = "Updated")
        dao.update(updated)
        val list = dao.observeAll().first()
        assertTrue(list.any { it.title == "Updated" })
        dao.delete(updated)
        val after = dao.observeAll().first()
        assertFalse(after.any { it.title == "Updated" })
    }
}