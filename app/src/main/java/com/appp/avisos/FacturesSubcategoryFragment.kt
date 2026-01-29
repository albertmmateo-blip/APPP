package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.appp.avisos.databinding.FragmentFacturesSubcategoryBinding

/**
 * Fragment that displays Factures subcategory selection for Pedro user only.
 * Shows 4 large square buttons for: Passades, Per passar, Per pagar, Per cobrar.
 * The adapter ensures this fragment is only shown to Pedro.
 */
class FacturesSubcategoryFragment : Fragment() {
    
    private var _binding: FragmentFacturesSubcategoryBinding? = null
    private val binding get() = _binding!!
    
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
        setupSubcategoryButtons()
    }
    
    /**
     * Set up click listeners for the four subcategory buttons
     */
    private fun setupSubcategoryButtons() {
        // Passades and Per passar - Show Compra/Venda selection
        binding.cardPassades.setOnClickListener {
            openCompraVendaSelection("Passades")
        }
        
        binding.cardPerPassar.setOnClickListener {
            openCompraVendaSelection("Per passar")
        }
        
        // Per pagar and Per cobrar - Show notes directly
        binding.cardPerPagar.setOnClickListener {
            openSubcategoryDetail("Per pagar")
        }
        
        binding.cardPerCobrar.setOnClickListener {
            openSubcategoryDetail("Per cobrar")
        }
    }
    
    /**
     * Open Compra/Venda selection activity for Passades or Per passar
     * @param parentSubcategory The parent subcategory (Passades or Per passar)
     */
    private fun openCompraVendaSelection(parentSubcategory: String) {
        val intent = Intent(requireContext(), FacturesCompraVendaSelectionActivity::class.java)
        intent.putExtra(FacturesCompraVendaSelectionActivity.EXTRA_PARENT_SUBCATEGORY, parentSubcategory)
        startActivity(intent)
    }
    
    /**
     * Open a detail activity showing notes for a specific subcategory
     * @param subcategory The subcategory name (Per pagar, Per cobrar)
     */
    private fun openSubcategoryDetail(subcategory: String) {
        val intent = Intent(requireContext(), FacturesSubcategoryDetailActivity::class.java)
        intent.putExtra("subcategory", subcategory)
        startActivity(intent)
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
