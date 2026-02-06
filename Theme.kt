package com.example.todolist.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColors = lightColors(
    primary = androidx.compose.ui.graphics.Color(0xFF6200EE),
    primaryVariant = androidx.compose.ui.graphics.Color(0xFF3700B3),
    secondary = androidx.compose.ui.graphics.Color(0xFF03DAC6)
)

@Composable
fun TodoListTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = LightColors) {
        content()
    }
}