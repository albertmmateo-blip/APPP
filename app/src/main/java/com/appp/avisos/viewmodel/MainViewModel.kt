package com.appp.avisos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.appp.avisos.database.AppDatabase
import com.appp.avisos.database.Note
import com.appp.avisos.repository.NoteRepository

/**
 * ViewModel for MainActivity that manages the list of notes.
 * Survives configuration changes and provides filtered notes based on selected category.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: NoteRepository
    
    // MutableLiveData to track the currently selected category
    private val _selectedCategory = MutableLiveData<String?>()
    
    /**
     * LiveData of notes that automatically updates when the selected category changes.
     * Uses MediatorLiveData for reactive filtering by observing both the category
     * and the appropriate notes source.
     */
    private val _notes: MediatorLiveData<List<Note>> = MediatorLiveData()
    val notes: LiveData<List<Note>> = _notes
    
    // Keep track of currently observed LiveData source
    private var currentNotesSource: LiveData<List<Note>>? = null
    private var currentCategory: String? = null
    
    // LiveData for note counts per category (initialized after repository)
    val trucarCount: LiveData<Int>
    val encarregarCount: LiveData<Int>
    val facturesCount: LiveData<Int>
    val notesCount: LiveData<Int>
    
    // LiveData for notes by category (for fragments, initialized after repository)
    val trucarNotes: LiveData<List<Note>>
    val encarregarNotes: LiveData<List<Note>>
    val facturesNotes: LiveData<List<Note>>
    val generalNotes: LiveData<List<Note>>
    
    init {
        // Initialize repository with database instance
        val database = AppDatabase.getInstance(application)
        val noteDao = database.noteDao()
        val editHistoryDao = database.noteEditHistoryDao()
        repository = NoteRepository(noteDao, editHistoryDao)
        
        // Initialize LiveData properties after repository
        trucarCount = repository.getNoteCountByCategory("Trucar")
        encarregarCount = repository.getNoteCountByCategory("Encarregar")
        facturesCount = repository.getNoteCountByCategory("Factures")
        notesCount = repository.getNoteCountByCategory("Notes")
        
        trucarNotes = repository.getNotesByCategory("Trucar")
        encarregarNotes = repository.getNotesByCategory("Encarregar")
        facturesNotes = repository.getNotesByCategory("Factures")
        generalNotes = repository.getNotesByCategory("Notes")
        
        // Set up MediatorLiveData to react to category changes
        _notes.addSource(_selectedCategory) { category ->
            // Only update if category actually changed
            if (currentCategory != category) {
                currentCategory = category
                
                // Remove previous source if it exists
                currentNotesSource?.let { _notes.removeSource(it) }
                
                // Determine which notes source to observe based on the category
                val newSource = if (category == null) {
                    // No category selected - show all notes
                    repository.getAllNotes()
                } else {
                    // Category selected - show filtered notes
                    repository.getNotesByCategory(category)
                }
                
                // Add the new source and update currentNotesSource
                currentNotesSource = newSource
                _notes.addSource(newSource) { notesList ->
                    _notes.value = notesList
                }
            }
        }
        
        // Initialize with no filter (show all notes)
        _selectedCategory.value = null
    }
    
    /**
     * Update the selected category filter.
     * Pass null to show all notes.
     * 
     * @param category The category to filter by, or null for all notes
     */
    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }
}
