package com.appp.avisos.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room Database for the APPP Avisos application.
 *
 * This is a singleton database class that provides access to the Note entity
 * and its corresponding DAO.
 */
@Database(entities = [Note::class, NoteEditHistory::class], version = 8, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to Note data access object
     */
    abstract fun noteDao(): NoteDao
    
    /**
     * Provides access to Note Edit History data access object
     */
    abstract fun noteEditHistoryDao(): NoteEditHistoryDao
    
    companion object {
        // Singleton instance
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Migration from version 3 to 4: Add recycle bin fields
         */
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add recycle bin columns to notes table
                db.execSQL("ALTER TABLE notes ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE notes ADD COLUMN deleted_date INTEGER")
                db.execSQL("ALTER TABLE notes ADD COLUMN deletion_type TEXT")
            }
        }
        
        /**
         * Migration from version 4 to 5: Add author field
         */
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add author column to notes table
                db.execSQL("ALTER TABLE notes ADD COLUMN author TEXT")
            }
        }
        
        /**
         * Migration from version 5 to 6: Add modified_by field to edit history
         */
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add modified_by column to note_edit_history table
                db.execSQL("ALTER TABLE note_edit_history ADD COLUMN modified_by TEXT")
                // Create index on modified_by for efficient filtering
                db.execSQL("CREATE INDEX IF NOT EXISTS index_note_edit_history_modified_by ON note_edit_history(modified_by)")
            }
        }
        
        /**
         * Migration from version 6 to 7: Add subcategory field for Factures
         */
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add subcategory column to notes table
                db.execSQL("ALTER TABLE notes ADD COLUMN subcategory TEXT")
            }
        }
        
        /**
         * Migration from version 7 to 8: Add edition_number field to edit history
         */
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add edition_number column to note_edit_history table
                db.execSQL("ALTER TABLE note_edit_history ADD COLUMN edition_number INTEGER NOT NULL DEFAULT 0")
                // Create index on edition_number for efficient grouping
                db.execSQL("CREATE INDEX IF NOT EXISTS index_note_edit_history_edition_number ON note_edit_history(edition_number)")
            }
        }
        
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
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
