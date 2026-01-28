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
    val notes: LiveData<List<Note>> = MediatorLiveData()
    
    // Keep track of currently observed LiveData source
    private var currentNotesSource: LiveData<List<Note>>? = null
    private var currentCategory: String? = null
    
    init {
        // Initialize repository with database instance
        val noteDao = AppDatabase.getInstance(application).noteDao()
        repository = NoteRepository(noteDao)
        
        // Set up MediatorLiveData to react to category changes
        (notes as MediatorLiveData).addSource(_selectedCategory) { category ->
            // Only update if category actually changed
            if (currentCategory != category) {
                currentCategory = category
                
                // Remove previous source if it exists
                currentNotesSource?.let { (notes as MediatorLiveData).removeSource(it) }
                
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
                (notes as MediatorLiveData).addSource(newSource) { notesList ->
                    (notes as MediatorLiveData).value = notesList
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
