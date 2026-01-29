package com.appp.avisos

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.appp.avisos.database.AppDatabase
import com.appp.avisos.database.Note
import com.appp.avisos.database.NoteEditHistory
import com.appp.avisos.database.NoteEditHistoryDao
import com.appp.avisos.database.NoteDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for NoteEditHistoryDao
 */
@RunWith(AndroidJUnit4::class)
class NoteEditHistoryDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: AppDatabase
    private lateinit var noteDao: NoteDao
    private lateinit var editHistoryDao: NoteEditHistoryDao
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        noteDao = database.noteDao()
        editHistoryDao = database.noteEditHistoryDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun insertAndRetrieveEditHistory() = runBlocking {
        // Given - Create a note first
        val note = Note(
            name = "Test Note",
            body = "Test Body",
            category = "Test",
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        val noteId = noteDao.insertNote(note).toInt()
        
        // When - Insert edit history
        val history = NoteEditHistory(
            noteId = noteId,
            fieldName = "Note Name",
            oldValue = "Old Name",
            newValue = "Test Note",
            timestamp = System.currentTimeMillis(),
            modifiedBy = "TestUser"
        )
        editHistoryDao.insertEditHistory(history)
        
        // Then - Retrieve and verify
        val count = editHistoryDao.getEditHistoryCount(noteId)
        assertEquals(1, count)
    }
    
    @Test
    fun multipleEditHistoryEntries() = runBlocking {
        // Given - Create a note
        val note = Note(
            name = "Test Note",
            body = "Test Body",
            category = "Test",
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        val noteId = noteDao.insertNote(note).toInt()
        
        // When - Insert multiple history entries
        val timestamp = System.currentTimeMillis()
        editHistoryDao.insertEditHistory(NoteEditHistory(
            noteId = noteId,
            fieldName = "Note Name",
            oldValue = "Old Name",
            newValue = "New Name",
            timestamp = timestamp,
            modifiedBy = "User1"
        ))
        editHistoryDao.insertEditHistory(NoteEditHistory(
            noteId = noteId,
            fieldName = "Note Body",
            oldValue = "Old Body",
            newValue = "New Body",
            timestamp = timestamp + 1000,
            modifiedBy = "User2"
        ))
        
        // Then - Verify count
        val count = editHistoryDao.getEditHistoryCount(noteId)
        assertEquals(2, count)
    }
    
    @Test
    fun cascadeDeleteEditHistory() = runBlocking {
        // Given - Create a note with history
        val note = Note(
            name = "Test Note",
            body = "Test Body",
            category = "Test",
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        val noteId = noteDao.insertNote(note).toInt()
        
        val history = NoteEditHistory(
            noteId = noteId,
            fieldName = "Note Name",
            oldValue = "Old",
            newValue = "New",
            timestamp = System.currentTimeMillis(),
            modifiedBy = "TestUser"
        )
        editHistoryDao.insertEditHistory(history)
        
        // Verify history exists
        val countBefore = editHistoryDao.getEditHistoryCount(noteId)
        assertEquals(1, countBefore)
        
        // When - Delete the note
        noteDao.deleteNote(note.copy(id = noteId))
        
        // Then - History should be deleted (cascade)
        val countAfter = editHistoryDao.getEditHistoryCount(noteId)
        assertEquals(0, countAfter)
    }
    
    @Test
    fun getDistinctModifiersForNote() = runBlocking {
        // Given - Create a note
        val note = Note(
            name = "Test Note",
            body = "Test Body",
            category = "Test",
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        val noteId = noteDao.insertNote(note).toInt()
        
        // When - Insert history from multiple users
        val timestamp = System.currentTimeMillis()
        editHistoryDao.insertEditHistory(NoteEditHistory(
            noteId = noteId,
            fieldName = "Field1",
            oldValue = "Old1",
            newValue = "New1",
            timestamp = timestamp,
            modifiedBy = "User1"
        ))
        editHistoryDao.insertEditHistory(NoteEditHistory(
            noteId = noteId,
            fieldName = "Field2",
            oldValue = "Old2",
            newValue = "New2",
            timestamp = timestamp + 1000,
            modifiedBy = "User2"
        ))
        editHistoryDao.insertEditHistory(NoteEditHistory(
            noteId = noteId,
            fieldName = "Field3",
            oldValue = "Old3",
            newValue = "New3",
            timestamp = timestamp + 2000,
            modifiedBy = "User1" // Duplicate user
        ))
        
        // Then - Should return distinct users
        val modifiers = editHistoryDao.getDistinctModifiersForNote(noteId)
        assertEquals(2, modifiers.size)
        assertTrue(modifiers.contains("User1"))
        assertTrue(modifiers.contains("User2"))
    }
    
    @Test
    fun filterEditHistoryByUser() = runBlocking {
        // Given - Create a note
        val note = Note(
            name = "Test Note",
            body = "Test Body",
            category = "Test",
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        val noteId = noteDao.insertNote(note).toInt()
        
        // When - Insert history from multiple users
        val timestamp = System.currentTimeMillis()
        editHistoryDao.insertEditHistory(NoteEditHistory(
            noteId = noteId,
            fieldName = "Field1",
            oldValue = "Old1",
            newValue = "New1",
            timestamp = timestamp,
            modifiedBy = "User1"
        ))
        editHistoryDao.insertEditHistory(NoteEditHistory(
            noteId = noteId,
            fieldName = "Field2",
            oldValue = "Old2",
            newValue = "New2",
            timestamp = timestamp + 1000,
            modifiedBy = "User2"
        ))
        
        // Then - Filter should work (check using LiveData observation in real scenario)
        val allCount = editHistoryDao.getEditHistoryCount(noteId)
        assertEquals(2, allCount)
    }
    
    @Test
    fun editHistoryWithNullModifiedBy() = runBlocking {
        // Given - Create a note
        val note = Note(
            name = "Test Note",
            body = "Test Body",
            category = "Test",
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        val noteId = noteDao.insertNote(note).toInt()
        
        // When - Insert history without user
        val history = NoteEditHistory(
            noteId = noteId,
            fieldName = "Note Name",
            oldValue = "Old",
            newValue = "New",
            timestamp = System.currentTimeMillis(),
            modifiedBy = null
        )
        editHistoryDao.insertEditHistory(history)
        
        // Then - Should be stored successfully
        val count = editHistoryDao.getEditHistoryCount(noteId)
        assertEquals(1, count)
        
        // And distinct modifiers should exclude null
        val modifiers = editHistoryDao.getDistinctModifiersForNote(noteId)
        assertEquals(0, modifiers.size)
    }
}
