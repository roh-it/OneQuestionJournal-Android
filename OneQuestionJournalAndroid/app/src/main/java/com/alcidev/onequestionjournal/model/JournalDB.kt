package com.alcidev.onequestionjournal.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [JournalContent::class], version = 1)
abstract class JournalDB:RoomDatabase() {
    abstract fun journalDao():JournalContentDAO
}