package com.appp.avisos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.appp.avisos.databinding.FragmentFacturesSubcategoryBinding

/**
 * Fragment that displays Factures subcategory selection for Pedro user only.
 * Shows 4 large square buttons for: Passades, Per passar, Per pagar, Per cobrar.
 * Non-Pedro users see the regular CategoryFragment instead.
 */
class FacturesSubcategoryFragment : Fragment() {
    
    private var _binding: FragmentFacturesSubcategoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var sessionManager: UserSessionManager
    
    // Callback interface for subcategory selection
    interface OnSubcategorySelectedListener {
        fun onSubcategorySelected(subcategory: String)
    }
    
    private var listener: OnSubcategorySelectedListener? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = UserSessionManager(requireContext())
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacturesSubcategoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Check if current user is Pedro
        if (sessionManager.getCurrentUser() != "Pedro") {
            // If not Pedro, replace this fragment with regular CategoryFragment
            replaceWithCategoryFragment()
            return
        }
        
        setupSubcategoryButtons()
    }
    
    /**
     * Set up click listeners for the four subcategory buttons
     */
    private fun setupSubcategoryButtons() {
        // Passades - Accounted for invoices
        binding.cardPassades.setOnClickListener {
            openSubcategory("Passades")
        }
        
        // Per passar - Yet to be accounted for invoices
        binding.cardPerPassar.setOnClickListener {
            openSubcategory("Per passar")
        }
        
        // Per pagar - Due invoices
        binding.cardPerPagar.setOnClickListener {
            openSubcategory("Per pagar")
        }
        
        // Per cobrar - Invoices not yet collected
        binding.cardPerCobrar.setOnClickListener {
            openSubcategory("Per cobrar")
        }
    }
    
    /**
     * Open a specific subcategory view showing notes for that subcategory
     * @param subcategory The subcategory name (Passades, Per passar, Per pagar, Per cobrar)
     */
    private fun openSubcategory(subcategory: String) {
        // Replace this fragment with CategoryFragment showing filtered notes
        val fragment = CategoryFragment.newInstance("Factures|$subcategory")
        
        parentFragmentManager.beginTransaction()
            .replace(id, fragment)
            .addToBackStack(null)
            .commit()
    }
    
    /**
     * Replace this fragment with regular CategoryFragment for non-Pedro users
     */
    private fun replaceWithCategoryFragment() {
        val fragment = CategoryFragment.newInstance("Factures")
        
        parentFragmentManager.beginTransaction()
            .replace(id, fragment)
            .commit()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        /**
         * Factory method to create a new instance of FacturesSubcategoryFragment
         * @return A new FacturesSubcategoryFragment instance
         */
        fun newInstance(): FacturesSubcategoryFragment {
            return FacturesSubcategoryFragment()
        }
    }
}
