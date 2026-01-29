package com.appp.avisos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appp.avisos.R
import com.appp.avisos.database.NoteEditHistory
import com.appp.avisos.databinding.ItemEditionHistoryBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Adapter for displaying edition history entries (grouped edits) in a RecyclerView
 * Each item represents a single edition (edit session) that may contain multiple field changes
 */
class EditionHistoryAdapter(
    private val onEditionClick: (Int) -> Unit
) : ListAdapter<NoteEditHistory, EditionHistoryAdapter.EditionViewHolder>(EditionDiffCallback()) {
    
    private val dateFormatter: DateTimeFormatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditionViewHolder {
        val binding = ItemEditionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EditionViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: EditionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class EditionViewHolder(
        private val binding: ItemEditionHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(edition: NoteEditHistory) {
            val context = binding.root.context
            val editionNumber = edition.editionNumber
            val timestamp = formatDate(edition.timestamp)
            val modifiedBy = edition.modifiedBy
            
            // Format: "Edició #5 per Joan Garcia a les 2026-01-29 17:35"
            val title = if (!modifiedBy.isNullOrBlank()) {
                context.getString(R.string.edition_title_format, editionNumber, modifiedBy, timestamp)
            } else {
                context.getString(R.string.edition_title_no_user_format, editionNumber, timestamp)
            }
            
            binding.textEditionTitle.text = title
            
            // Set click listener to view changes
            binding.root.setOnClickListener {
                onEditionClick(editionNumber)
            }
        }
        
        private fun formatDate(timestamp: Long): String {
            val instant = Instant.ofEpochMilli(timestamp)
            return dateFormatter.format(instant.atZone(ZoneId.systemDefault()))
        }
    }
    
    private class EditionDiffCallback : DiffUtil.ItemCallback<NoteEditHistory>() {
        override fun areItemsTheSame(oldItem: NoteEditHistory, newItem: NoteEditHistory): Boolean {
            return oldItem.editionNumber == newItem.editionNumber
        }
        
        override fun areContentsTheSame(oldItem: NoteEditHistory, newItem: NoteEditHistory): Boolean {
            return oldItem.editionNumber == newItem.editionNumber &&
                   oldItem.timestamp == newItem.timestamp &&
                   oldItem.modifiedBy == newItem.modifiedBy
        }
    }
}
