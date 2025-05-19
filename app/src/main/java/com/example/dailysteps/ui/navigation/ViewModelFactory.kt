package com.example.dailysteps.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
// Добавьте эти импорты:
import com.example.dailysteps.domain.usecase.tasks.GetTasksUseCase
import com.example.dailysteps.domain.usecase.tasks.AddTaskUseCase
import com.example.dailysteps.domain.usecase.tasks.ToggleDoneUseCase


class ViewModelFactory(
    private val get: GetTasksUseCase,
    private val add: AddTaskUseCase,
    private val toggle: ToggleDoneUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            GetTasksUseCase::class.java,
            AddTaskUseCase::class.java,
            ToggleDoneUseCase::class.java
        ).newInstance(get, add, toggle) as T
    }
}


class SimpleFactory<T : ViewModel>(val creator: () -> T) : ViewModelProvider.Factory {
    override fun <U : ViewModel> create(modelClass: Class<U>): U {
        @Suppress("UNCHECKED_CAST")
        return creator() as U
    }
}
