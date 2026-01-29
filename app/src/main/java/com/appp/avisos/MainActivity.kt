package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appp.avisos.adapter.CategoryPagerAdapter
import com.appp.avisos.database.Note
import com.appp.avisos.databinding.ActivityMainBinding
import com.appp.avisos.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var pagerAdapter: CategoryPagerAdapter
    
    // Categories corresponding to tab positions
    private val categories = arrayOf("Trucar", "Encarregar", "Factures", "Notes")
    
    // Track the currently selected category
    private var currentCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        
        // Set up ViewPager2 with categories
        setupViewPager()
        
        // Set up FAB click listener
        setupFab()
        
        // Observe note counts to update badges
        observeNoteCounts()
    }
    
    /**
     * Set up ViewPager2 with TabLayout and category fragments
     */
    private fun setupViewPager() {
        // Create adapter for ViewPager2
        pagerAdapter = CategoryPagerAdapter(this, categories)
        binding.viewPager.adapter = pagerAdapter
        
        // Connect TabLayout with ViewPager2 using TabLayoutMediator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Configure each tab with icon and text
            when (position) {
                0 -> {
                    tab.setText(R.string.category_trucar)
                    tab.setIcon(R.drawable.ic_phone)
                }
                1 -> {
                    tab.setText(R.string.category_encarregar)
                    tab.setIcon(R.drawable.ic_shopping_cart)
                }
                2 -> {
                    tab.setText(R.string.category_factures)
                    tab.setIcon(R.drawable.ic_receipt)
                }
                3 -> {
                    tab.setText(R.string.category_notes)
                    tab.setIcon(R.drawable.ic_note)
                }
            }
        }.attach()
        
        // Set up page change listener to track current category
        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentCategory = categories[position]
            }
        })
        
        // Set initial category
        currentCategory = categories[0]
    }
    
    /**
     * Observe note counts and update tab badges
     */
    private fun observeNoteCounts() {
        viewModel.trucarCount.observe(this) { count ->
            updateTabBadge(0, count)
        }
        
        viewModel.encarregarCount.observe(this) { count ->
            updateTabBadge(1, count)
        }
        
        viewModel.facturesCount.observe(this) { count ->
            updateTabBadge(2, count)
        }
        
        viewModel.notesCount.observe(this) { count ->
            updateTabBadge(3, count)
        }
    }
    
    /**
     * Update badge on a specific tab
     * 
     * @param position The tab position
     * @param count The number to display on the badge
     */
    private fun updateTabBadge(position: Int, count: Int) {
        val tab = binding.tabLayout.getTabAt(position)
        if (count > 0) {
            val badge = tab?.orCreateBadge
            badge?.number = count
            badge?.isVisible = true
        } else {
            tab?.removeBadge()
        }
    }
    
    /**
     * Set up FAB to open NoteEditorActivity for creating new notes
     */
    private fun setupFab() {
        binding.fabAddNote.setOnClickListener {
            // Pass current category when creating new note
            openNoteEditor(null)
        }
    }
    
    /**
     * Open NoteEditorActivity to create a new note or edit an existing one
     * 
     * @param note The note to edit, or null to create a new note
     */
    private fun openNoteEditor(note: Note?) {
        val intent = Intent(this, NoteEditorActivity::class.java)
        
        if (note != null) {
            // Edit mode - pass note data
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_ID, note.id)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_NAME, note.name)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_BODY, note.body)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_CONTACT, note.contact)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_CATEGORY, note.category)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_CREATED_DATE, note.createdDate)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_MODIFIED_DATE, note.modifiedDate)
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_IS_URGENT, note.isUrgent)
        }
        
        // Pass current category for new notes
        intent.putExtra(NoteEditorActivity.EXTRA_CURRENT_CATEGORY, currentCategory)
        
        startActivity(intent)
    }
}
