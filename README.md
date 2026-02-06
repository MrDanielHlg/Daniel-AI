# TodoList (Android)

A simple production-style to-do list Android application built with Kotlin and Jetpack Compose. Uses Room for persistent local storage.

Features:
- Add / Edit / Delete tasks
- Mark tasks complete
- Search tasks
- Persistent local storage using Room (SQLite)
- Export / import helpers (ViewModel provides export/import JSON strings)
- Unit tests for DAO

How to build:
1. Open the `app/` project in Android Studio (or import the Gradle project from repository root).
2. Build & run on device or emulator (minSdk 24).
3. The app stores todos in an on-device Room database: `todos.db`.

Project structure (important files):
- app/src/main/java/com/example/todolist/data: Room entity, DAO, Database
- app/src/main/java/com/example/todolist/repo: Repository abstraction
- app/src/main/java/com/example/todolist/ui: Compose UI, ViewModel, MainActivity
- app/src/test: Unit tests for Room DAO

Notes:
- Export/import uses JSON strings in ViewModel. You can extend UI to save to files using `context.getExternalFilesDir()` if you want user-accessible exports.
- No external network or cloud services are used; all data is stored locally.
- Unit tests use an in-memory Room database.

Run tests:
- From Android Studio: Run `app/src/test` unit tests.
- Or use Gradle: `./gradlew test`.