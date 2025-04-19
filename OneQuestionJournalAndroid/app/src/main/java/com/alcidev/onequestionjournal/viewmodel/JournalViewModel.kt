package com.alcidev.onequestionjournal.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.alcidev.onequestionjournal.model.JournalContent
import com.alcidev.onequestionjournal.model.JournalContentDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class JournalViewModel(private val journalDao:JournalContentDAO): ViewModel() {

    private val _journals = MutableStateFlow<List<JournalContent>>(emptyList())
    val journals: StateFlow<List<JournalContent>> = _journals

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchJournalContent()
    }

    private fun fetchJournalContent() {
        viewModelScope.launch {
            journalDao.getAllJournals().collect { journalList ->
                _isLoading.value = false
                _journals.value = journalList
            }
        }
    }

    fun addJournal(journal: JournalContent) {
        viewModelScope.launch {
            journalDao.insertJournal(journal)
        }
    }

    fun getCurrentFormattedDateTime(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yy HH:mm")
            return current.format(formatter)
        } else {
            val calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat("dd-MMMM-yy HH:mm", Locale.getDefault())
            return formatter.format(calendar.time)
        }
    }
}