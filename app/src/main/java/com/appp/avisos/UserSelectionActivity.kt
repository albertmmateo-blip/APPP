package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appp.avisos.databinding.ActivityUserSelectionBinding

/**
 * Activity for user selection at app startup.
 * Presents a list of users to choose from without requiring authentication.
 */
class UserSelectionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityUserSelectionBinding
    private lateinit var sessionManager: UserSessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = UserSessionManager(this)
        
        // Set up button click listeners for each user
        setupUserButtons()
    }
    
    /**
     * Set up click listeners for all user selection buttons
     */
    private fun setupUserButtons() {
        binding.buttonPedro.setOnClickListener { selectUser("Pedro") }
        binding.buttonIsa.setOnClickListener { selectUser("Isa") }
        binding.buttonLourdes.setOnClickListener { selectUser("Lourdes") }
        binding.buttonAlexia.setOnClickListener { selectUser("Alexia") }
        binding.buttonAlbert.setOnClickListener { selectUser("Albert") }
        binding.buttonJoan.setOnClickListener { selectUser("Joan") }
    }
    
    /**
     * Handle user selection
     * @param username The selected username
     */
    private fun selectUser(username: String) {
        // Save the selected user
        sessionManager.setCurrentUser(username)
        
        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        
        // Finish this activity so user can't go back to selection without logging out
        finish()
    }
    
    override fun onBackPressed() {
        // Prevent going back from user selection screen
        // User must select a user to proceed
        // Do nothing
    }
}
