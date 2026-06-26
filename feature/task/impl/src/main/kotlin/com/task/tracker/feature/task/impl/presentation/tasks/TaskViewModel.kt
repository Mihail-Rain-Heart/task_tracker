package com.task.tracker.feature.task.impl.presentation.tasks

import androidx.lifecycle.viewModelScope
import com.task.tracker.core.data.util.BaseViewModel
import com.task.tracker.core.domain.GetTasksUseCase
import com.task.tracker.feature.task.impl.domain.tasks.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
internal class TaskViewModel @Inject constructor(
    private val getTaskUseCase: GetTasksUseCase,
) : BaseViewModel() {

    private val _state = MutableStateFlow(
        value = State(tasks = emptyList())
    )

    private val _uiState = MutableStateFlow(
        value = UiState(
            tasks = emptyList(),
            isRefreshing = false,
        )
    )

    val uiState = _uiState.asStateFlow()

    init {

        getTaskUseCase()
            .onEach { tasks ->
                // TODO("Implement update state by tasks")
            }
            .launchIn(scope = viewModelScope)
    }
}
