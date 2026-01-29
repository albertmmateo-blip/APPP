package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.appp.avisos.adapter.NotesAdapter
import com.appp.avisos.database.Note
import com.appp.avisos.databinding.FragmentCategoryBinding
import com.appp.avisos.viewmodel.MainViewModel

/**
 * Fragment that displays notes for a specific category
 */
class CategoryFragment : Fragment() {
    
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var notesAdapter: NotesAdapter
    private var category: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString(ARG_CATEGORY)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeNotes()
    }
    
    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter { note ->
            openNoteDetail(note)
        }
        binding.recyclerViewNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notesAdapter
        }
    }
    
    private fun observeNotes() {
        // Set the selected category in ViewModel
        viewModel.setSelectedCategory(category)
        
        // Observe filtered notes
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            notesAdapter.submitList(notes)
            handleEmptyState(notes.isEmpty())
        }
    }
    
    private fun handleEmptyState(isEmpty: Boolean) {
        binding.recyclerViewNotes.visibility = if (isEmpty) View.GONE else View.VISIBLE
        // TODO: Show empty state view when implemented
    }
    
    private fun openNoteDetail(note: Note) {
        val intent = Intent(requireContext(), NoteDetailActivity::class.java).apply {
            putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
            putExtra(NoteDetailActivity.EXTRA_CURRENT_CATEGORY, category)
        }
        startActivity(intent)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_CATEGORY = "category"
        
        fun newInstance(category: String): CategoryFragment {
            return CategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                }
            }
        }
    }
}
