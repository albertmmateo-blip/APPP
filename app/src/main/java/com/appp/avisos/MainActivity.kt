package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appp.avisos.adapter.NotesAdapter
import com.appp.avisos.database.Note
import com.appp.avisos.databinding.ActivityMainBinding
import com.appp.avisos.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter
    
    // Categories corresponding to tab positions
    private val categories = arrayOf("Trucar", "Encarregar", "Factures", "Notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        
        // Set up tabs with icons and text labels
        setupTabs()
        
        // Set up RecyclerView with adapter
        setupRecyclerView()
        
        // Set up tab selection listener
        setupTabListener()
        
        // Set up FAB click listener
        setupFab()
        
        // Observe LiveData from ViewModel
        observeNotes()
    }
    
    /**
     * Configure TabLayout with 4 category tabs
     * Each tab includes both an icon and text label
     */
    private fun setupTabs() {
        // Tab 1: Trucar (Call)
        binding.tabLayout.addTab(
            binding.tabLayout.newTab()
                .setText(R.string.category_trucar)
                .setIcon(R.drawable.ic_phone)
        )
        
        // Tab 2: Encarregar (Order)
        binding.tabLayout.addTab(
            binding.tabLayout.newTab()
                .setText(R.string.category_encarregar)
                .setIcon(R.drawable.ic_shopping_cart)
        )
        
        // Tab 3: Factures (Invoices)
        binding.tabLayout.addTab(
            binding.tabLayout.newTab()
                .setText(R.string.category_factures)
                .setIcon(R.drawable.ic_receipt)
        )
        
        // Tab 4: Notes
        binding.tabLayout.addTab(
            binding.tabLayout.newTab()
                .setText(R.string.category_notes)
                .setIcon(R.drawable.ic_note)
        )
        
        // Don't select any tab initially - this will show all notes
        // Tab selection will be handled by user interaction
    }
    
    /**
     * Set up RecyclerView with NotesAdapter
     */
    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter { note ->
            // Handle note click - open editor in edit mode
            openNoteEditor(note)
        }
        binding.recyclerViewNotes.adapter = notesAdapter
    }
    
    /**
     * Set up tab selection listener to filter notes by category
     */
    private fun setupTabListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    // Filter notes by selected category
                    val category = categories[position]
                    viewModel.setSelectedCategory(category)
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // No action needed
            }
            
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No action needed
            }
        })
    }
    
    /**
     * Set up FAB to open NoteEditorActivity for creating new notes
     */
    private fun setupFab() {
        binding.fabAddNote.setOnClickListener {
            openNoteEditor(null)
        }
    }
    
    /**
     * Observe notes LiveData from ViewModel and update UI
     */
    private fun observeNotes() {
        viewModel.notes.observe(this) { notes ->
            // Update adapter with new list
            notesAdapter.submitList(notes)
            
            // Handle empty state
            handleEmptyState(notes.isEmpty())
        }
    }
    
    /**
     * Show or hide empty state message
     * 
     * @param isEmpty true if the notes list is empty
     */
    private fun handleEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerViewNotes.visibility = View.GONE
            // TODO: Show empty state view when implemented
            // For now, just hide the RecyclerView
        } else {
            binding.recyclerViewNotes.visibility = View.VISIBLE
        }
    }
    
    /**
     * Open NoteEditorActivity to create a new note or edit an existing one
     * 
     * @param note The note to edit, or null to create a new note
     */
    private fun openNoteEditor(note: Note?) {
        val intent = Intent(this, NoteEditorActivity::class.java)
        
        if (note != null) {
            // Edit mode - pass note data
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_ID, note.id)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_NAME, note.name)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_BODY, note.body)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_CONTACT, note.contact)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_CATEGORY, note.category)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_CREATED_DATE, note.createdDate)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_MODIFIED_DATE, note.modifiedDate)
        }
        
        startActivity(intent)
    }
}
