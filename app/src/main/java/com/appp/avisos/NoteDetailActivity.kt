package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appp.avisos.adapter.EditionHistoryAdapter
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
    private lateinit var editionHistoryAdapter: EditionHistoryAdapter
    private var isEditHistoryExpanded = false
    private var currentNoteId: Int = 0
    
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
        editionHistoryAdapter = EditionHistoryAdapter { editionNumber ->
            // Navigate to EditionDetailActivity when an edition is clicked
            val intent = Intent(this, EditionDetailActivity::class.java).apply {
                putExtra(EditionDetailActivity.EXTRA_NOTE_ID, currentNoteId)
                putExtra(EditionDetailActivity.EXTRA_EDITION_NUMBER, editionNumber)
            }
            startActivity(intent)
        }
        binding.recyclerViewEditHistory.apply {
            layoutManager = LinearLayoutManager(this@NoteDetailActivity)
            adapter = editionHistoryAdapter
        }
    }
    
    /**
     * Load note data from intent extras
     */
    private fun loadNoteFromIntent() {
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0)
        currentNoteId = noteId
        
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
                    
                    // Display author if available
                    if (note.author != null) {
                        binding.layoutAuthorSection.visibility = View.VISIBLE
                        binding.textAuthor.text = note.author
                    } else {
                        binding.layoutAuthorSection.visibility = View.GONE
                    }
                    
                    // Show urgent badge if note is urgent
                    if (note.isUrgent) {
                        binding.layoutUrgentSection.visibility = View.VISIBLE
                    } else {
                        binding.layoutUrgentSection.visibility = View.GONE
                    }
                    
                    // Set the Edit button color based on category
                    setCategoryColor(note.category)
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
        val editionsLiveData = viewModel.getEditions()
        if (editionsLiveData != null) {
            editionsLiveData.observe(this) { editionsList ->
                if (editionsList.isNotEmpty()) {
                    // Show edit history section only if there are editions
                    binding.layoutEditHistorySection.visibility = View.VISIBLE
                    editionHistoryAdapter.submitList(editionsList)
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
        
        // Make Created Date section clickable to toggle edit history
        binding.layoutCreatedDate.setOnClickListener {
            toggleEditHistory()
        }
        
        // Make Modified Date section clickable to toggle edit history
        binding.layoutModifiedDate.setOnClickListener {
            toggleEditHistory()
        }
        
        binding.buttonFinalitza.setOnClickListener {
            showFinalizeConfirmationDialog()
        }
        
        binding.buttonEsborra.setOnClickListener {
            showDeleteConfirmationDialog()
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
    
    /**
     * Set the Edit button background color based on the category
     */
    private fun setCategoryColor(category: String) {
        val colorResId = when (category) {
            getString(R.string.category_trucar) -> R.color.category_trucar
            getString(R.string.category_encarregar) -> R.color.category_encarregar
            getString(R.string.category_factures) -> R.color.category_factures
            getString(R.string.category_notes) -> R.color.category_notes
            else -> R.color.primary
        }
        
        val color = ContextCompat.getColor(this, colorResId)
        binding.buttonEdit.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
    }
    
    /**
     * Show confirmation dialog before finalizing a note
     */
    private fun showFinalizeConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_finalize_title)
            .setMessage(R.string.dialog_finalize_message)
            .setPositiveButton(R.string.button_finalize) { _, _ ->
                finalizeNote()
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }
    
    /**
     * Finalize the note by moving it to the "Finalitzades" recycle bin
     */
    private fun finalizeNote() {
        viewModel.finalizeNote(
            onSuccess = {
                Toast.makeText(this, R.string.message_note_finalized, Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { error ->
                Toast.makeText(this, "${getString(R.string.error_finalize_failed)}: $error", Toast.LENGTH_LONG).show()
            }
        )
    }
    
    /**
     * Show confirmation dialog before deleting a note
     */
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(R.string.dialog_delete_message)
            .setPositiveButton(R.string.button_delete) { _, _ ->
                deleteNote()
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }
    
    /**
     * Delete the note by moving it to the "Esborrades" recycle bin
     */
    private fun deleteNote() {
        viewModel.deleteNote(
            onSuccess = {
                Toast.makeText(this, R.string.message_note_deleted, Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { error ->
                Toast.makeText(this, "${getString(R.string.error_delete_failed)}: $error", Toast.LENGTH_LONG).show()
            }
        )
    }
}
