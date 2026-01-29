package com.appp.avisos

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.appp.avisos.databinding.ActivityUserSelectionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Activity for user selection at app startup.
 * Presents a list of users to choose from.
 * Pedro requires password authentication.
 */
class UserSelectionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityUserSelectionBinding
    private lateinit var sessionManager: UserSessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = UserSessionManager(this)
        
        // Disable back button to prevent bypassing user selection
        // Allow exiting the app entirely if user presses back
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Exit the app when back is pressed on user selection screen
                finish()
            }
        })
        
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
        // Pedro requires password authentication
        if (username == "Pedro") {
            showPedroPasswordDialog()
        } else {
            // Other users can login directly
            proceedToMainActivity(username)
        }
    }
    
    /**
     * Show password dialog for Pedro login
     */
    private fun showPedroPasswordDialog() {
        val passwordInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Contrasenya"
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Inici de sessió - Pedro")
            .setMessage("Si us plau, introdueix la contrasenya:")
            .setView(passwordInput)
            .setPositiveButton("Acceptar") { dialog, _ ->
                val enteredPassword = passwordInput.text.toString()
                if (sessionManager.validateFacturesPassword(enteredPassword)) {
                    // Password correct - proceed to login
                    proceedToMainActivity("Pedro")
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
            .setCancelable(true)
            .show()
    }
    
    /**
     * Proceed to MainActivity after successful login
     * @param username The username to log in
     */
    private fun proceedToMainActivity(username: String) {
        // Save the selected user
        sessionManager.setCurrentUser(username)
        
        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        
        // Finish this activity so user can't go back to selection without logging out
        finish()
    }
}
