/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later OR GPL-2.0-only
 */
package com.nextcloud.client.jobs.upload

import com.nextcloud.client.preferences.AppPreferences
import com.owncloud.android.lib.common.utils.Log_OC
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Service for managing concurrent file upload operations.
 * Provides a thread pool with configurable maximum concurrent uploads.
 */
class UploadExecutorService(private val preferences: AppPreferences) {

    companion object {
        private const val TAG = "UploadExecutorService"
        private const val THREAD_NAME_PREFIX = "upload-worker-"
    }

    private var executorService: ExecutorService? = null
    private var currentMaxConcurrentUploads = 0

    /**
     * Get or create the executor service with the current preference settings.
     * Recreates the executor if the max concurrent uploads setting has changed.
     */
    @Synchronized
    fun getExecutorService(): ExecutorService {
        val maxConcurrentUploads = preferences.maxConcurrentUploads

        // Create new executor if needed or if settings changed
        if (executorService == null || maxConcurrentUploads != currentMaxConcurrentUploads) {
            Log_OC.d(TAG, "Creating new upload executor with $maxConcurrentUploads threads")
            
            // Shutdown previous executor if it exists
            executorService?.shutdown()
            
            // Create new thread pool
            executorService = if (maxConcurrentUploads == 1) {
                // Use single thread executor for backward compatibility
                Executors.newSingleThreadExecutor(UploadThreadFactory())
            } else {
                // Use fixed thread pool for parallel uploads
                Executors.newFixedThreadPool(maxConcurrentUploads, UploadThreadFactory())
            }
            
            currentMaxConcurrentUploads = maxConcurrentUploads
        }

        return executorService!!
    }

    /**
     * Shutdown the executor service gracefully.
     */
    @Synchronized
    fun shutdown() {
        executorService?.let { executor ->
            Log_OC.d(TAG, "Shutting down upload executor")
            executor.shutdown()
            executorService = null
            currentMaxConcurrentUploads = 0
        }
    }

    /**
     * Custom thread factory for upload threads with proper naming.
     */
    private class UploadThreadFactory : ThreadFactory {
        private val threadNumber = AtomicInteger(1)

        override fun newThread(r: Runnable): Thread {
            val thread = Thread(r, THREAD_NAME_PREFIX + threadNumber.getAndIncrement())
            thread.isDaemon = false
            thread.priority = Thread.NORM_PRIORITY
            return thread
        }
    }
}