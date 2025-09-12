# Parallel File Upload Feature

## What's New

The Nextcloud Android app now supports **parallel file uploads** to improve upload performance when you have multiple files to sync.

## How It Works

### Before (Sequential)
Files uploaded one at a time:
```
File1 ████████████████████ (Complete)
File2                     ████████████████████ (Complete)
File3                                         ████████████████████ (Complete)
```

### After (Parallel)
Multiple files upload simultaneously:
```
File1 ████████████████████ (Complete)
File2 ████████████████████ (Complete)  
File3 ████████████████████ (Complete)
```

## Configuration

### How to Enable
1. Open **Settings** → **Sync**
2. Find **"Max concurrent uploads"**
3. Set your preferred number (1-10)
4. Changes apply to new upload batches

### Recommended Settings
- **Slow connection**: 1-2 uploads
- **Fast WiFi**: 3-5 uploads  
- **Very fast connection**: 5-10 uploads

### Default Behavior
- **New users**: 1 upload (same as before)
- **Existing users**: 1 upload (no change needed)
- **Why**: Prevents overwhelming servers and maintains compatibility

## Benefits

### Performance
- ⚡ **Faster uploads** when you have good bandwidth
- 📊 **Better network utilization** 
- 🎛️ **User control** over upload speed vs. system load

### Compatibility
- ✅ **Backward compatible** - existing behavior unchanged
- 🔄 **Reversible** - can always go back to 1 upload
- 🛡️ **Safe defaults** - won't change behavior without user action

## Things to Consider

### When to Use More Uploads
- Fast WiFi or mobile connection
- Uploading many small files
- Server can handle the load
- Battery life not a concern

### When to Use Fewer Uploads
- Slow or unstable connection
- Large files (they'll max out bandwidth anyway)
- Older device or low battery
- Shared or limited server resources

### Technical Notes
- Each upload runs in its own thread
- Progress reporting is thread-safe
- Upload errors are handled independently
- Database operations remain synchronized

## Troubleshooting

### If Uploads Seem Slower
- Reduce concurrent uploads to 2-3
- Check your network connection
- Consider server capacity limits

### If App Uses Too Much Battery
- Reduce to 1-2 concurrent uploads
- Large files may benefit from sequential uploads

### If You Want the Old Behavior
- Set "Max concurrent uploads" to **1**
- This gives you exactly the same behavior as before

## Support

This feature maintains full backward compatibility. If you experience any issues:

1. Try reducing concurrent uploads to 1
2. Check that your uploads work normally
3. Gradually increase if needed
4. Report any bugs through normal channels

The implementation is designed to be safe and conservative - it won't change your experience unless you actively configure it to do so.