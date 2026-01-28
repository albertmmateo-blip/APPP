package com.appp.avisos.repository

import androidx.lifecycle.LiveData
import com.appp.avisos.database.Note
import com.appp.avisos.database.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository that provides a clean API for data access to the rest of the application.
 * This class abstracts the data sources (in this case, Room database) and handles
 * background thread operations.
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
     * Runs on IO dispatcher for background execution.
     * 
     * @param note The note to insert
     * @return The ID of the newly inserted note
     */
    suspend fun insertNote(note: Note): Long {
        return withContext(Dispatchers.IO) {
            noteDao.insertNote(note)
        }
    }
    
    /**
     * Update an existing note in the database.
     * Runs on IO dispatcher for background execution.
     * 
     * @param note The note to update
     */
    suspend fun updateNote(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.updateNote(note)
        }
    }
    
    /**
     * Delete a note from the database.
     * Runs on IO dispatcher for background execution.
     * 
     * @param note The note to delete
     */
    suspend fun deleteNote(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.deleteNote(note)
        }
    }
}
