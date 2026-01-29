package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appp.avisos.databinding.ActivityFacturesCompraVendaSelectionBinding

/**
 * Activity that displays Compra/Venda selection for Factures subcategories (Passades or Per passar).
 * Shows two large buttons: Compra and Venda.
 * Only accessible to Pedro through Factures → Passades or Factures → Per passar.
 */
class FacturesCompraVendaSelectionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFacturesCompraVendaSelectionBinding
    private var parentSubcategory: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacturesCompraVendaSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get parent subcategory from intent (Passades or Per passar)
        parentSubcategory = intent.getStringExtra(EXTRA_PARENT_SUBCATEGORY)
        
        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Factures - $parentSubcategory"
        
        // Set up button click listeners
        setupButtons()
    }
    
    /**
     * Set up click listeners for Compra and Venda buttons
     */
    private fun setupButtons() {
        binding.cardCompra.setOnClickListener {
            openCompraDetail()
        }
        
        binding.cardVenda.setOnClickListener {
            openVendaDetail()
        }
    }
    
    /**
     * Open Compra notes for the parent subcategory
     */
    private fun openCompraDetail() {
        val intent = Intent(this, CompraSubcategoryDetailActivity::class.java)
        intent.putExtra(CompraSubcategoryDetailActivity.EXTRA_SUBCATEGORY, parentSubcategory)
        startActivity(intent)
    }
    
    /**
     * Open Venda notes for the parent subcategory
     */
    private fun openVendaDetail() {
        val intent = Intent(this, VendaSubcategoryDetailActivity::class.java)
        intent.putExtra(VendaSubcategoryDetailActivity.EXTRA_SUBCATEGORY, parentSubcategory)
        startActivity(intent)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    companion object {
        const val EXTRA_PARENT_SUBCATEGORY = "parent_subcategory"
    }
}
