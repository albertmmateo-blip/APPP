package com.appp.avisos

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.appp.avisos.adapter.CategoryPagerAdapter
import com.appp.avisos.database.Note
import com.appp.avisos.databinding.ActivityMainBinding
import com.appp.avisos.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var pagerAdapter: CategoryPagerAdapter
    private lateinit var sessionManager: UserSessionManager
    
    // Categories corresponding to tab positions
    private val categories = arrayOf("Trucar", "Encarregar", "Factures", "Notes")
    
    // Track the currently selected category
    private var currentCategory: String? = null
    
    // Track the previously selected tab position for optimization
    private var previousSelectedPosition: Int = 0
    
    // Track if we're programmatically changing tabs (to avoid triggering password dialog)
    private var isHandlingProgrammaticTabChange: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize session manager
        sessionManager = UserSessionManager(this)
        
        // Check if user is logged in
        if (!sessionManager.isUserLoggedIn()) {
            // No user logged in, redirect to user selection
            redirectToUserSelection()
            return
        }
        
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
        
        // Add tab selection listener to handle Factures authentication
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                val position = tab.position
                val categoryName = categories[position]
                
                // Check if user is trying to access Factures tab
                if (categoryName == "Factures" && !isHandlingProgrammaticTabChange) {
                    val currentUser = sessionManager.getCurrentUser()
                    
                    // Only Pedro can access Factures
                    if (currentUser == "Pedro") {
                        // Check if already authenticated
                        if (!sessionManager.isFacturesAuthenticated()) {
                            // Block the tab change and show password dialog
                            isHandlingProgrammaticTabChange = true
                            binding.viewPager.post {
                                binding.viewPager.setCurrentItem(previousSelectedPosition, false)
                                isHandlingProgrammaticTabChange = false
                                showFacturesPasswordDialog(position)
                            }
                        }
                    } else {
                        // Non-Pedro users cannot access Factures at all
                        isHandlingProgrammaticTabChange = true
                        binding.viewPager.post {
                            binding.viewPager.setCurrentItem(previousSelectedPosition, false)
                            isHandlingProgrammaticTabChange = false
                            showAccessDeniedDialog()
                        }
                    }
                }
            }
            
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                // No action needed
            }
            
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                // No action needed
            }
        })
        
        // Set up page change listener to track current category and update tab icon colors
        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentCategory = categories[position]
                updateTabIconColors(position)
                
                // Update previous position only if this is not a blocked attempt
                if (!isHandlingProgrammaticTabChange) {
                    previousSelectedPosition = position
                }
            }
        })
        
        // Set initial category and icon colors (post to ensure tabs are initialized)
        currentCategory = categories[0]
        binding.tabLayout.post {
            updateTabIconColors(0)
        }
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
     * @param position The tab position (0-3) corresponding to category index
     * @param count The number to display on the badge; if 0, badge is removed
     */
    private fun updateTabBadge(position: Int, count: Int) {
        val tab = binding.tabLayout.getTabAt(position) ?: return
        
        if (count > 0) {
            val badge = tab.orCreateBadge
            badge.number = count
            badge.isVisible = true
        } else {
            tab.removeBadge()
        }
    }
    
    /**
     * Update tab icon colors based on the currently selected tab
     * The selected tab icon gets its category color, while others remain white
     * Optimized to only update the previously selected and newly selected tabs
     * 
     * @param selectedPosition The position of the currently selected tab
     */
    private fun updateTabIconColors(selectedPosition: Int) {
        // Get white color for unselected tabs
        val whiteColor = ContextCompat.getColor(this, R.color.text_on_primary)
        
        // Reset previous selected tab to white (if different from current)
        if (previousSelectedPosition != selectedPosition) {
            val previousTab = binding.tabLayout.getTabAt(previousSelectedPosition)
            previousTab?.icon?.setTintList(ColorStateList.valueOf(whiteColor))
        }
        
        // Apply category-specific color to newly selected tab
        val currentTab = binding.tabLayout.getTabAt(selectedPosition)
        if (currentTab != null) {
            val categoryColorResId = when (selectedPosition) {
                0 -> R.color.category_trucar      // Blue for Trucar
                1 -> R.color.category_encarregar  // Orange for Encarregar
                2 -> R.color.category_factures    // Red for Factures
                3 -> R.color.category_notes       // Darker tan for Notes
                else -> R.color.text_on_primary   // Default white
            }
            val color = ContextCompat.getColor(this, categoryColorResId)
            currentTab.icon?.setTintList(ColorStateList.valueOf(color))
        }
        
        // Update previous position tracker
        previousSelectedPosition = selectedPosition
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
    
    /**
     * Show password dialog for Factures access
     * @param facturesPosition The position of the Factures tab to navigate to on success
     */
    private fun showFacturesPasswordDialog(facturesPosition: Int) {
        val passwordInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Contrasenya"
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Accés a Factures")
            .setMessage("Aquesta categoria està restringida. Si us plau, introdueix la contrasenya:")
            .setView(passwordInput)
            .setPositiveButton("Acceptar") { dialog, _ ->
                val enteredPassword = passwordInput.text.toString()
                if (sessionManager.validateFacturesPassword(enteredPassword)) {
                    // Password correct - grant access and navigate to Factures
                    sessionManager.setFacturesAuthenticated(true)
                    isHandlingProgrammaticTabChange = true
                    binding.viewPager.post {
                        binding.viewPager.setCurrentItem(facturesPosition, true)
                        isHandlingProgrammaticTabChange = false
                    }
                } else {
                    // Password incorrect - show error
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("Contrasenya incorrecta")
                        .setPositiveButton("D'acord", null)
                        .show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel·lar") { dialog, _ ->
                dialog.dismiss()
            }
            .setOnCancelListener { dialog ->
                // Handle back button or outside tap same as cancel button
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
    
    /**
     * Show access denied dialog for non-Pedro users
     */
    private fun showAccessDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Accés Denegat")
            .setMessage("Només l'usuari Pedro té accés a la categoria Factures.")
            .setPositiveButton("D'acord", null)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_recycle_bin -> {
                openRecycleBin()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * Redirect to UserSelectionActivity
     */
    private fun redirectToUserSelection() {
        val intent = Intent(this, UserSelectionActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    /**
     * Log out the current user and return to user selection
     */
    private fun logout() {
        sessionManager.logout()
        redirectToUserSelection()
    }
    
    /**
     * Open RecycleBinActivity to view deleted notes
     */
    private fun openRecycleBin() {
        val intent = Intent(this, RecycleBinActivity::class.java)
        startActivity(intent)
    }
}
