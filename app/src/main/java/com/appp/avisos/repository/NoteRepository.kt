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
     * Get count of edit history entries for a specific note
     */
    suspend fun getEditHistoryCount(noteId: Int): Int {
        return editHistoryDao.getEditHistoryCount(noteId)
    }
}
