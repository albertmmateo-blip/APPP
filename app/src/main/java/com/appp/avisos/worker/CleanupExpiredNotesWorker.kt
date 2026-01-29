package com.appp.avisos.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.appp.avisos.database.AppDatabase
import com.appp.avisos.database.Note
import com.appp.avisos.repository.NoteRepository

/**
 * Worker that cleans up expired notes from the recycle bin.
 * This worker runs periodically to delete notes that have been in the recycle bin
 * for more than 15 days.
 */
class CleanupExpiredNotesWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Get database instance
            val database = AppDatabase.getInstance(applicationContext)
            val noteDao = database.noteDao()
            val editHistoryDao = database.noteEditHistoryDao()
            val repository = NoteRepository(noteDao, editHistoryDao)
            
            // Clean up expired notes (using configured retention period)
            repository.cleanupExpiredNotes(daysThreshold = Note.RECYCLE_BIN_RETENTION_DAYS)
            
            Result.success()
        } catch (e: Exception) {
            // If cleanup fails, retry
            Result.retry()
        }
    }
}
