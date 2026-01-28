package com.appp.avisos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appp.avisos.database.AppDatabase
import com.appp.avisos.database.Note
import com.appp.avisos.repository.NoteRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for NoteEditorActivity that manages note creation and editing.
 * Handles loading, saving, updating, and deleting notes with validation.
 */
class NoteEditorViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: NoteRepository
    
    // LiveData for the currently loaded note
    private val _currentNote = MutableLiveData<Note?>()
    val currentNote: LiveData<Note?> = _currentNote
    
    init {
        val noteDao = AppDatabase.getInstance(application).noteDao()
        repository = NoteRepository(noteDao)
    }
    
    /**
     * Load a note by its ID for editing.
     * 
     * @param id The ID of the note to load
     * @param onSuccess Callback invoked with the loaded note
     * @param onError Callback invoked if loading fails (e.g., note not found)
     */
    fun loadNote(
        id: Int,
        onSuccess: (Note) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val note = repository.getNoteById(id)
                if (note != null) {
                    _currentNote.value = note
                    onSuccess(note)
                } else {
                    onError("Note not found")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to load note")
            }
        }
    }
    
    /**
     * Validate input fields for a note.
     * 
     * @param name The note name
     * @param body The note body
     * @return Pair of (isValid, errorMessage). If valid, errorMessage is null.
     */
    fun validateInput(name: String, body: String): Pair<Boolean, String?> {
        return when {
            name.trim().isEmpty() -> Pair(false, "Name cannot be empty")
            body.trim().isEmpty() -> Pair(false, "Body cannot be empty")
            else -> Pair(true, null)
        }
    }
    
    /**
     * Save a note with the provided fields.
     * Creates a new note if no note is currently loaded, otherwise updates the existing note.
     * Automatically sets timestamps correctly.
     * 
     * @param name The note name
     * @param body The note body
     * @param contact Optional contact information
     * @param category The note category
     * @param onSuccess Callback invoked on successful save
     * @param onError Callback invoked if save fails
     */
    fun saveNote(
        name: String,
        body: String,
        contact: String?,
        category: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Validate input
        val (isValid, errorMessage) = validateInput(name, body)
        if (!isValid) {
            onError(errorMessage ?: "Validation failed")
            return
        }
        
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                val existingNote = _currentNote.value
                
                val note = if (existingNote != null) {
                    // Update existing note - preserve createdDate, update modifiedDate
                    existingNote.copy(
                        name = name.trim(),
                        body = body.trim(),
                        contact = contact?.trim()?.ifEmpty { null },
                        category = category,
                        modifiedDate = currentTime
                    )
                } else {
                    // Create new note - set both createdDate and modifiedDate
                    Note(
                        name = name.trim(),
                        body = body.trim(),
                        contact = contact?.trim()?.ifEmpty { null },
                        category = category,
                        createdDate = currentTime,
                        modifiedDate = currentTime
                    )
                }
                
                if (existingNote != null) {
                    repository.updateNote(note)
                    _currentNote.value = note
                } else {
                    // Insert and capture the returned ID
                    val insertedId = repository.insertNote(note)
                    // Update the current note with the actual database ID
                    _currentNote.value = note.copy(id = insertedId.toInt())
                }
                
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to save note")
            }
        }
    }
    
    /**
     * Delete the currently loaded note.
     * 
     * @param onSuccess Callback invoked on successful deletion
     * @param onError Callback invoked if deletion fails
     */
    fun deleteNote(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val note = _currentNote.value
        if (note == null) {
            onError("No note to delete")
            return
        }
        
        viewModelScope.launch {
            try {
                repository.deleteNote(note)
                _currentNote.value = null
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to delete note")
            }
        }
    }
}
