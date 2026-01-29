package com.appp.avisos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appp.avisos.UserSessionManager
import com.appp.avisos.database.AppDatabase
import com.appp.avisos.database.Note
import com.appp.avisos.database.NoteEditHistory
import com.appp.avisos.repository.NoteRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for NoteEditorActivity that manages note creation and editing.
 * Handles loading, saving, updating, and deleting notes with validation.
 */
class NoteEditorViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: NoteRepository
    private val userSessionManager: UserSessionManager
    
    // LiveData for the currently loaded note
    private val _currentNote = MutableLiveData<Note?>()
    val currentNote: LiveData<Note?> = _currentNote
    
    init {
        val database = AppDatabase.getInstance(application)
        val noteDao = database.noteDao()
        val editHistoryDao = database.noteEditHistoryDao()
        repository = NoteRepository(noteDao, editHistoryDao)
        userSessionManager = UserSessionManager(application)
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
     * Automatically sets timestamps correctly and records edit history for updates.
     * 
     * @param name The note name
     * @param body The note body
     * @param contact Optional contact information
     * @param category The note category
     * @param isUrgent Whether the note is marked as urgent
     * @param author The username of the note author (for new notes only)
     * @param onSuccess Callback invoked on successful save
     * @param onError Callback invoked if save fails
     */
    fun saveNote(
        name: String,
        body: String,
        contact: String?,
        category: String,
        subcategory: String? = null,
        isUrgent: Boolean = false,
        author: String? = null,
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
                        subcategory = subcategory,
                        modifiedDate = currentTime,
                        isUrgent = isUrgent
                    )
                } else {
                    // Create new note - set both createdDate and modifiedDate
                    Note(
                        name = name.trim(),
                        body = body.trim(),
                        contact = contact?.trim()?.ifEmpty { null },
                        category = category,
                        subcategory = subcategory,
                        createdDate = currentTime,
                        modifiedDate = currentTime,
                        isUrgent = isUrgent,
                        author = author
                    )
                }
                
                if (existingNote != null) {
                    // Track changes for edit history
                    recordEditHistory(existingNote, note, currentTime)
                    
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
     * Record edit history for changed fields
     */
    private suspend fun recordEditHistory(oldNote: Note, newNote: Note, timestamp: Long) {
        val currentUser = userSessionManager.getCurrentUser()
        
        // Track name changes
        if (oldNote.name != newNote.name) {
            repository.insertEditHistory(
                NoteEditHistory(
                    noteId = oldNote.id,
                    fieldName = "Note Name",
                    oldValue = oldNote.name,
                    newValue = newNote.name,
                    timestamp = timestamp,
                    modifiedBy = currentUser
                )
            )
        }
        
        // Track body changes
        if (oldNote.body != newNote.body) {
            repository.insertEditHistory(
                NoteEditHistory(
                    noteId = oldNote.id,
                    fieldName = "Note Body",
                    oldValue = oldNote.body,
                    newValue = newNote.body,
                    timestamp = timestamp,
                    modifiedBy = currentUser
                )
            )
        }
        
        // Track contact changes
        if (oldNote.contact != newNote.contact) {
            repository.insertEditHistory(
                NoteEditHistory(
                    noteId = oldNote.id,
                    fieldName = "Contact",
                    oldValue = oldNote.contact,
                    newValue = newNote.contact,
                    timestamp = timestamp,
                    modifiedBy = currentUser
                )
            )
        }
        
        // Track category changes
        if (oldNote.category != newNote.category) {
            repository.insertEditHistory(
                NoteEditHistory(
                    noteId = oldNote.id,
                    fieldName = "Category",
                    oldValue = oldNote.category,
                    newValue = newNote.category,
                    timestamp = timestamp,
                    modifiedBy = currentUser
                )
            )
        }
        
        // Track urgent flag changes
        if (oldNote.isUrgent != newNote.isUrgent) {
            repository.insertEditHistory(
                NoteEditHistory(
                    noteId = oldNote.id,
                    fieldName = "Urgent",
                    oldValue = if (oldNote.isUrgent) "Yes" else "No",
                    newValue = if (newNote.isUrgent) "Yes" else "No",
                    timestamp = timestamp,
                    modifiedBy = currentUser
                )
            )
        }
    }
    
    /**
     * Delete the currently loaded note by moving it to the recycle bin (soft delete).
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
                // Soft delete - move to recycle bin with type "Esborrades"
                repository.softDeleteNote(note.id, Note.DELETION_TYPE_ESBORRADES)
                _currentNote.value = null
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to delete note")
            }
        }
    }
    
    /**
     * Finalize the currently loaded note by moving it to the recycle bin.
     * Used when a note's tasks are completed.
     * 
     * @param onSuccess Callback invoked on successful finalization
     * @param onError Callback invoked if finalization fails
     */
    fun finalizeNote(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val note = _currentNote.value
        if (note == null) {
            onError("No note to finalize")
            return
        }
        
        viewModelScope.launch {
            try {
                // Soft delete - move to recycle bin with type "Finalitzades"
                repository.softDeleteNote(note.id, Note.DELETION_TYPE_FINALITZADES)
                _currentNote.value = null
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to finalize note")
            }
        }
    }
    
    /**
     * Get edit history for the currently loaded note
     */
    fun getEditHistory(): LiveData<List<NoteEditHistory>>? {
        val noteId = _currentNote.value?.id
        return if (noteId != null && noteId != 0) {
            repository.getEditHistoryForNote(noteId)
        } else {
            null
        }
    }
}
