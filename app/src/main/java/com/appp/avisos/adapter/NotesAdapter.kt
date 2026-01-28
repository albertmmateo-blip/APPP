package com.appp.avisos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appp.avisos.database.Note
import com.appp.avisos.databinding.ItemNoteBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * RecyclerView adapter for displaying a list of notes.
 * 
 * This adapter uses ListAdapter and DiffUtil for efficient updates,
 * ViewBinding for view access, and follows Android best practices.
 *
 * @param onItemClicked Callback invoked when a note item is clicked, receiving the clicked Note
 */
class NotesAdapter(
    private val onItemClicked: (Note) -> Unit
) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(DiffCallback) {

    /**
     * ViewHolder for note items.
     * Uses ViewBinding for efficient and type-safe view access.
     */
    inner class NoteViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Set click listener once in the constructor for better performance
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(position))
                }
            }
        }

        /**
         * Binds a Note to the ViewHolder's views.
         * 
         * @param note The note to display
         */
        fun bind(note: Note) {
            // Set note title
            binding.textNoteName.text = note.name
            
            // Set note body preview (ellipsized by layout)
            binding.textNoteBody.text = note.body
            
            // Handle contact info
            if (note.contact.isNullOrEmpty()) {
                // Hide contact info if not available
                binding.iconContact.visibility = View.GONE
                binding.textContactInfo.visibility = View.GONE
            } else {
                // Show contact info
                binding.iconContact.visibility = View.VISIBLE
                binding.textContactInfo.visibility = View.VISIBLE
                binding.textContactInfo.text = note.contact
            }
            
            // Format and display modified date
            binding.textModifiedDate.text = formatDate(note.modifiedDate)
        }

        /**
         * Formats a timestamp to "dd/MM/yyyy HH:mm" format.
         * 
         * @param timestamp The timestamp in milliseconds
         * @return Formatted date string
         */
        private fun formatDate(timestamp: Long): String {
            val instant = Instant.ofEpochMilli(timestamp)
            return dateFormatter.format(instant.atZone(ZoneId.systemDefault()))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    /**
     * Companion object for static utilities and DiffUtil callback.
     */
    companion object {
        /**
         * Thread-safe date formatter for efficient date formatting.
         * DateTimeFormatter is immutable and thread-safe.
         */
        private val dateFormatter: DateTimeFormatter = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    }

    /**
     * DiffUtil callback for computing differences between Note lists.
     * 
     * This enables ListAdapter to efficiently update only changed items,
     * providing smooth animations and optimal performance.
     */
    object DiffCallback : DiffUtil.ItemCallback<Note>() {
        /**
         * Checks if two notes represent the same item (same ID).
         */
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         * Checks if two notes have the same content.
         * Uses data class equals() for comprehensive comparison.
         */
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}
