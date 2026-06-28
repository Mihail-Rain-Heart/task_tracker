package com.task.tracker.core.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.tracker.core.common.presentation.mvi.Action
import com.task.tracker.core.common.presentation.mvi.Effect
import com.task.tracker.core.common.presentation.mvi.State
import com.task.tracker.core.common.util.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<A: Action, S: State, E: Effect>(val logger: Logger) : ViewModel() {

    protected abstract val mutableState: MutableStateFlow<S>

    protected val mutableEffects = MutableSharedFlow<E>()

    val state get() = mutableState.asStateFlow()

    val effects = mutableEffects.asSharedFlow()

    init {
        logger.i(TAG, "init ${this::class.simpleName}")
    }

    abstract fun handle(action: A)

    open fun handle(effect: E) {
        viewModelScope.launch { mutableEffects.emit(value = effect) }
    }

    override fun onCleared() {
        super.onCleared()
        logger.i(TAG, "cleared ${this::class.simpleName}")
    }
}

private const val TAG = "VM"
