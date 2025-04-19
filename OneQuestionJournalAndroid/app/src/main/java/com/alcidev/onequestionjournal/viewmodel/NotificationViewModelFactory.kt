package com.alcidev.onequestionjournal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.alcidev.onequestionjournal.scheduler.NotificationScheduler

class NotificationViewModelFactory(private val notificationScheduler:NotificationScheduler) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return NotificationViewModel(notificationScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}