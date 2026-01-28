package com.appp.avisos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
            // EDIT mode - load note from database via ViewModel
            viewModel.loadNote(
                id = noteId,
                onSuccess = { note ->
                    // Populate fields
                    binding.editTextNoteName.setText(note.name)
                    binding.editTextNoteBody.setText(note.body)
                    binding.editTextContact.setText(note.contact)
                    
                    // Set category in spinner
                    val categoryIndex = categories.indexOf(note.category)
                    if (categoryIndex >= 0) {
                        binding.spinnerCategory.setSelection(categoryIndex)
                    }
                    
                    // Show delete button for existing notes
                    binding.buttonDelete.visibility = View.VISIBLE
                },
                onError = { error ->
                    Toast.makeText(this, "Error loading note: $error", Toast.LENGTH_LONG).show()
                    finish()
                }
            )
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
        val name = binding.editTextNoteName.text.toString()
        val body = binding.editTextNoteBody.text.toString()
        val contact = binding.editTextContact.text.toString()
        val category = binding.spinnerCategory.selectedItem.toString()
        
        // Clear previous errors
        binding.textInputLayoutNoteName.error = null
        binding.textInputLayoutNoteBody.error = null
        
        // Save via ViewModel (validation happens in ViewModel)
        viewModel.saveNote(
            name = name,
            body = body,
            contact = contact,
            category = category,
            onSuccess = {
                Toast.makeText(this, R.string.message_note_saved, Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { error ->
                // Handle validation errors
                when {
                    error.contains("Name") -> {
                        binding.textInputLayoutNoteName.error = error
                    }
                    error.contains("Body") -> {
                        binding.textInputLayoutNoteBody.error = error
                    }
                    else -> {
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
    
    /**
     * Show confirmation dialog before deleting note
     */
    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(R.string.dialog_delete_message)
            .setPositiveButton(R.string.button_delete) { _, _ ->
                deleteNote()
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
            .apply {
                // Make the positive (DELETE) button red using theme's error color
                getButton(android.content.DialogInterface.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(context, R.color.error))
            }
    }
    
    /**
     * Delete the current note
     */
    private fun deleteNote() {
        viewModel.deleteNote(
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
