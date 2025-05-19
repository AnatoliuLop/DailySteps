
package com.example.dailysteps.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dailysteps.data.ServiceLocator
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyRolloverWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val repo = ServiceLocator.provideTaskRepository()
        val prefs = ServiceLocator.preferences
        val isoFmt = DateTimeFormatter.ISO_DATE


        val lastIso = prefs.lastDate.first().takeIf { it.isNotBlank() }
        val yesterday = if (lastIso != null) {
            LocalDate.parse(lastIso, isoFmt)
        } else {
            LocalDate.now().minusDays(1)
        }
        val yesterdayIso = yesterday.format(isoFmt)
        val todayIso     = yesterday.plusDays(1).format(isoFmt)


        val tasksYesterday = repo.getTasks(yesterdayIso).first()
        tasksYesterday
            .filterNot { it.done }
            .forEach { task ->
                repo.insert(task.copy(id = 0, date = todayIso))
            }


        prefs.setLastDate(todayIso)

        return Result.success()
    }
}
