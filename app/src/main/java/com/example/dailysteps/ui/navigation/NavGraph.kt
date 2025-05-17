// app/src/main/java/com/example/dailysteps/ui/navigation/NavGraph.kt
package com.example.dailysteps.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.domain.usecase.GetStreakUseCase
import com.example.dailysteps.domain.usecase.GetStepEntryUseCase
import com.example.dailysteps.domain.usecase.UpdateStepEntryUseCase
import com.example.dailysteps.domain.usecase.GetTasksUseCase
import com.example.dailysteps.domain.usecase.AddTaskUseCase
import com.example.dailysteps.domain.usecase.ToggleDoneUseCase
import com.example.dailysteps.domain.usecase.GetDayNoteUseCase
import com.example.dailysteps.domain.usecase.SaveDayNoteUseCase
import com.example.dailysteps.domain.usecase.GetTaskStreaksUseCase
import com.example.dailysteps.domain.usecase.UpdateTaskUseCase
import com.example.dailysteps.ui.screens.*
import com.example.dailysteps.ui.viewmodel.*

@Composable
fun DailyStepsNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController  = navController,
        startDestination = Routes.MENU
    ) {
        // Главное меню
        composable(Routes.MENU) {
            val vm: MainMenuViewModel = viewModel(
                factory = SimpleFactory {
                    MainMenuViewModel(
                        GetStreakUseCase(ServiceLocator.provideTaskRepository()),
                        GetStepEntryUseCase(ServiceLocator.provideStepRepo()),
                        UpdateStepEntryUseCase(ServiceLocator.provideStepRepo())
                    )
                }
            )
            MainMenuScreen(
                streak       = vm.streak,
                stepState    = vm.stepState,
                onUpdateSteps= vm::onStepCountUpdated,
                onNavigate   = { route -> navController.navigate(route) }
            )
        }

        // План
        composable(Routes.PLAN) {
            val vm: PlanViewModel = viewModel(
                factory = SimpleFactory {
                    PlanViewModel(
                        GetTasksUseCase(ServiceLocator.provideTaskRepository()),
                        AddTaskUseCase(ServiceLocator.provideTaskRepository()),
                        ToggleDoneUseCase(ServiceLocator.provideTaskRepository()),
                        UpdateTaskUseCase(ServiceLocator.provideTaskRepository())
                    )
                }
            )
            DailyPlanScreen(
                tasks   = vm.tasks,
                onAdd   = vm::add,
                onToggle= vm::toggle,
                onNoteChange = vm::changeNote,
                onNext  = { navController.navigate(Routes.REVIEW) },
                onBack  = { navController.popBackStack() }
            )
        }

        // Обзор дня
        composable(Routes.REVIEW) {
            val vm: ReviewViewModel = viewModel(
                factory = SimpleFactory {
                    ReviewViewModel(
                        GetTasksUseCase(ServiceLocator.provideTaskRepository()),
                        ToggleDoneUseCase(ServiceLocator.provideTaskRepository()),
                        GetDayNoteUseCase(ServiceLocator.provideNoteRepo()),
                        SaveDayNoteUseCase(ServiceLocator.provideNoteRepo())
                    )
                }
            )
            DailyReviewScreen(
                tasks    = vm.tasks,
                dayNote  = vm.dayNote,
                onToggle = vm::toggle,
                onSaveNote = vm::saveNote,
                onNext   = { navController.navigate(Routes.HISTORY) },
                onBack   = { navController.popBackStack() }
            )
        }

        // История
        composable(Routes.HISTORY) {
            HistoryScreen(
                onDaySelected = { /* ... */ },
                onBack        = { navController.popBackStack() }
            )
        }

        // Статистика
        composable(Routes.STATS) {
            val vm: StatsViewModel = viewModel(
                factory = SimpleFactory {
                    StatsViewModel(
                        GetTasksUseCase(ServiceLocator.provideTaskRepository()),
                        GetStreakUseCase(ServiceLocator.provideTaskRepository()),
                        GetTaskStreaksUseCase(
                            ServiceLocator.provideDefaultRepo(),
                            ServiceLocator.provideTaskRepository()
                        )
                    )
                }
            )
            StatsScreen(
                percentDone = vm.percentDone,
                streak      = vm.streak,
                taskStreaks = vm.taskStreaks,
                onBack      = { navController.popBackStack() }
            )
        }
    }
}
