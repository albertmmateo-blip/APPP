package com.appp.avisos.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteEditHistoryDao {
    /**
     * Insert a new edit history entry
     */
    @Insert
    suspend fun insertEditHistory(history: NoteEditHistory): Long
    
    /**
     * Get all edit history entries for a specific note, ordered by timestamp descending (newest first)
     */
    @Query("SELECT * FROM note_edit_history WHERE note_id = :noteId ORDER BY timestamp DESC")
    fun getEditHistoryForNote(noteId: Int): LiveData<List<NoteEditHistory>>
    
    /**
     * Get count of edit history entries for a specific note
     */
    @Query("SELECT COUNT(*) FROM note_edit_history WHERE note_id = :noteId")
    suspend fun getEditHistoryCount(noteId: Int): Int
}
