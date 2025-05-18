// app/src/main/java/com/example/dailysteps/ui/navigation/NavGraph.kt
package com.example.dailysteps.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.domain.usecase.stats.GetStreakUseCase
import com.example.dailysteps.domain.usecase.steps.GetStepEntryUseCase
import com.example.dailysteps.domain.usecase.steps.UpdateStepEntryUseCase
import com.example.dailysteps.domain.usecase.tasks.GetTasksUseCase
import com.example.dailysteps.domain.usecase.tasks.AddTaskUseCase
import com.example.dailysteps.domain.usecase.tasks.ToggleDoneUseCase
import com.example.dailysteps.domain.usecase.daynote.GetDayNoteUseCase
import com.example.dailysteps.domain.usecase.daynote.SaveDayNoteUseCase
import com.example.dailysteps.domain.usecase.stats.GetTaskStreaksUseCase
import com.example.dailysteps.domain.usecase.tasks.GetCompletionRatesUseCase
import com.example.dailysteps.domain.usecase.tasks.GetHistoryDatesUseCase
import com.example.dailysteps.domain.usecase.tasks.UpdateTaskUseCase
import com.example.dailysteps.ui.screens.*
import com.example.dailysteps.ui.viewmodel.*
import com.example.dailysteps.work.DailyRolloverWorker
import kotlinx.coroutines.launch

@Composable
fun DailyStepsNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController  = navController,
        startDestination = Routes.MENU
    ) {
        // Главное меню
        composable(Routes.MENU) {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
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
                onNavigate   = { route -> navController.navigate(route) },
                onDebugPrevDay = { scope.launch { ServiceLocator.debugPreviousDay() } },
                onDebugNextDay  = { scope.launch { ServiceLocator.debugNextDay() } },
                onDebugReset   = { scope.launch { ServiceLocator.debugReset() } },
                onRunRolloverNow = {
                    // через WorkManager запустить worker сразу
                    val request = OneTimeWorkRequestBuilder<DailyRolloverWorker>().build()
                    WorkManager.getInstance(context)
                        .enqueue(request)
                }
            )
        }

        // План на день
        composable(Routes.PLAN) {
            val vm: PlanViewModel = viewModel(factory = SimpleFactory {
                PlanViewModel(
                    ServiceLocator.preferences,
                    ServiceLocator.provideGetTasksUseCase(),
                    ServiceLocator.provideAddTaskUseCase(),
                    ServiceLocator.provideUpdateTaskUseCase(),
                    ServiceLocator.provideDeleteTaskUseCase()
                )
            })
            DailyPlanScreen(
                tasks        = vm.tasks,
                onAdd        = vm::add,
                onNoteChange = vm::changeNote,
                onEdit       = vm::editDescription,
                onDelete     = vm::remove,
                onBack       = { navController.popBackStack() },
                onSettings   = { navController.navigate(Routes.SETTINGS) },
                onNext       = { navController.navigate(Routes.REVIEW) }
            )
        }

        // Обзор дня
        composable(Routes.REVIEW) {
            val vm: ReviewViewModel = viewModel(factory = SimpleFactory {
                ReviewViewModel(
                    ServiceLocator.preferences,
                    ServiceLocator.provideGetTasksUseCase(),
                    ServiceLocator.provideToggleDoneUseCase(),
                    ServiceLocator.provideGetDayNoteUseCase(),
                    ServiceLocator.provideSaveDayNoteUseCase()
                    // Если вы всё ещё переносите задачи, сюда же провайдьте AddTaskUseCase
                )
            })

            DailyReviewScreen(
                tasks         = vm.tasks,
                dayNote       = vm.dayNote,
                onToggle      = vm::toggle,
                onSaveNote    = vm::saveNote,
                onCompleteDay = vm::completeDay,
                onDismissCompletion = vm::clearCompletionMessage,  // ← прокинули
                completionMessage = vm.completionMessage,           // ← прокинули
                onBack        = { navController.popBackStack() },
                onSettings    = { navController.navigate(Routes.SETTINGS) },
                onNext        = { navController.navigate(Routes.HISTORY) }
            )
        }



        // История
        composable(Routes.HISTORY) {
            val vm: HistoryViewModel = viewModel(factory = SimpleFactory {
                HistoryViewModel(
                    ServiceLocator.preferences,
                    ServiceLocator.provideGetHistoryDatesUseCase(),
                    ServiceLocator.provideGetCompletionRatesUseCase(),
                    ServiceLocator.provideGetTasksUseCase(),       // ← новый
                    ServiceLocator.provideGetDayNoteUseCase()     // ← новый
                )
            })
            HistoryScreen(
                calendarDates    = vm.calendarDates,
                historyRates     = vm.historyRates,
                tasksForDate     = vm.tasksForDate,
                dayNoteForDate   = vm.dayNoteForDate,
                selectedDate     = vm.selectedDate,
                onDaySelected    = vm::selectDate,
                onClearSelection = vm::clearSelection,
                onBack       = { navController.popBackStack() },
                onSettings   = { navController.navigate(Routes.SETTINGS) },
                prefs            = ServiceLocator.preferences
            )
        }

        // Статистика
        composable(Routes.STATS) {
            val vm: StatsViewModel = viewModel(factory = SimpleFactory {
                StatsViewModel(
                    ServiceLocator.preferences,
                    ServiceLocator.provideGetTodayCompletionUseCase(),
                    ServiceLocator.provideGetStreakUseCase(),
                    ServiceLocator.provideGetTaskStreaksUseCase()
                )
            })
            StatsScreen(
                percentDone = vm.percentDone,
                streak = vm.streak,
                taskStreaks = vm.taskStreaks,
                onBack      = { navController.popBackStack() },
                onSettings  = { navController.navigate(Routes.SETTINGS) }
            )
        }

      //  Экран настроек
        composable(Routes.SETTINGS) {
            val prefs = ServiceLocator.preferences
            // Собираем из Flow текущее состояние
            val locale by prefs.locale.collectAsState(initial = "en")
            val isDark by prefs.isDarkTheme.collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            SettingsDialog(
                currentLocale = locale,                    // используем локальную переменную
                onLocaleChange = { newLoc ->
                    scope.launch { prefs.setLocale(newLoc) }
                },
                currentTheme = isDark,                     // используем локальную переменную
                onThemeChange = { dark ->
                    scope.launch { prefs.setDarkTheme(dark) }
                },
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}
