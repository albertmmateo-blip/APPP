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
     * Get all notes sorted by modified date descending (newest first)
     */
    @Query("SELECT * FROM notes ORDER BY modified_date DESC")
    fun getAllNotes(): LiveData<List<Note>>
    
    /**
     * Get notes filtered by category, sorted by modified date descending (newest first)
     */
    @Query("SELECT * FROM notes WHERE category = :category ORDER BY modified_date DESC")
    fun getNotesByCategory(category: String): LiveData<List<Note>>
}
