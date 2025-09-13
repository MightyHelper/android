/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later OR GPL-2.0-only
 */
package com.nextcloud.client.jobs.upload;

import com.nextcloud.client.preferences.AppPreferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UploadExecutorService to verify thread pool management
 */
@RunWith(MockitoJUnitRunner.class)
public class UploadExecutorServiceTest {

    @Mock
    private AppPreferences preferences;

    @Test
    public void testSingleThreadExecutor() {
        // Given
        when(preferences.getMaxConcurrentUploads()).thenReturn(1);
        UploadExecutorService service = new UploadExecutorService(preferences);

        // When
        ExecutorService executor = service.getExecutorService();

        // Then
        assertNotNull("Executor should not be null", executor);
        assertTrue("Executor should not be shut down", !executor.isShutdown());
    }

    @Test
    public void testMultiThreadExecutor() {
        // Given
        when(preferences.getMaxConcurrentUploads()).thenReturn(3);
        UploadExecutorService service = new UploadExecutorService(preferences);

        // When
        ExecutorService executor = service.getExecutorService();

        // Then
        assertNotNull("Executor should not be null", executor);
        assertTrue("Executor should not be shut down", !executor.isShutdown());
    }

    @Test
    public void testExecutorRecreation() {
        // Given
        UploadExecutorService service = new UploadExecutorService(preferences);
        when(preferences.getMaxConcurrentUploads()).thenReturn(1);
        ExecutorService executor1 = service.getExecutorService();

        // When - change the preference
        when(preferences.getMaxConcurrentUploads()).thenReturn(3);
        ExecutorService executor2 = service.getExecutorService();

        // Then
        assertNotNull("First executor should not be null", executor1);
        assertNotNull("Second executor should not be null", executor2);
        // Note: We can't easily test if they're different instances without making fields package-private
    }

    @Test
    public void testShutdown() {
        // Given
        when(preferences.getMaxConcurrentUploads()).thenReturn(2);
        UploadExecutorService service = new UploadExecutorService(preferences);
        ExecutorService executor = service.getExecutorService();

        // When
        service.shutdown();

        // Then
        assertTrue("Executor should be shut down", executor.isShutdown());
    }
}