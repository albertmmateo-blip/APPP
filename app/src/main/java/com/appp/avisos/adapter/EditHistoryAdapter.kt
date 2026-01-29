package com.appp.avisos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appp.avisos.database.NoteEditHistory
import com.appp.avisos.databinding.ItemEditHistoryBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Adapter for displaying edit history entries in a RecyclerView
 */
class EditHistoryAdapter : ListAdapter<NoteEditHistory, EditHistoryAdapter.EditHistoryViewHolder>(EditHistoryDiffCallback()) {
    
    private val dateFormatter: DateTimeFormatter = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditHistoryViewHolder {
        val binding = ItemEditHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EditHistoryViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: EditHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class EditHistoryViewHolder(
        private val binding: ItemEditHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(history: NoteEditHistory) {
            binding.textFieldName.text = history.fieldName
            binding.textOldValue.text = history.oldValue ?: "(empty)"
            binding.textNewValue.text = history.newValue ?: "(empty)"
            binding.textTimestamp.text = formatDate(history.timestamp)
        }
        
        private fun formatDate(timestamp: Long): String {
            val instant = Instant.ofEpochMilli(timestamp)
            return dateFormatter.format(instant.atZone(ZoneId.systemDefault()))
        }
    }
    
    private class EditHistoryDiffCallback : DiffUtil.ItemCallback<NoteEditHistory>() {
        override fun areItemsTheSame(oldItem: NoteEditHistory, newItem: NoteEditHistory): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: NoteEditHistory, newItem: NoteEditHistory): Boolean {
            return oldItem == newItem
        }
    }
}
