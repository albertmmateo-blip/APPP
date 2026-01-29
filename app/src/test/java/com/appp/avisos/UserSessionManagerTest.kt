package com.appp.avisos

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit tests for UserSessionManager
 */
@RunWith(MockitoJUnitRunner::class)
class UserSessionManagerTest {
    
    @Mock
    private lateinit var mockContext: Context
    
    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences
    
    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor
    
    private lateinit var sessionManager: UserSessionManager
    
    @Before
    fun setup() {
        // Setup mock behavior
        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor)
        `when`(mockEditor.remove(anyString())).thenReturn(mockEditor)
        
        sessionManager = UserSessionManager(mockContext)
    }
    
    @Test
    fun `logout clears user session`() {
        // When
        sessionManager.logout()
        
        // Then
        verify(mockEditor).remove("current_user")
        verify(mockEditor).apply()
    }
    
    @Test
    fun `setCurrentUser accepts valid user Pedro`() {
        // When
        sessionManager.setCurrentUser("Pedro")
        
        // Then
        verify(mockEditor).putString("current_user", "Pedro")
        verify(mockEditor).apply()
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `setCurrentUser rejects invalid user`() {
        // When
        sessionManager.setCurrentUser("InvalidUser")
        
        // Then - exception should be thrown
    }
    
    @Test
    fun `getCurrentUser returns stored user`() {
        // Given
        `when`(mockSharedPreferences.getString("current_user", null)).thenReturn("Pedro")
        
        // When
        val result = sessionManager.getCurrentUser()
        
        // Then
        assertEquals("Pedro", result)
    }
    
    @Test
    fun `isUserLoggedIn returns true when user is logged in`() {
        // Given
        `when`(mockSharedPreferences.getString("current_user", null)).thenReturn("Pedro")
        
        // When
        val result = sessionManager.isUserLoggedIn()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isUserLoggedIn returns false when no user is logged in`() {
        // Given
        `when`(mockSharedPreferences.getString("current_user", null)).thenReturn(null)
        
        // When
        val result = sessionManager.isUserLoggedIn()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `validateFacturesPassword returns true for correct password`() {
        // When
        val result = sessionManager.validateFacturesPassword("mixo")
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `validateFacturesPassword returns false for incorrect password`() {
        // When
        val result = sessionManager.validateFacturesPassword("wrong")
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `validateFacturesPassword is case sensitive`() {
        // When & Then
        assertTrue(sessionManager.validateFacturesPassword("mixo"))
        assertFalse(sessionManager.validateFacturesPassword("MIXO"))
        assertFalse(sessionManager.validateFacturesPassword("Mixo"))
    }
}
