package com.appp.avisos

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager for user session and authentication.
 * Handles storing and retrieving the currently logged-in user.
 */
class UserSessionManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "user_session_prefs"
        private const val KEY_CURRENT_USER = "current_user"
        
        // List of available users
        val AVAILABLE_USERS = listOf(
            "Pedro",
            "Isa",
            "Lourdes",
            "Alexia",
            "Albert",
            "Joan"
        )
    }
    
    /**
     * Save the currently logged-in user
     * @param username The username to save
     */
    fun setCurrentUser(username: String) {
        sharedPreferences.edit()
            .putString(KEY_CURRENT_USER, username)
            .apply()
    }
    
    /**
     * Get the currently logged-in user
     * @return The username, or null if no user is logged in
     */
    fun getCurrentUser(): String? {
        return sharedPreferences.getString(KEY_CURRENT_USER, null)
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    fun isUserLoggedIn(): Boolean {
        return getCurrentUser() != null
    }
    
    /**
     * Log out the current user
     */
    fun logout() {
        sharedPreferences.edit()
            .remove(KEY_CURRENT_USER)
            .apply()
    }
}
