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
    fun `validateFacturesPassword returns true for correct password`() {
        // When
        val result = sessionManager.validateFacturesPassword("mixo")
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `validateFacturesPassword returns false for incorrect password`() {
        // When
        val resultWrong1 = sessionManager.validateFacturesPassword("wrong")
        val resultWrong2 = sessionManager.validateFacturesPassword("MIXO")
        val resultWrong3 = sessionManager.validateFacturesPassword("mix")
        val resultEmpty = sessionManager.validateFacturesPassword("")
        
        // Then
        assertFalse(resultWrong1)
        assertFalse(resultWrong2)
        assertFalse(resultWrong3)
        assertFalse(resultEmpty)
    }
    
    @Test
    fun `setFacturesAuthenticated stores authentication status`() {
        // When
        sessionManager.setFacturesAuthenticated(true)
        
        // Then
        verify(mockEditor).putBoolean("factures_authenticated", true)
        verify(mockEditor).apply()
    }
    
    @Test
    fun `isFacturesAuthenticated returns false by default`() {
        // Given
        `when`(mockSharedPreferences.getBoolean("factures_authenticated", false)).thenReturn(false)
        
        // When
        val result = sessionManager.isFacturesAuthenticated()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `isFacturesAuthenticated returns true when authenticated`() {
        // Given
        `when`(mockSharedPreferences.getBoolean("factures_authenticated", false)).thenReturn(true)
        
        // When
        val result = sessionManager.isFacturesAuthenticated()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `logout clears Factures authentication`() {
        // When
        sessionManager.logout()
        
        // Then
        verify(mockEditor).remove("current_user")
        verify(mockEditor).remove("factures_authenticated")
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
}
