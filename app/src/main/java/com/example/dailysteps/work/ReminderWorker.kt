// app/src/main/java/com/example/dailysteps/work/ReminderWorker.kt
package com.example.dailysteps.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.dailysteps.notifications.NotificationHelper
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {

        val type = inputData.getString(KEY_TYPE) ?: return Result.success()
        val ctx = applicationContext

        when (type) {
            "morning" -> {
                NotificationHelper.showNotification(
                    ctx,
                    100,
                    ctx.getString(R.string.notif_title),
                    ctx.getString(R.string.notif_morning_msg)
                )
            }
            "midday" -> {

                val fmt = DateTimeFormatter.ISO_DATE
                val iso = ServiceLocator.preferences
                    .lastDate.first()
                    .takeIf(String::isNotBlank)
                    ?: LocalDate.now().format(fmt)
                val tasks = ServiceLocator
                    .provideTaskRepository()
                    .getTasks(iso)
                    .first()
                if (tasks.any { !it.done }) {
                    NotificationHelper.showNotification(
                        ctx,
                        101,
                        ctx.getString(R.string.notif_title),
                        ctx.getString(R.string.notif_midday_msg)
                    )
                }
            }
            "evening" -> {
                NotificationHelper.showNotification(
                    ctx,
                    102,
                    ctx.getString(R.string.notif_title),
                    ctx.getString(R.string.notif_evening_msg)
                )
            }
        }
        return Result.success()
    }

    companion object {
        private const val KEY_TYPE = "type"
        fun makeInputData(type: String) = workDataOf(KEY_TYPE to type)
    }
}
