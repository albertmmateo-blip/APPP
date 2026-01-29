package com.appp.avisos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.appp.avisos.database.AppDatabase
import com.appp.avisos.database.Note
import com.appp.avisos.repository.NoteRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for RecycleBinActivity that manages deleted notes.
 * Handles viewing, restoring, and permanently deleting notes from the recycle bin.
 */
class RecycleBinViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: NoteRepository
    
    // LiveData for deleted notes by type
    val esborradesNotes: LiveData<List<Note>>
    val finalitzadesNotes: LiveData<List<Note>>
    val allDeletedNotes: LiveData<List<Note>>
    
    init {
        val database = AppDatabase.getInstance(application)
        val noteDao = database.noteDao()
        val editHistoryDao = database.noteEditHistoryDao()
        repository = NoteRepository(noteDao, editHistoryDao)
        
        // Initialize LiveData streams for each deletion type
        esborradesNotes = repository.getDeletedNotesByType(Note.DELETION_TYPE_ESBORRADES)
        finalitzadesNotes = repository.getDeletedNotesByType(Note.DELETION_TYPE_FINALITZADES)
        allDeletedNotes = repository.getDeletedNotes()
    }
    
    /**
     * Restore a note from the recycle bin back to active notes.
     * 
     * @param noteId The ID of the note to restore
     * @param onSuccess Callback invoked on successful restoration
     * @param onError Callback invoked if restoration fails
     */
    fun restoreNote(
        noteId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.restoreNote(noteId)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to restore note")
            }
        }
    }
    
    /**
     * Permanently delete a note from the recycle bin.
     * This action cannot be undone.
     * 
     * @param note The note to permanently delete
     * @param onSuccess Callback invoked on successful deletion
     * @param onError Callback invoked if deletion fails
     */
    fun permanentlyDeleteNote(
        note: Note,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.permanentlyDeleteNote(note)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to permanently delete note")
            }
        }
    }
    
    /**
     * Clean up expired notes from the recycle bin.
     * Deletes notes that have been in the recycle bin for more than 15 days.
     * 
     * @param onSuccess Callback invoked on successful cleanup
     * @param onError Callback invoked if cleanup fails
     */
    fun cleanupExpiredNotes(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.cleanupExpiredNotes(daysThreshold = 15)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to cleanup expired notes")
            }
        }
    }
}
