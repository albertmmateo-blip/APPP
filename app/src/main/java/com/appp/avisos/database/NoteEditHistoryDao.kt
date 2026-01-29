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
     * Get edit history entries for a specific note filtered by user
     */
    @Query("SELECT * FROM note_edit_history WHERE note_id = :noteId AND modified_by = :username ORDER BY timestamp DESC")
    fun getEditHistoryForNoteByUser(noteId: Int, username: String): LiveData<List<NoteEditHistory>>
    
    /**
     * Get edit history entries for a specific note within a date range
     */
    @Query("SELECT * FROM note_edit_history WHERE note_id = :noteId AND timestamp >= :startDate AND timestamp <= :endDate ORDER BY timestamp DESC")
    fun getEditHistoryForNoteDateRange(noteId: Int, startDate: Long, endDate: Long): LiveData<List<NoteEditHistory>>
    
    /**
     * Get edit history entries for a specific note filtered by user and date range
     */
    @Query("SELECT * FROM note_edit_history WHERE note_id = :noteId AND modified_by = :username AND timestamp >= :startDate AND timestamp <= :endDate ORDER BY timestamp DESC")
    fun getEditHistoryForNoteByUserAndDateRange(noteId: Int, username: String, startDate: Long, endDate: Long): LiveData<List<NoteEditHistory>>
    
    /**
     * Get count of edit history entries for a specific note
     */
    @Query("SELECT COUNT(*) FROM note_edit_history WHERE note_id = :noteId")
    suspend fun getEditHistoryCount(noteId: Int): Int
    
    /**
     * Get all distinct users who have modified a specific note
     */
    @Query("SELECT DISTINCT modified_by FROM note_edit_history WHERE note_id = :noteId AND modified_by IS NOT NULL ORDER BY modified_by ASC")
    suspend fun getDistinctModifiersForNote(noteId: Int): List<String>
}
