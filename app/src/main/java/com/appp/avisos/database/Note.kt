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
    val isUrgent: Boolean = false
)
