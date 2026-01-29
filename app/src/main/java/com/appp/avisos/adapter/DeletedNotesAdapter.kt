package com.appp.avisos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appp.avisos.R
import com.appp.avisos.database.Note
import com.appp.avisos.databinding.ItemDeletedNoteBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Adapter for displaying deleted notes in the Recycle Bin
 */
class DeletedNotesAdapter(
    private val onRestoreClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : ListAdapter<Note, DeletedNotesAdapter.DeletedNoteViewHolder>(NoteDiffCallback()) {

    companion object {
        private val dateFormatter: DateTimeFormatter = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletedNoteViewHolder {
        val binding = ItemDeletedNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeletedNoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeletedNoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DeletedNoteViewHolder(
        private val binding: ItemDeletedNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.textNoteName.text = note.name
            binding.textNoteBody.text = note.body
            
            // Format and display deletion date
            val deletionLabel = if (note.deletionType == Note.DELETION_TYPE_FINALITZADES) {
                binding.root.context.getString(R.string.finalized_on)
            } else {
                binding.root.context.getString(R.string.deleted_on)
            }
            val formattedDate = note.deletedDate?.let { formatDate(it) } ?: ""
            binding.textDeletedDate.text = "$deletionLabel $formattedDate"
            
            // Set category badge
            binding.chipCategory.text = note.category
            
            // Set category-specific color for the chip
            val categoryColor = when (note.category) {
                "Trucar" -> R.color.category_trucar
                "Encarregar" -> R.color.category_encarregar
                "Factures" -> R.color.category_factures
                "Notes" -> R.color.category_notes
                else -> R.color.category_notes
            }
            binding.chipCategory.setChipBackgroundColorResource(categoryColor)
            
            // Set up button click listeners
            binding.buttonRestore.setOnClickListener {
                onRestoreClick(note)
            }
            
            binding.buttonDeletePermanently.setOnClickListener {
                onDeleteClick(note)
            }
        }

        private fun formatDate(timestamp: Long): String {
            val instant = Instant.ofEpochMilli(timestamp)
            return dateFormatter.format(instant.atZone(ZoneId.systemDefault()))
        }
    }

    private class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}
