package com.appp.avisos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.appp.avisos.adapter.DeletedNotesAdapter
import com.appp.avisos.database.Note
import com.appp.avisos.databinding.FragmentRecycleBinBinding
import com.appp.avisos.viewmodel.RecycleBinViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment that displays deleted notes for a specific deletion type
 */
class RecycleBinFragment : Fragment() {
    
    private var _binding: FragmentRecycleBinBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: RecycleBinViewModel by activityViewModels()
    private lateinit var deletedNotesAdapter: DeletedNotesAdapter
    private var deletionType: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deletionType = arguments?.getString(ARG_DELETION_TYPE)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecycleBinBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeDeletedNotes()
    }
    
    /**
     * Configure RecyclerView with DeletedNotesAdapter
     */
    private fun setupRecyclerView() {
        deletedNotesAdapter = DeletedNotesAdapter(
            onRestoreClick = { note ->
                showRestoreConfirmationDialog(note)
            },
            onDeleteClick = { note ->
                showPermanentDeleteConfirmationDialog(note)
            }
        )
        binding.recyclerViewDeletedNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deletedNotesAdapter
        }
    }
    
    /**
     * Observe deleted notes LiveData for this deletion type and update the RecyclerView
     */
    private fun observeDeletedNotes() {
        val notesLiveData = when (deletionType) {
            Note.DELETION_TYPE_ESBORRADES -> viewModel.esborradesNotes
            Note.DELETION_TYPE_FINALITZADES -> viewModel.finalitzadesNotes
            else -> viewModel.esborradesNotes // Default fallback
        }
        
        notesLiveData.observe(viewLifecycleOwner) { notes ->
            deletedNotesAdapter.submitList(notes)
            handleEmptyState(notes.isEmpty())
        }
    }
    
    /**
     * Show or hide RecyclerView based on whether deleted notes list is empty
     */
    private fun handleEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerViewDeletedNotes.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.recyclerViewDeletedNotes.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }
    
    /**
     * Show confirmation dialog before restoring a note
     */
    private fun showRestoreConfirmationDialog(note: Note) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_restore_title)
            .setMessage(R.string.dialog_restore_message)
            .setPositiveButton(R.string.button_restore) { _, _ ->
                restoreNote(note)
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }
    
    /**
     * Restore a note from the recycle bin
     */
    private fun restoreNote(note: Note) {
        viewModel.restoreNote(
            noteId = note.id,
            onSuccess = {
                Toast.makeText(
                    requireContext(),
                    R.string.message_note_restored,
                    Toast.LENGTH_SHORT
                ).show()
            },
            onError = { error ->
                Toast.makeText(
                    requireContext(),
                    "Error: $error",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
    
    /**
     * Show confirmation dialog before permanently deleting a note
     */
    private fun showPermanentDeleteConfirmationDialog(note: Note) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_permanent_delete_title)
            .setMessage(R.string.dialog_permanent_delete_message)
            .setPositiveButton(R.string.button_delete) { _, _ ->
                permanentlyDeleteNote(note)
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
            .apply {
                // Make the positive (DELETE) button red using theme's error color
                getButton(android.content.DialogInterface.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.error))
            }
    }
    
    /**
     * Permanently delete a note from the recycle bin
     */
    private fun permanentlyDeleteNote(note: Note) {
        viewModel.permanentlyDeleteNote(
            note = note,
            onSuccess = {
                Toast.makeText(
                    requireContext(),
                    R.string.message_note_deleted_permanently,
                    Toast.LENGTH_SHORT
                ).show()
            },
            onError = { error ->
                Toast.makeText(
                    requireContext(),
                    "Error: $error",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_DELETION_TYPE = "deletion_type"
        
        /**
         * Factory method to create a new instance of RecycleBinFragment
         * @param deletionType The deletion type to filter by (Esborrades or Finalitzades)
         * @return A new RecycleBinFragment instance configured for the specified type
         */
        fun newInstance(deletionType: String): RecycleBinFragment {
            return RecycleBinFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DELETION_TYPE, deletionType)
                }
            }
        }
    }
}
