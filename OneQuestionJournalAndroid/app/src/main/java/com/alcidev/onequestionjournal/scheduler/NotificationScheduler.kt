package com.alcidev.onequestionjournal.scheduler

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.alcidev.onequestionjournal.R
import com.alcidev.onequestionjournal.model.DailyNotificationsWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleDailyNotifications(hr:Int, min:Int){

        val now = Calendar.getInstance()

        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hr)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if(scheduledTime.before(now)) {
            scheduledTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = scheduledTime.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<DailyNotificationsWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            context.getString(R.string.notification_work_name),
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
        
    }

    fun cancelDailyNotification() {
        WorkManager.getInstance(context).cancelUniqueWork(context.getString(R.string.notification_work_name))
    }

}