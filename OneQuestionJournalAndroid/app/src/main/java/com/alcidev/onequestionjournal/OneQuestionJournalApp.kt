package com.alcidev.onequestionjournal

import android.app.Application
import com.alcidev.onequestionjournal.model.JournalDB
import com.alcidev.onequestionjournal.model.JournalDBInstance


class OneQuestionJournalApp: Application() {

    private lateinit var database: JournalDB

    override fun onCreate() {
        super.onCreate()
        database = JournalDBInstance.getDatabase(this)
    }

}