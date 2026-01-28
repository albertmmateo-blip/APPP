package com.appp.avisos.repository

import androidx.lifecycle.LiveData
import com.appp.avisos.database.Note
import com.appp.avisos.database.NoteDao

/**
 * Repository that provides a clean API for data access to the rest of the application.
 * This class abstracts the data sources (in this case, Room database).
 * Room's suspend functions are main-safe and handle threading automatically.
 */
class NoteRepository(private val noteDao: NoteDao) {
    
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
}
