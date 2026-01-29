package com.appp.avisos.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "body")
    val body: String,
    
    @ColumnInfo(name = "contact")
    val contact: String? = null,
    
    @ColumnInfo(name = "category")
    val category: String,
    
    @ColumnInfo(name = "created_date")
    val createdDate: Long,
    
    @ColumnInfo(name = "modified_date")
    val modifiedDate: Long,
    
    @ColumnInfo(name = "is_urgent", defaultValue = "0")
    val isUrgent: Boolean = false,
    
    // Recycle Bin fields
    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    val isDeleted: Boolean = false,
    
    @ColumnInfo(name = "deleted_date")
    val deletedDate: Long? = null,
    
    @ColumnInfo(name = "deletion_type")
    val deletionType: String? = null,  // "Esborrades" or "Finalitzades"
    
    @ColumnInfo(name = "author")
    val author: String? = null  // Username of the note creator
) {
    companion object {
        const val DELETION_TYPE_ESBORRADES = "Esborrades"
        const val DELETION_TYPE_FINALITZADES = "Finalitzades"
        
        // Recycle bin configuration
        const val RECYCLE_BIN_RETENTION_DAYS = 15
        const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    }
}
