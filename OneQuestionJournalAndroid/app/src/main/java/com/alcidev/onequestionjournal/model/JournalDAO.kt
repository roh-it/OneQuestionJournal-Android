package com.alcidev.onequestionjournal.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface JournalContentDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(vararg journalContent: JournalContent)

    @Query("SELECT * FROM journals")
    fun getAllJournals(): Flow<List<JournalContent>>
}
