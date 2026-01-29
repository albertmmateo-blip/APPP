package com.appp.avisos

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appp.avisos.adapter.EditHistoryAdapter
import com.appp.avisos.databinding.ActivityEditionDetailBinding
import com.appp.avisos.viewmodel.NoteEditorViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Activity for viewing detailed changes within a specific edition
 */
class EditionDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityEditionDetailBinding
    private val viewModel: NoteEditorViewModel by viewModels()
    private lateinit var changesAdapter: EditHistoryAdapter
    
    companion object {
        const val EXTRA_NOTE_ID = "note_id"
        const val EXTRA_EDITION_NUMBER = "edition_number"
        
        private val dateFormatter: DateTimeFormatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up changes RecyclerView
        setupChangesRecyclerView()
        
        // Load note and edition data
        loadEditionDetails()
        
        // Set up button listeners
        setupButtonListeners()
    }
    
    /**
     * Set up the changes RecyclerView with adapter
     */
    private fun setupChangesRecyclerView() {
        changesAdapter = EditHistoryAdapter()
        binding.recyclerViewChanges.apply {
            layoutManager = LinearLayoutManager(this@EditionDetailActivity)
            adapter = changesAdapter
        }
    }
    
    /**
     * Load edition details from intent extras
     */
    private fun loadEditionDetails() {
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0)
        val editionNumber = intent.getIntExtra(EXTRA_EDITION_NUMBER, 0)
        
        if (noteId != 0 && editionNumber != 0) {
            // Load the note to get context
            viewModel.loadNote(
                id = noteId,
                onSuccess = { note ->
                    // Set up title
                    title = getString(R.string.edition_detail_title)
                    
                    // Load and display changes for this edition
                    loadEditionChanges(editionNumber)
                },
                onError = { error ->
                    Toast.makeText(this, "Error loading note: $error", Toast.LENGTH_LONG).show()
                    finish()
                }
            )
        } else {
            Toast.makeText(this, "Invalid edition details", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    /**
     * Load and display changes for a specific edition
     */
    private fun loadEditionChanges(editionNumber: Int) {
        val changesLiveData = viewModel.getChangesForEdition(editionNumber)
        
        if (changesLiveData != null) {
            changesLiveData.observe(this) { changesList ->
                if (changesList.isNotEmpty()) {
                    // Show changes
                    binding.recyclerViewChanges.visibility = View.VISIBLE
                    binding.textEmptyMessage.visibility = View.GONE
                    changesAdapter.submitList(changesList)
                    
                    // Update header with edition info
                    val firstChange = changesList.first()
                    val timestamp = formatDate(firstChange.timestamp)
                    val modifiedBy = firstChange.modifiedBy
                    
                    val editionTitle = if (!modifiedBy.isNullOrBlank()) {
                        getString(R.string.edition_title_format, editionNumber, modifiedBy, timestamp)
                    } else {
                        getString(R.string.edition_title_no_user_format, editionNumber, timestamp)
                    }
                    
                    binding.textEditionTitle.text = editionTitle
                } else {
                    // Show empty message
                    binding.recyclerViewChanges.visibility = View.GONE
                    binding.textEmptyMessage.visibility = View.VISIBLE
                }
            }
        } else {
            Toast.makeText(this, "Unable to load edition changes", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    /**
     * Format a timestamp to "yyyy-MM-dd HH:mm" format
     */
    private fun formatDate(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        return dateFormatter.format(instant.atZone(ZoneId.systemDefault()))
    }
    
    /**
     * Set up click listeners for buttons
     */
    private fun setupButtonListeners() {
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }
}
