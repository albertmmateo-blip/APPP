package com.appp.avisos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.appp.avisos.database.AppDatabase
import com.appp.avisos.database.Note
import com.appp.avisos.repository.NoteRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for NoteEditorActivity that manages note creation and editing.
 * Handles saving, updating, and deleting notes.
 */
class NoteEditorViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: NoteRepository
    
    init {
        val noteDao = AppDatabase.getInstance(application).noteDao()
        repository = NoteRepository(noteDao)
    }
    
    /**
     * Save a new note or update an existing one.
     * 
     * @param note The note to save
     * @param onSuccess Callback invoked on successful save
     * @param onError Callback invoked if save fails
     */
    fun saveNote(
        note: Note,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (note.id == 0) {
                    // New note - insert
                    repository.insertNote(note)
                } else {
                    // Existing note - update
                    repository.updateNote(note)
                }
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to save note")
            }
        }
    }
    
    /**
     * Delete a note from the database.
     * 
     * @param note The note to delete
     * @param onSuccess Callback invoked on successful deletion
     * @param onError Callback invoked if deletion fails
     */
    fun deleteNote(
        note: Note,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.deleteNote(note)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to delete note")
            }
        }
    }
}
