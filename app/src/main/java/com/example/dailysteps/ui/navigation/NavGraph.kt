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
        composable(Routes.MENU) {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            val vm: MainMenuViewModel = viewModel(
                factory = SimpleFactory {
                    MainMenuViewModel(
                        GetStreakUseCase(ServiceLocator.provideTaskRepository(),ServiceLocator.preferences)
                    )
                }
            )
            MainMenuScreen(
                streak         = vm.streak,
                onNavigate     = { route -> navController.navigate(route) },
                onSettings     = { navController.navigate(Routes.SETTINGS) },
                onDebugPrevDay = { scope.launch { ServiceLocator.debugPreviousDay() } },
                onDebugNextDay = { scope.launch { ServiceLocator.debugNextDay() } },
                onDebugReset   = { scope.launch { ServiceLocator.debugReset() } },
                onRunRolloverNow = {
                    val request = OneTimeWorkRequestBuilder<DailyRolloverWorker>().build()
                    WorkManager.getInstance(context).enqueue(request)
                }
            )
        }


        composable(Routes.PLAN) {
            val context = LocalContext.current
            val vm: PlanViewModel = viewModel(factory = SimpleFactory {
                PlanViewModel(
                    ServiceLocator.preferences,
                    context,
                    ServiceLocator.provideGetTasksUseCase(),
                    ServiceLocator.provideAddTaskUseCase(),
                    ServiceLocator.provideUpdateTaskUseCase(),
                    ServiceLocator.provideDeleteTaskUseCase()
                )
            })
            DailyPlanScreen(
                tasks        = vm.tasks,
                error        = vm.error,
                onAdd        = vm::add,
                onNoteChange = vm::changeNote,
                onEdit       = vm::editDescription,
                onDelete     = vm::remove,
                onBack       = { navController.popBackStack() },
                onSettings   = { navController.navigate(Routes.SETTINGS) },
                onNext       = { navController.navigate(Routes.REVIEW) }
            )
        }


        composable(Routes.REVIEW) {
            val context = LocalContext.current
            val vm: ReviewViewModel = viewModel(factory = SimpleFactory {
                ReviewViewModel(
                    context,
                    ServiceLocator.preferences,
                    ServiceLocator.provideGetTasksUseCase(),
                    ServiceLocator.provideToggleDoneUseCase(),
                    ServiceLocator.provideGetDayNoteUseCase(),
                    ServiceLocator.provideSaveDayNoteUseCase()

                )
            })

            DailyReviewScreen(
                tasks         = vm.tasks,
                dayNote       = vm.dayNote,
                onToggle      = vm::toggle,
                onSaveNote    = vm::saveNote,
                onCompleteDay = vm::completeDay,
                onDismissCompletion = vm::clearCompletionMessage,
                completionMessage = vm.completionMessage,
                onBack        = { navController.popBackStack() },
                onSettings    = { navController.navigate(Routes.SETTINGS) },
                onNext        = { navController.navigate(Routes.HISTORY) }
            )
        }




        composable(Routes.HISTORY) {
            val vm: HistoryViewModel = viewModel(factory = SimpleFactory {
                HistoryViewModel(
                    ServiceLocator.preferences,
                    ServiceLocator.provideGetHistoryDatesUseCase(),
                    ServiceLocator.provideGetCompletionRatesUseCase(),
                    ServiceLocator.provideGetTasksUseCase(),
                    ServiceLocator.provideGetDayNoteUseCase()
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


        composable(Routes.STATS) {
            val vm: StatsViewModel = viewModel(factory = SimpleFactory {
                StatsViewModel(
                    ServiceLocator.preferences,
                    ServiceLocator.provideGetTodayCompletionUseCase(),
                    ServiceLocator.provideGetStreakUseCase(),
                    ServiceLocator.provideGetTaskStreaksUseCase(),
                    ServiceLocator.provideGetWeeklyCompletionUseCase()
                )
            })
            StatsScreen(
                percentDone = vm.percentDone,
                streak = vm.streak,
                taskStreaks = vm.taskStreaks,
                weeklyStats = vm.weeklyStats,
                onBack      = { navController.popBackStack() },
                onSettings  = { navController.navigate(Routes.SETTINGS) }
            )
        }


        composable(Routes.SETTINGS) {
            val prefs = ServiceLocator.preferences
            // Собираем из Flow текущее состояние
            val locale by prefs.locale.collectAsState(initial = "en")
            val isDark by prefs.isDarkTheme.collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            SettingsDialog(
                currentLocale = locale,
                onLocaleChange = { newLoc ->
                    scope.launch { prefs.setLocale(newLoc) }
                },
                currentTheme = isDark,
                onThemeChange = { dark ->
                    scope.launch { prefs.setDarkTheme(dark) }
                },
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}