package com.appp.avisos.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database for the APPP Avisos application.
 * 
 * This is a singleton database class that provides access to the Note entity
 * and its corresponding DAO.
 */
@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to Note data access object
     */
    abstract fun noteDao(): NoteDao
    
    companion object {
        // Singleton instance
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Gets the singleton instance of AppDatabase.
         * 
         * @param context Application context
         * @return Singleton instance of AppDatabase
         */
        fun getInstance(context: Context): AppDatabase {
            // If instance already exists, return it
            return INSTANCE ?: synchronized(this) {
                // Double-check to ensure thread safety
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appp_avisos_db"
                )
                    .fallbackToDestructiveMigration() // For development, allows destructive migration
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
