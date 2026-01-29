package com.appp.avisos

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.appp.avisos.worker.CleanupExpiredNotesWorker
import java.util.concurrent.TimeUnit

/**
 * Application class for APPP Avisos.
 * Initializes background tasks and other app-wide configurations.
 */
class ApppAvisosApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Schedule periodic cleanup of expired notes
        scheduleCleanupWork()
    }

    /**
     * Schedule periodic work to clean up expired notes from the recycle bin.
     * This job runs once daily to delete notes that have been in the recycle bin
     * for more than 15 days.
     */
    private fun scheduleCleanupWork() {
        // Create constraints (no special requirements)
        val constraints = Constraints.Builder()
            .build()

        // Create a periodic work request that runs once a day
        val cleanupWorkRequest = PeriodicWorkRequestBuilder<CleanupExpiredNotesWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        // Enqueue the work request, replacing any existing work with the same name
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            CLEANUP_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if already scheduled
            cleanupWorkRequest
        )
    }

    companion object {
        private const val CLEANUP_WORK_NAME = "cleanup_expired_notes"
    }
}
