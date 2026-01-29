package com.appp.avisos

import com.appp.avisos.database.NoteEditHistory
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for NoteEditHistory entity
 */
class NoteEditHistoryTest {
    
    @Test
    fun `noteEditHistory creation with all fields`() {
        // Given
        val noteId = 1
        val fieldName = "Note Name"
        val oldValue = "Old Name"
        val newValue = "New Name"
        val timestamp = System.currentTimeMillis()
        val modifiedBy = "TestUser"
        
        // When
        val history = NoteEditHistory(
            noteId = noteId,
            fieldName = fieldName,
            oldValue = oldValue,
            newValue = newValue,
            timestamp = timestamp,
            modifiedBy = modifiedBy
        )
        
        // Then
        assertEquals(noteId, history.noteId)
        assertEquals(fieldName, history.fieldName)
        assertEquals(oldValue, history.oldValue)
        assertEquals(newValue, history.newValue)
        assertEquals(timestamp, history.timestamp)
        assertEquals(modifiedBy, history.modifiedBy)
    }
    
    @Test
    fun `noteEditHistory creation with null values`() {
        // Given
        val noteId = 1
        val fieldName = "Contact"
        val timestamp = System.currentTimeMillis()
        
        // When
        val history = NoteEditHistory(
            noteId = noteId,
            fieldName = fieldName,
            oldValue = null,
            newValue = "New Contact",
            timestamp = timestamp,
            modifiedBy = null
        )
        
        // Then
        assertNull(history.oldValue)
        assertEquals("New Contact", history.newValue)
        assertNull(history.modifiedBy)
    }
    
    @Test
    fun `noteEditHistory with empty string old value`() {
        // Given
        val noteId = 1
        val fieldName = "Note Body"
        val timestamp = System.currentTimeMillis()
        
        // When
        val history = NoteEditHistory(
            noteId = noteId,
            fieldName = fieldName,
            oldValue = "",
            newValue = "New Body",
            timestamp = timestamp,
            modifiedBy = "User1"
        )
        
        // Then
        assertEquals("", history.oldValue)
        assertEquals("New Body", history.newValue)
    }
    
    @Test
    fun `noteEditHistory timestamp is valid`() {
        // Given
        val currentTime = System.currentTimeMillis()
        
        // When
        val history = NoteEditHistory(
            noteId = 1,
            fieldName = "Test Field",
            oldValue = "Old",
            newValue = "New",
            timestamp = currentTime,
            modifiedBy = "TestUser"
        )
        
        // Then
        assertTrue(history.timestamp <= System.currentTimeMillis())
        assertTrue(history.timestamp >= currentTime - 1000) // Within 1 second
    }
    
    @Test
    fun `noteEditHistory equality check`() {
        // Given
        val timestamp = System.currentTimeMillis()
        val history1 = NoteEditHistory(
            id = 1,
            noteId = 1,
            fieldName = "Name",
            oldValue = "Old",
            newValue = "New",
            timestamp = timestamp,
            modifiedBy = "User1"
        )
        val history2 = NoteEditHistory(
            id = 1,
            noteId = 1,
            fieldName = "Name",
            oldValue = "Old",
            newValue = "New",
            timestamp = timestamp,
            modifiedBy = "User1"
        )
        
        // Then
        assertEquals(history1, history2)
    }
    
    @Test
    fun `noteEditHistory with different modifiedBy values are not equal`() {
        // Given
        val timestamp = System.currentTimeMillis()
        val history1 = NoteEditHistory(
            id = 1,
            noteId = 1,
            fieldName = "Name",
            oldValue = "Old",
            newValue = "New",
            timestamp = timestamp,
            modifiedBy = "User1"
        )
        val history2 = NoteEditHistory(
            id = 1,
            noteId = 1,
            fieldName = "Name",
            oldValue = "Old",
            newValue = "New",
            timestamp = timestamp,
            modifiedBy = "User2"
        )
        
        // Then
        assertNotEquals(history1, history2)
    }
}
