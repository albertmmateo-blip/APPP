package com.appp.avisos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appp.avisos.database.Note
import com.appp.avisos.databinding.ActivityNoteEditorBinding
import com.appp.avisos.viewmodel.NoteEditorViewModel

/**
 * Activity for creating and editing notes.
 * Supports two modes:
 * - CREATE: Creating a new note
 * - EDIT: Editing an existing note (pass note data via intent extras)
 */
class NoteEditorActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityNoteEditorBinding
    private val viewModel: NoteEditorViewModel by viewModels()
    
    // Note being edited (null for new notes)
    private var editingNote: Note? = null
    
    // Categories for spinner
    private val categories = arrayOf("Trucar", "Encarregar", "Factures", "Notes")
    
    companion object {
        const val EXTRA_NOTE_ID = "note_id"
        const val EXTRA_NOTE_NAME = "note_name"
        const val EXTRA_NOTE_BODY = "note_body"
        const val EXTRA_NOTE_CONTACT = "note_contact"
        const val EXTRA_NOTE_CATEGORY = "note_category"
        const val EXTRA_NOTE_CREATED_DATE = "note_created_date"
        const val EXTRA_NOTE_MODIFIED_DATE = "note_modified_date"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up category spinner
        setupCategorySpinner()
        
        // Load note if editing
        loadNoteFromIntent()
        
        // Set up button listeners
        setupButtonListeners()
    }
    
    /**
     * Set up the category spinner with predefined categories
     */
    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }
    
    /**
     * Load note data from intent extras if in EDIT mode
     */
    private fun loadNoteFromIntent() {
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0)
        
        if (noteId != 0) {
            // EDIT mode - load existing note
            val name = intent.getStringExtra(EXTRA_NOTE_NAME) ?: ""
            val body = intent.getStringExtra(EXTRA_NOTE_BODY) ?: ""
            val contact = intent.getStringExtra(EXTRA_NOTE_CONTACT)
            val category = intent.getStringExtra(EXTRA_NOTE_CATEGORY) ?: categories[0]
            val createdDate = intent.getLongExtra(EXTRA_NOTE_CREATED_DATE, System.currentTimeMillis())
            val modifiedDate = intent.getLongExtra(EXTRA_NOTE_MODIFIED_DATE, System.currentTimeMillis())
            
            editingNote = Note(
                id = noteId,
                name = name,
                body = body,
                contact = contact,
                category = category,
                createdDate = createdDate,
                modifiedDate = modifiedDate
            )
            
            // Populate fields
            binding.editTextNoteName.setText(name)
            binding.editTextNoteBody.setText(body)
            binding.editTextContact.setText(contact)
            
            // Set category in spinner
            val categoryIndex = categories.indexOf(category)
            if (categoryIndex >= 0) {
                binding.spinnerCategory.setSelection(categoryIndex)
            }
            
            // Show delete button for existing notes
            binding.buttonDelete.visibility = View.VISIBLE
        } else {
            // CREATE mode - hide delete button
            binding.buttonDelete.visibility = View.GONE
        }
    }
    
    /**
     * Set up click listeners for all buttons
     */
    private fun setupButtonListeners() {
        binding.buttonSave.setOnClickListener {
            saveNote()
        }
        
        binding.buttonDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
        
        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }
    
    /**
     * Validate and save the note
     */
    private fun saveNote() {
        val name = binding.editTextNoteName.text.toString().trim()
        val body = binding.editTextNoteBody.text.toString().trim()
        val contact = binding.editTextContact.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()
        
        // Validate required fields
        var isValid = true
        
        if (name.isEmpty()) {
            binding.textInputLayoutNoteName.error = getString(R.string.error_empty_name)
            isValid = false
        } else {
            binding.textInputLayoutNoteName.error = null
        }
        
        if (body.isEmpty()) {
            binding.textInputLayoutNoteBody.error = getString(R.string.error_empty_body)
            isValid = false
        } else {
            binding.textInputLayoutNoteBody.error = null
        }
        
        if (!isValid) {
            return
        }
        
        // Create or update note
        val currentTime = System.currentTimeMillis()
        val note = if (editingNote != null) {
            // Update existing note
            editingNote!!.copy(
                name = name,
                body = body,
                contact = contact.ifEmpty { null },
                category = category,
                modifiedDate = currentTime
            )
        } else {
            // Create new note
            Note(
                name = name,
                body = body,
                contact = contact.ifEmpty { null },
                category = category,
                createdDate = currentTime,
                modifiedDate = currentTime
            )
        }
        
        // Save via ViewModel
        viewModel.saveNote(
            note = note,
            onSuccess = {
                Toast.makeText(this, R.string.message_note_saved, Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { error ->
                Toast.makeText(this, getString(R.string.error_save_failed) + ": $error", Toast.LENGTH_LONG).show()
            }
        )
    }
    
    /**
     * Show confirmation dialog before deleting note
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
     * Delete the current note
     */
    private fun deleteNote() {
        editingNote?.let { note ->
            viewModel.deleteNote(
                note = note,
                onSuccess = {
                    Toast.makeText(this, R.string.message_note_deleted, Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = { error ->
                    Toast.makeText(this, getString(R.string.error_delete_failed) + ": $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}
