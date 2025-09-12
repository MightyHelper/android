# Parallel File Upload Implementation

## Overview

This implementation adds support for parallel file uploads to the Nextcloud Android app, allowing users to configure the maximum number of concurrent uploads for improved performance.

## Key Components

### 1. AppPreferences Configuration
- **New methods**: `getMaxConcurrentUploads()` and `setMaxConcurrentUploads(int)`
- **Default value**: 1 (maintains backward compatibility with sequential uploads)
- **Range**: 1-10 uploads (clamped automatically)
- **Preference key**: `max_concurrent_uploads`

### 2. UploadExecutorService
- **Purpose**: Manages thread pool for concurrent upload operations
- **Thread naming**: Upload threads are named `upload-worker-N` for debugging
- **Dynamic reconfiguration**: Recreates thread pool when settings change
- **Graceful shutdown**: Properly closes thread pool on worker stop

### 3. FileUploadWorker Enhancements
- **Dual mode operation**: 
  - Sequential mode (maxConcurrentUploads = 1): Original behavior preserved
  - Parallel mode (maxConcurrentUploads > 1): New concurrent execution
- **Thread safety**: Synchronized access to shared resources
- **Progress reporting**: Thread-safe progress updates with proper locking
- **Error handling**: Aggregated error tracking across parallel uploads

## Configuration

### User Interface
1. Open Settings → Sync
2. Find "Max concurrent uploads" setting
3. Set value between 1-10
4. Changes take effect on next upload batch

### Default Behavior
- **Default**: 1 concurrent upload (sequential mode)
- **Rationale**: Maintains existing behavior and prevents overwhelming servers
- **Migration**: Existing users will continue with sequential uploads unless changed

## Thread Safety

### Synchronized Components
- **UploadsStorageManager.updateUpload()**: Already thread-safe with `synchronized` keyword
- **Progress reporting**: New `progressLock` prevents race conditions
- **Current operation tracking**: Synchronized access to `currentUploadFileOperation`

### Thread Pool Management
- **Single thread**: Uses `Executors.newSingleThreadExecutor()` for maxConcurrentUploads = 1
- **Multiple threads**: Uses `Executors.newFixedThreadPool(n)` for n > 1
- **Cleanup**: Proper shutdown of thread pools when worker stops or preferences change

## Performance Considerations

### Benefits
- **Faster uploads**: Multiple files upload simultaneously
- **Better utilization**: Makes use of available network bandwidth
- **User control**: Users can optimize based on their connection and server capacity

### Limitations
- **Server load**: Higher concurrent uploads may stress the server
- **Battery impact**: More CPU usage with multiple threads
- **Network contention**: May not improve performance on slow connections

## Error Handling

### Individual Upload Failures
- Each upload runs independently
- Failures in one upload don't affect others
- Proper cleanup and notification for each failed upload

### Aggregate Results
- Worker succeeds if at least one upload succeeds
- Worker fails only if all uploads fail
- Error count tracked and logged for debugging

## Backward Compatibility

### Existing Behavior Preserved
- Default setting (1) maintains sequential uploads
- All existing notifications and progress reporting work unchanged
- No API changes for external consumers

### Migration Path
- Automatic: No migration needed, uses new default
- Manual: Users can opt-in to parallel uploads via settings
- Testing: Can always revert to sequential by setting value to 1

## Debugging and Monitoring

### Logging
- Upload thread creation/destruction logged
- Parallel vs sequential mode decisions logged
- Individual upload completion status logged
- Error aggregation results logged

### Thread Naming
- Upload threads named `upload-worker-N` for easy identification
- Helps in debugging performance issues and thread leaks

## Implementation Notes

### Code Changes
- **Minimal impact**: Preserved existing code paths for sequential uploads
- **Clean separation**: Parallel logic isolated in new methods
- **Thread safety**: Added only where necessary to avoid over-synchronization

### Testing Approach
- Unit tests for thread pool management
- Sequential upload behavior unchanged
- Progress reporting thread safety verified
- Preference validation (1-10 range) tested

## Future Enhancements

### Potential Improvements
- **Dynamic adjustment**: Automatically adjust concurrent uploads based on network conditions
- **Priority queuing**: Allow high-priority uploads to jump ahead
- **Bandwidth throttling**: Limit upload speed per thread
- **Advanced scheduling**: Upload during off-peak hours