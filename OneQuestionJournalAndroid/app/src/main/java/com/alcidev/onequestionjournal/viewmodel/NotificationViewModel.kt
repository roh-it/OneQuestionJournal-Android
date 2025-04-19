package com.alcidev.onequestionjournal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcidev.onequestionjournal.scheduler.NotificationScheduler

class NotificationViewModel(private val notificationScheduler: NotificationScheduler):ViewModel() {

    private val _isNotificationEnabled = MutableLiveData<Boolean>(false)

    val isNotificationEnabled: LiveData<Boolean> get() = _isNotificationEnabled



    fun toggleNotification(isEnabled: Boolean, hour: Int, minute: Int) {

        _isNotificationEnabled.value = isEnabled
        if (isEnabled) {
            notificationScheduler.scheduleDailyNotifications(hour, minute)
        } else {
            notificationScheduler.cancelDailyNotification()
        }
    }
    
}