package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appp.avisos.adapter.EditHistoryAdapter
import com.appp.avisos.databinding.ActivityNoteDetailBinding
import com.appp.avisos.viewmodel.NoteEditorViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Activity for viewing note details in read-only mode.
 * Users can tap the "Edita" button to switch to edit mode.
 */
class NoteDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityNoteDetailBinding
    private val viewModel: NoteEditorViewModel by viewModels()
    private lateinit var editHistoryAdapter: EditHistoryAdapter
    private var isEditHistoryExpanded = false
    
    companion object {
        const val EXTRA_NOTE_ID = "note_id"
        const val EXTRA_CURRENT_CATEGORY = "current_category"
        private const val STATE_HISTORY_EXPANDED = "state_history_expanded"
        
        private val dateFormatter: DateTimeFormatter = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Restore expanded state if available
        isEditHistoryExpanded = savedInstanceState?.getBoolean(STATE_HISTORY_EXPANDED, false) ?: false
        
        // Set up edit history RecyclerView
        setupEditHistoryRecyclerView()
        
        // Set up edit history observer (do this once in onCreate)
        setupEditHistoryObserver()
        
        // Load note if ID is provided
        loadNoteFromIntent()
        
        // Set up button listeners
        setupButtonListeners()
        
        // Restore UI state if needed
        if (isEditHistoryExpanded) {
            // UI will be updated when observer receives data
            updateToggleButtonState()
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_HISTORY_EXPANDED, isEditHistoryExpanded)
    }
    
    /**
     * Set up the edit history RecyclerView with adapter
     */
    private fun setupEditHistoryRecyclerView() {
        editHistoryAdapter = EditHistoryAdapter()
        binding.recyclerViewEditHistory.apply {
            layoutManager = LinearLayoutManager(this@NoteDetailActivity)
            adapter = editHistoryAdapter
        }
    }
    
    /**
     * Load note data from intent extras
     */
    private fun loadNoteFromIntent() {
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0)
        
        if (noteId != 0) {
            viewModel.loadNote(
                id = noteId,
                onSuccess = { note ->
                    // Populate fields in read-only mode
                    binding.textNoteName.text = note.name
                    binding.textNoteBody.text = note.body
                    binding.textContact.text = note.contact ?: getString(R.string.no_contact)
                    binding.textCategory.text = note.category
                    binding.textCreatedDate.text = formatDate(note.createdDate)
                    binding.textModifiedDate.text = formatDate(note.modifiedDate)
                    
                    // Show urgent badge if note is urgent
                    if (note.isUrgent) {
                        binding.layoutUrgentSection.visibility = View.VISIBLE
                    } else {
                        binding.layoutUrgentSection.visibility = View.GONE
                    }
                },
                onError = { error ->
                    Toast.makeText(this, "Error loading note: $error", Toast.LENGTH_LONG).show()
                    finish()
                }
            )
        } else {
            Toast.makeText(this, "Invalid note ID", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    /**
     * Set up observer for edit history (called once in onCreate)
     */
    private fun setupEditHistoryObserver() {
        val editHistoryLiveData = viewModel.getEditHistory()
        if (editHistoryLiveData != null) {
            editHistoryLiveData.observe(this) { historyList ->
                if (historyList.isNotEmpty()) {
                    // Show edit history section only if there are edits
                    binding.layoutEditHistorySection.visibility = View.VISIBLE
                    editHistoryAdapter.submitList(historyList)
                } else {
                    // Hide edit history section if no edits
                    binding.layoutEditHistorySection.visibility = View.GONE
                }
            }
        }
    }
    
    /**
     * Set up click listeners for buttons
     */
    private fun setupButtonListeners() {
        binding.buttonEdit.setOnClickListener {
            openNoteEditor()
        }
        
        binding.buttonBack.setOnClickListener {
            finish()
        }
        
        binding.buttonToggleEditHistory.setOnClickListener {
            toggleEditHistory()
        }
    }
    
    /**
     * Toggle the visibility of the edit history RecyclerView
     */
    private fun toggleEditHistory() {
        isEditHistoryExpanded = !isEditHistoryExpanded
        updateToggleButtonState()
    }
    
    /**
     * Update the toggle button and RecyclerView based on expanded state
     */
    private fun updateToggleButtonState() {
        if (isEditHistoryExpanded) {
            // Show edit history
            binding.recyclerViewEditHistory.visibility = View.VISIBLE
            binding.buttonToggleEditHistory.text = getString(R.string.button_hide_edit_history)
            binding.buttonToggleEditHistory.icon = getDrawable(android.R.drawable.arrow_up_float)
        } else {
            // Hide edit history
            binding.recyclerViewEditHistory.visibility = View.GONE
            binding.buttonToggleEditHistory.text = getString(R.string.button_view_edit_history)
            binding.buttonToggleEditHistory.icon = getDrawable(android.R.drawable.arrow_down_float)
        }
    }
    
    /**
     * Open NoteEditorActivity in edit mode with current category context
     */
    private fun openNoteEditor() {
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0)
        val currentCategory = intent.getStringExtra(EXTRA_CURRENT_CATEGORY)
        
        val intent = Intent(this, NoteEditorActivity::class.java).apply {
            putExtra(NoteEditorActivity.EXTRA_NOTE_ID, noteId)
            putExtra(NoteEditorActivity.EXTRA_CURRENT_CATEGORY, currentCategory)
        }
        startActivity(intent)
        // Finish this activity so when user saves and goes back, they return to MainActivity
        finish()
    }
    
    /**
     * Format a timestamp to "dd/MM/yyyy HH:mm" format
     */
    private fun formatDate(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        return dateFormatter.format(instant.atZone(ZoneId.systemDefault()))
    }
}
