package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appp.avisos.adapter.NotesAdapter
import com.appp.avisos.database.Note
import com.appp.avisos.databinding.ActivityVendaSubcategoryDetailBinding
import com.appp.avisos.viewmodel.MainViewModel

/**
 * Activity that displays notes for a specific Venda subcategory.
 * Opened when Pedro taps on a subcategory button.
 */
class VendaSubcategoryDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVendaSubcategoryDetailBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter
    private var subcategory: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendaSubcategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get subcategory from intent
        subcategory = intent.getStringExtra("subcategory")
        
        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = subcategory ?: "Venda"
        
        // Set up RecyclerView
        setupRecyclerView()
        
        // Observe notes for this subcategory
        observeNotes()
        
        // Set up FAB to create new note
        binding.fabAddNote.setOnClickListener {
            openNoteEditor(null)
        }
    }
    
    /**
     * Set up RecyclerView with adapter
     */
    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter { note ->
            openNoteDetail(note)
        }
        binding.recyclerViewNotes.apply {
            layoutManager = LinearLayoutManager(this@VendaSubcategoryDetailActivity)
            adapter = notesAdapter
        }
    }
    
    /**
     * Observe notes for the subcategory
     */
    private fun observeNotes() {
        subcategory?.let { sub ->
            viewModel.getNotesByCategoryAndSubcategory("Venda", sub).observe(this) { notes ->
                notesAdapter.submitList(notes)
            }
        }
    }
    
    /**
     * Open note detail activity
     */
    private fun openNoteDetail(note: Note) {
        val intent = Intent(this, NoteDetailActivity::class.java).apply {
            putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
            putExtra(NoteDetailActivity.EXTRA_CURRENT_CATEGORY, "Venda|$subcategory")
        }
        startActivity(intent)
    }
    
    /**
     * Open note editor to create a new note
     */
    private fun openNoteEditor(note: Note?) {
        val intent = Intent(this, NoteEditorActivity::class.java)
        
        if (note != null) {
            // Edit mode
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_ID, note.id)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_NAME, note.name)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_BODY, note.body)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_CONTACT, note.contact)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_CATEGORY, note.category)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_CREATED_DATE, note.createdDate)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_MODIFIED_DATE, note.modifiedDate)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_IS_URGENT, note.isUrgent)
        }
        
        // Pass current category with subcategory
        intent.putExtra(NoteEditorActivity.EXTRA_CURRENT_CATEGORY, "Venda|$subcategory")
        
        startActivity(intent)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
