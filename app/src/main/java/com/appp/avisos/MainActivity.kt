package com.appp.avisos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appp.avisos.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        
        // Set up tabs with icons and text labels
        setupTabs()
    }
    
    /**
     * Configure TabLayout with 4 category tabs
     * Each tab includes both an icon and text label
     */
    private fun setupTabs() {
        // Tab 1: Trucar (Call)
        binding.tabLayout.addTab(
            binding.tabLayout.newTab()
                .setText(R.string.tab_trucar)
                .setIcon(R.drawable.ic_phone)
        )
        
        // Tab 2: Encarregar (Order)
        binding.tabLayout.addTab(
            binding.tabLayout.newTab()
                .setText(R.string.tab_encarregar)
                .setIcon(R.drawable.ic_shopping_cart)
        )
        
        // Tab 3: Factures (Invoices)
        binding.tabLayout.addTab(
            binding.tabLayout.newTab()
                .setText(R.string.tab_factures)
                .setIcon(R.drawable.ic_receipt)
        )
        
        // Tab 4: Notes
        binding.tabLayout.addTab(
            binding.tabLayout.newTab()
                .setText(R.string.tab_notes)
                .setIcon(R.drawable.ic_note)
        )
        
        // Set default selected tab
        binding.tabLayout.getTabAt(0)?.select()
    }
}
