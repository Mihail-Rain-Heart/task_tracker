# Task Tracker

Приложение «Трекер задач» с offline-first архитектурой, разрабатываемое в рамках тестового задания.
Синхронизирует задачи с mock-сервером, работает без интернета и корректно разрешает конфликты при восстановлении соединения.

**Статус:** в процессе.

#### Стек
- Kotlin + Coroutines + Flow
- Jetpack Compose
- SQLDelight
- WorkManager (Будет добавлено для синка данных)
- Retrofit / OkHttp
- Hilt

#### Экраны
- Список задач

#### Архитектура
- Clean Architecture (data / domain / presentation)
- MVI

**Шаблон взят из:** https://github.com/android/nowinandroid

Всего времени потрачено: 2д  (за сколько дойдёт до завершения?)
