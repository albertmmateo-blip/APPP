package com.appp.avisos

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appp.avisos.adapter.RecycleBinPagerAdapter
import com.appp.avisos.databinding.ActivityRecycleBinBinding
import com.appp.avisos.viewmodel.RecycleBinViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Activity for managing the Recycle Bin.
 * Displays deleted notes in two categories: Esborrades and Finalitzades.
 * Users can restore notes or permanently delete them.
 */
class RecycleBinActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRecycleBinBinding
    private val viewModel: RecycleBinViewModel by viewModels()
    private lateinit var pagerAdapter: RecycleBinPagerAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Set up ViewPager2 with tabs
        setupViewPager()
        
        // Trigger cleanup of expired notes on activity creation
        cleanupExpiredNotes()
    }
    
    /**
     * Set up ViewPager2 with TabLayout and recycle bin fragments
     */
    private fun setupViewPager() {
        // Create adapter for ViewPager2
        pagerAdapter = RecycleBinPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        
        // Connect TabLayout with ViewPager2 using TabLayoutMediator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.setText(R.string.tab_esborrades)
                1 -> tab.setText(R.string.tab_finalitzades)
            }
        }.attach()
    }
    
    /**
     * Clean up notes that have been in the recycle bin for more than 15 days
     */
    private fun cleanupExpiredNotes() {
        viewModel.cleanupExpiredNotes()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
