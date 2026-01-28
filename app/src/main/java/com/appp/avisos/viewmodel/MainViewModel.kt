package com.appp.avisos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
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
     * Uses switchMap extension function to switch between different LiveData sources
     * based on the selected category.
     */
    val notes: LiveData<List<Note>>
    
    init {
        // Initialize repository with database instance
        val noteDao = AppDatabase.getInstance(application).noteDao()
        repository = NoteRepository(noteDao)
        
        // Set up transformation to switch between all notes and filtered notes
        notes = _selectedCategory.switchMap { category ->
            if (category == null) {
                // No category selected - show all notes
                repository.getAllNotes()
            } else {
                // Category selected - show filtered notes
                repository.getNotesByCategory(category)
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
