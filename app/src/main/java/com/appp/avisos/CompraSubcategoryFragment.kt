package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.appp.avisos.databinding.FragmentCompraSubcategoryBinding

/**
 * Fragment that displays Compra subcategory selection for Pedro user only.
 * Shows 2 large buttons for: Passades and Per passar.
 * The adapter ensures this fragment is only shown to Pedro.
 */
class CompraSubcategoryFragment : Fragment() {
    
    private var _binding: FragmentCompraSubcategoryBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompraSubcategoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSubcategoryButtons()
    }
    
    /**
     * Set up click listeners for the two subcategory buttons
     */
    private fun setupSubcategoryButtons() {
        // Passades - Accounted for purchases
        binding.cardPassades.setOnClickListener {
            openSubcategoryDetail("Passades")
        }
        
        // Per passar - Yet to be accounted for purchases
        binding.cardPerPassar.setOnClickListener {
            openSubcategoryDetail("Per passar")
        }
    }
    
    /**
     * Open a detail activity showing notes for a specific subcategory
     * @param subcategory The subcategory name (Passades, Per passar)
     */
    private fun openSubcategoryDetail(subcategory: String) {
        val intent = Intent(requireContext(), CompraSubcategoryDetailActivity::class.java)
        intent.putExtra("subcategory", subcategory)
        startActivity(intent)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        /**
         * Factory method to create a new instance of CompraSubcategoryFragment
         * @return A new CompraSubcategoryFragment instance
         */
        fun newInstance(): CompraSubcategoryFragment {
            return CompraSubcategoryFragment()
        }
    }
}
