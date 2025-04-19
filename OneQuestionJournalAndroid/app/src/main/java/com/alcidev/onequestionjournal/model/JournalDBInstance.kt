package com.alcidev.onequestionjournal.model

import android.content.Context
import androidx.room.Room

object JournalDBInstance {
    private var INSTANCE: JournalDB? = null

    private const val  DB_NAME = "JournalDB"

    fun getDatabase(context: Context): JournalDB {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                JournalDB::class.java,
                DB_NAME
            ).build()
            INSTANCE = instance
            instance
        }
    }
}