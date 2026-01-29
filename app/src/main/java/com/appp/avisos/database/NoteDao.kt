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
     */
    @Query("SELECT * FROM notes ORDER BY is_urgent DESC, modified_date DESC")
    fun getAllNotes(): LiveData<List<Note>>
    
    /**
     * Get notes filtered by category, sorted by urgent flag (urgent first), then modified date descending (newest first)
     */
    @Query("SELECT * FROM notes WHERE category = :category ORDER BY is_urgent DESC, modified_date DESC")
    fun getNotesByCategory(category: String): LiveData<List<Note>>
    
    /**
     * Get a single note by its ID
     */
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Note?
    
    /**
     * Get count of notes in a specific category
     */
    @Query("SELECT COUNT(*) FROM notes WHERE category = :category")
    fun getNoteCountByCategory(category: String): LiveData<Int>
}
