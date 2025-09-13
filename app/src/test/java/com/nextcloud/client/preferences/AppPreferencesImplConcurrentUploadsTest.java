/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later OR GPL-2.0-only
 */
package com.nextcloud.client.preferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import android.content.SharedPreferences;
import android.content.Context;

import com.nextcloud.client.account.UserAccountManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;

/**
 * Unit tests for concurrent uploads preference functionality
 */
@RunWith(MockitoJUnitRunner.class)
public class AppPreferencesImplConcurrentUploadsTest {

    @Mock
    private Context context;

    @Mock
    private SharedPreferences preferences;

    @Mock
    private SharedPreferences.Editor editor;

    @Mock
    private UserAccountManager userAccountManager;

    @Test
    public void testGetMaxConcurrentUploadsDefault() {
        // Given
        when(preferences.getInt("max_concurrent_uploads", 1)).thenReturn(1);
        AppPreferencesImpl appPreferences = new AppPreferencesImpl(context, preferences, userAccountManager);

        // When
        int result = appPreferences.getMaxConcurrentUploads();

        // Then
        assertEquals("Default max concurrent uploads should be 1", 1, result);
    }

    @Test
    public void testSetMaxConcurrentUploadsValid() {
        // Given
        when(preferences.edit()).thenReturn(editor);
        when(editor.putInt(anyString(), anyInt())).thenReturn(editor);
        AppPreferencesImpl appPreferences = new AppPreferencesImpl(context, preferences, userAccountManager);

        // When
        appPreferences.setMaxConcurrentUploads(3);

        // Then
        verify(editor).putInt("max_concurrent_uploads", 3);
        verify(editor).apply();
    }

    @Test
    public void testSetMaxConcurrentUploadsClampedLow() {
        // Given
        when(preferences.edit()).thenReturn(editor);
        when(editor.putInt(anyString(), anyInt())).thenReturn(editor);
        AppPreferencesImpl appPreferences = new AppPreferencesImpl(context, preferences, userAccountManager);

        // When - try to set below minimum
        appPreferences.setMaxConcurrentUploads(0);

        // Then - should be clamped to 1
        verify(editor).putInt("max_concurrent_uploads", 1);
        verify(editor).apply();
    }

    @Test
    public void testSetMaxConcurrentUploadsClampedHigh() {
        // Given
        when(preferences.edit()).thenReturn(editor);
        when(editor.putInt(anyString(), anyInt())).thenReturn(editor);
        AppPreferencesImpl appPreferences = new AppPreferencesImpl(context, preferences, userAccountManager);

        // When - try to set above maximum
        appPreferences.setMaxConcurrentUploads(15);

        // Then - should be clamped to 10
        verify(editor).putInt("max_concurrent_uploads", 10);
        verify(editor).apply();
    }

    @Test
    public void testGetMaxConcurrentUploadsCustomValue() {
        // Given
        when(preferences.getInt("max_concurrent_uploads", 1)).thenReturn(5);
        AppPreferencesImpl appPreferences = new AppPreferencesImpl(context, preferences, userAccountManager);

        // When
        int result = appPreferences.getMaxConcurrentUploads();

        // Then
        assertEquals("Should return custom value", 5, result);
    }
}