package com.appp.avisos.repository

import androidx.lifecycle.LiveData
import com.appp.avisos.database.Note
import com.appp.avisos.database.NoteDao
import com.appp.avisos.database.NoteEditHistory
import com.appp.avisos.database.NoteEditHistoryDao

/**
 * Repository that provides a clean API for data access to the rest of the application.
 * This class abstracts the data sources (in this case, Room database).
 * Room's suspend functions are main-safe and handle threading automatically.
 */
class NoteRepository(
    private val noteDao: NoteDao,
    private val editHistoryDao: NoteEditHistoryDao
) {
    
    /**
     * Get all notes sorted by modified date (newest first).
     * Returns LiveData that can be observed by the UI.
     */
    fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getAllNotes()
    }
    
    /**
     * Get notes filtered by category, sorted by modified date (newest first).
     * Returns LiveData that can be observed by the UI.
     */
    fun getNotesByCategory(category: String): LiveData<List<Note>> {
        return noteDao.getNotesByCategory(category)
    }
    
    /**
     * Insert a new note into the database.
     * Room automatically handles background execution for suspend functions.
     * 
     * @param note The note to insert
     * @return The ID of the newly inserted note
     */
    suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note)
    }
    
    /**
     * Update an existing note in the database.
     * Room automatically handles background execution for suspend functions.
     * 
     * @param note The note to update
     */
    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }
    
    /**
     * Delete a note from the database.
     * Room automatically handles background execution for suspend functions.
     * 
     * @param note The note to delete
     */
    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
    
    /**
     * Get a single note by its ID.
     * Room automatically handles background execution for suspend functions.
     * 
     * @param noteId The ID of the note to retrieve
     * @return The note if found, null otherwise
     */
    suspend fun getNoteById(noteId: Int): Note? {
        return noteDao.getNoteById(noteId)
    }
    
    // Recycle Bin operations
    
    /**
     * Soft delete a note by moving it to the recycle bin
     * 
     * @param noteId The ID of the note to delete
     * @param deletionType The type of deletion (Esborrades or Finalitzades)
     */
    suspend fun softDeleteNote(noteId: Int, deletionType: String) {
        val deletedDate = System.currentTimeMillis()
        noteDao.softDeleteNote(noteId, deletedDate, deletionType)
    }
    
    /**
     * Get all deleted notes
     */
    fun getDeletedNotes(): LiveData<List<Note>> {
        return noteDao.getDeletedNotes()
    }
    
    /**
     * Get deleted notes filtered by type
     * 
     * @param type The deletion type (Esborrades or Finalitzades)
     */
    fun getDeletedNotesByType(type: String): LiveData<List<Note>> {
        return noteDao.getDeletedNotesByType(type)
    }
    
    /**
     * Restore a note from the recycle bin
     * 
     * @param noteId The ID of the note to restore
     */
    suspend fun restoreNote(noteId: Int) {
        noteDao.restoreNote(noteId)
    }
    
    /**
     * Permanently delete a note (hard delete)
     * 
     * @param note The note to permanently delete
     */
    suspend fun permanentlyDeleteNote(note: Note) {
        // Delete edit history first
        noteDao.permanentlyDeleteNote(note)
    }
    
    /**
     * Clean up notes that have been in the recycle bin for more than the specified days
     * 
     * @param daysThreshold Number of days after which notes should be permanently deleted
     */
    suspend fun cleanupExpiredNotes(daysThreshold: Int = Note.RECYCLE_BIN_RETENTION_DAYS) {
        val thresholdTimestamp = System.currentTimeMillis() - (daysThreshold * Note.MILLIS_PER_DAY)
        // Use batch delete operation for efficiency
        noteDao.deleteExpiredNotes(thresholdTimestamp)
    }
    
    /**
     * Insert a new edit history entry
     */
    suspend fun insertEditHistory(history: NoteEditHistory): Long {
        return editHistoryDao.insertEditHistory(history)
    }
    
    /**
     * Get edit history for a specific note
     */
    fun getEditHistoryForNote(noteId: Int): LiveData<List<NoteEditHistory>> {
        return editHistoryDao.getEditHistoryForNote(noteId)
    }
    
    /**
     * Get edit history for a specific note filtered by user
     */
    fun getEditHistoryForNoteByUser(noteId: Int, username: String): LiveData<List<NoteEditHistory>> {
        return editHistoryDao.getEditHistoryForNoteByUser(noteId, username)
    }
    
    /**
     * Get edit history for a specific note within a date range
     */
    fun getEditHistoryForNoteDateRange(noteId: Int, startDate: Long, endDate: Long): LiveData<List<NoteEditHistory>> {
        return editHistoryDao.getEditHistoryForNoteDateRange(noteId, startDate, endDate)
    }
    
    /**
     * Get edit history for a specific note filtered by user and date range
     */
    fun getEditHistoryForNoteByUserAndDateRange(noteId: Int, username: String, startDate: Long, endDate: Long): LiveData<List<NoteEditHistory>> {
        return editHistoryDao.getEditHistoryForNoteByUserAndDateRange(noteId, username, startDate, endDate)
    }
    
    /**
     * Get count of edit history entries for a specific note
     */
    suspend fun getEditHistoryCount(noteId: Int): Int {
        return editHistoryDao.getEditHistoryCount(noteId)
    }
    
    /**
     * Get all distinct users who have modified a specific note
     */
    suspend fun getDistinctModifiersForNote(noteId: Int): List<String> {
        return editHistoryDao.getDistinctModifiersForNote(noteId)
    }
    
    /**
     * Get the maximum edition number for a specific note
     */
    suspend fun getMaxEditionNumber(noteId: Int): Int {
        return editHistoryDao.getMaxEditionNumber(noteId)
    }
    
    /**
     * Get all editions for a note (grouped by edition number)
     */
    fun getEditionsForNote(noteId: Int): LiveData<List<NoteEditHistory>> {
        return editHistoryDao.getEditionsForNote(noteId)
    }
    
    /**
     * Get all changes for a specific edition
     */
    fun getChangesForEdition(noteId: Int, editionNumber: Int): LiveData<List<NoteEditHistory>> {
        return editHistoryDao.getChangesForEdition(noteId, editionNumber)
    }
    
    /**
     * Get count of notes in a specific category.
     * Returns LiveData that can be observed by the UI.
     * 
     * @param category The category name to count notes for
     * @return LiveData containing the count of notes in the specified category
     */
    fun getNoteCountByCategory(category: String): LiveData<Int> {
        return noteDao.getNoteCountByCategory(category)
    }
    
    /**
     * Get notes filtered by category and subcategory.
     * Returns LiveData that can be observed by the UI.
     * 
     * @param category The main category (e.g., "Factures")
     * @param subcategory The subcategory (e.g., "Passades", "Per passar", "Per pagar", "Per cobrar")
     * @return LiveData containing the list of notes matching the category and subcategory
     */
    fun getNotesByCategoryAndSubcategory(category: String, subcategory: String): LiveData<List<Note>> {
        return noteDao.getNotesByCategoryAndSubcategory(category, subcategory)
    }
}
