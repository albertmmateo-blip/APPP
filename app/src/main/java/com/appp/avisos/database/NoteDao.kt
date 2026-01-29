package com.appp.avisos.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    /**
     * Insert a new note and return its ID
     */
    @Insert
    suspend fun insertNote(note: Note): Long
    
    /**
     * Update an existing note
     */
    @Update
    suspend fun updateNote(note: Note)
    
    /**
     * Delete a note
     */
    @Delete
    suspend fun deleteNote(note: Note)
    
    /**
     * Get all notes sorted by urgent flag (urgent first), then modified date descending (newest first)
     * Excludes notes in recycle bin
     */
    @Query("SELECT * FROM notes WHERE is_deleted = 0 ORDER BY is_urgent DESC, modified_date DESC")
    fun getAllNotes(): LiveData<List<Note>>
    
    /**
     * Get notes filtered by category, sorted by urgent flag (urgent first), then modified date descending (newest first)
     * Excludes notes in recycle bin
     */
    @Query("SELECT * FROM notes WHERE category = :category AND is_deleted = 0 ORDER BY is_urgent DESC, modified_date DESC")
    fun getNotesByCategory(category: String): LiveData<List<Note>>
    
    /**
     * Get a single note by its ID
     */
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Note?
    
    /**
     * Get count of notes in a specific category
     * Excludes notes in recycle bin
     * @param category The category name to count notes for
     * @return LiveData containing the count of notes in the category
     */
    @Query("SELECT COUNT(*) FROM notes WHERE category = :category AND is_deleted = 0")
    fun getNoteCountByCategory(category: String): LiveData<Int>
    
    // Recycle Bin operations
    
    /**
     * Get all deleted notes sorted by deleted date (newest first)
     */
    @Query("SELECT * FROM notes WHERE is_deleted = 1 ORDER BY deleted_date DESC")
    fun getDeletedNotes(): LiveData<List<Note>>
    
    /**
     * Get deleted notes filtered by deletion type
     */
    @Query("SELECT * FROM notes WHERE is_deleted = 1 AND deletion_type = :type ORDER BY deleted_date DESC")
    fun getDeletedNotesByType(type: String): LiveData<List<Note>>
    
    /**
     * Get notes that have been deleted for more than the specified number of days
     * @param thresholdTimestamp The timestamp before which notes should be considered expired
     */
    @Query("SELECT * FROM notes WHERE is_deleted = 1 AND deleted_date < :thresholdTimestamp")
    suspend fun getExpiredDeletedNotes(thresholdTimestamp: Long): List<Note>
    
    /**
     * Permanently delete all notes that expired before the threshold timestamp (batch operation)
     * @param thresholdTimestamp The timestamp before which notes should be deleted
     */
    @Query("DELETE FROM notes WHERE is_deleted = 1 AND deleted_date < :thresholdTimestamp")
    suspend fun deleteExpiredNotes(thresholdTimestamp: Long): Int
    
    /**
     * Soft delete a note by marking it as deleted
     * @param noteId The ID of the note to soft delete
     * @param deletedDate The timestamp when the note was deleted
     * @param deletionType The type of deletion (Esborrades or Finalitzades)
     */
    @Query("UPDATE notes SET is_deleted = 1, deleted_date = :deletedDate, deletion_type = :deletionType WHERE id = :noteId")
    suspend fun softDeleteNote(noteId: Int, deletedDate: Long, deletionType: String)
    
    /**
     * Restore a note from the recycle bin
     * @param noteId The ID of the note to restore
     */
    @Query("UPDATE notes SET is_deleted = 0, deleted_date = NULL, deletion_type = NULL WHERE id = :noteId")
    suspend fun restoreNote(noteId: Int)
    
    /**
     * Permanently delete a note (hard delete)
     */
    @Delete
    suspend fun permanentlyDeleteNote(note: Note)
}
