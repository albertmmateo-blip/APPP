package com.appp.avisos.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a single edit history entry for a note.
 * Each entry captures what changed, from what value to what value, when, and by whom.
 */
@Entity(
    tableName = "note_edit_history",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["note_id"]), Index(value = ["modified_by"])]
)
data class NoteEditHistory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    
    @ColumnInfo(name = "note_id")
    val noteId: Int,
    
    @ColumnInfo(name = "field_name")
    val fieldName: String,
    
    @ColumnInfo(name = "old_value")
    val oldValue: String?,
    
    @ColumnInfo(name = "new_value")
    val newValue: String?,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    
    @ColumnInfo(name = "modified_by")
    val modifiedBy: String? = null  // Username of the user who made the change
)
