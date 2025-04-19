package com.alcidev.onequestionjournal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journals")
data class JournalContent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question:String,
    val answer:String,
    val time:String)