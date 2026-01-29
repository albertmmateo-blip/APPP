# Edit Log Enhancement - Verification Guide

## Overview
This guide provides step-by-step instructions for verifying the enhanced edit log feature once the build environment is available.

## Prerequisites
- Android development environment with network connectivity
- Android device or emulator (API 24+)
- Gradle and Android SDK properly configured

## Build and Test

### 1. Verify Build
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Expected: BUILD SUCCESSFUL
```

### 2. Run Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests com.appp.avisos.NoteEditHistoryTest

# Expected: 6 tests passed
```

**Unit Tests Included**:
- ✓ `noteEditHistory creation with all fields`
- ✓ `noteEditHistory creation with null values`
- ✓ `noteEditHistory with empty string old value`
- ✓ `noteEditHistory timestamp is valid`
- ✓ `noteEditHistory equality check`
- ✓ `noteEditHistory with different modifiedBy values are not equal`

### 3. Run Integration Tests
```bash
# Run instrumentation tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Expected: 6 tests passed
```

**Integration Tests Included**:
- ✓ `insertAndRetrieveEditHistory`
- ✓ `multipleEditHistoryEntries`
- ✓ `cascadeDeleteEditHistory`
- ✓ `getDistinctModifiersForNote`
- ✓ `filterEditHistoryByUser`
- ✓ `editHistoryWithNullModifiedBy`

### 4. Install and Launch
```bash
# Install on connected device
./gradlew installDebug

# Launch app
adb shell am start -n com.appp.avisos/.UserSelectionActivity
```

## Manual Testing Checklist

### User Tracking Verification

#### Test 1: Edit History with User
1. ✅ Launch app
2. ✅ Select a user (e.g., "Pedro") from user selection screen
3. ✅ Create a new note
4. ✅ View the note (no history should appear yet)
5. ✅ Edit the note (change name or body)
6. ✅ Save the changes
7. ✅ View the note details
8. ✅ Tap "View edit history"
9. ✅ **Verify**: History entry shows "DD/MM/YYYY HH:MM by Pedro"

**Expected Result**: User "Pedro" is displayed after the timestamp

#### Test 2: Multiple Users Editing Same Note
1. ✅ User "Pedro" creates and edits a note
2. ✅ Log out and log in as "Isa"
3. ✅ Edit the same note
4. ✅ View edit history
5. ✅ **Verify**: History shows entries from both "Pedro" and "Isa"

**Expected Result**: Different users are tracked separately

#### Test 3: Edit Without Logged-In User
1. ✅ Clear app data to reset user session
2. ✅ Use the app without selecting a user (if possible)
3. ✅ Edit a note
4. ✅ View edit history
5. ✅ **Verify**: History shows timestamp without username

**Expected Result**: History works even without logged-in user

### Database Migration Verification

#### Test 4: Upgrade from Version 5 to 6
1. ✅ Install previous version (before enhancements)
2. ✅ Create notes and edit them
3. ✅ Install new version (with enhancements)
4. ✅ Launch app
5. ✅ **Verify**: App launches without crashes
6. ✅ View existing note with history
7. ✅ **Verify**: Old history entries show (without username)
8. ✅ Edit the note again
9. ✅ **Verify**: New history entry shows with username

**Expected Result**: Smooth migration, old data preserved

### Filtering Verification (API Level)

#### Test 5: Filter by User (Programmatic)
```kotlin
// In a ViewModel or repository test with proper coroutine scope
lifecycleScope.launch {
    val noteId = 123
    // Get all modifiers
    val modifiers = repository.getDistinctModifiersForNote(noteId)
    Log.d("Test", "Modifiers: $modifiers")
    
    // Filter by specific user
    repository.getEditHistoryForNoteByUser(noteId, "Pedro").observe(viewLifecycleOwner) { history ->
        Log.d("Test", "Pedro's edits: ${history.size}")
    }
}
```

**Expected Result**: Filtering methods work correctly

#### Test 6: Filter by Date Range (Programmatic)
```kotlin
val noteId = 123
val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
val now = System.currentTimeMillis()

repository.getEditHistoryForNoteDateRange(noteId, sevenDaysAgo, now).observe(this) { history ->
    Log.d("Test", "Last 7 days: ${history.size} edits")
}
```

**Expected Result**: Date filtering works correctly

### Performance Verification

#### Test 7: Large History Performance
1. ✅ Create a note
2. ✅ Edit the note 50+ times (script this if possible)
3. ✅ View note details
4. ✅ Tap "View edit history"
5. ✅ **Verify**: History loads quickly (< 1 second)
6. ✅ Scroll through history
7. ✅ **Verify**: Smooth scrolling, no lag

**Expected Result**: Good performance even with many entries

#### Test 8: Multiple Notes with History
1. ✅ Create 20+ notes
2. ✅ Edit each note multiple times
3. ✅ Navigate between notes
4. ✅ View edit history for different notes
5. ✅ **Verify**: No performance degradation

**Expected Result**: Scales well with multiple notes

### Cascade Delete Verification

#### Test 9: Delete Note with History
1. ✅ Create a note
2. ✅ Edit it multiple times to build history
3. ✅ View edit history (confirm entries exist)
4. ✅ Delete the note (move to recycle bin)
5. ✅ Check database directly or restore and re-delete permanently
6. ✅ **Verify**: History entries are deleted when note is deleted

**Expected Result**: CASCADE delete prevents orphaned records

### Security Verification

#### Test 10: Data Privacy
1. ✅ Use Android Debug Database or SQLite browser
2. ✅ Inspect note_edit_history table
3. ✅ **Verify**: Modified_by column exists
4. ✅ **Verify**: User content is stored as expected
5. ✅ **Verify**: No sensitive data leakage in logs

**Expected Result**: Data stored securely, locally only

## Database Inspection

### Check Schema
```bash
# Connect to database via adb
adb shell
run-as com.appp.avisos
cd databases
sqlite3 appp_avisos_db

# Check table schema
.schema note_edit_history
```

**Expected Output**:
```sql
CREATE TABLE note_edit_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    note_id INTEGER NOT NULL,
    field_name TEXT NOT NULL,
    old_value TEXT,
    new_value TEXT,
    timestamp INTEGER NOT NULL,
    modified_by TEXT,
    FOREIGN KEY(note_id) REFERENCES notes(id) ON DELETE CASCADE
);
CREATE INDEX index_note_edit_history_note_id ON note_edit_history(note_id);
CREATE INDEX index_note_edit_history_modified_by ON note_edit_history(modified_by);
```

### Check Data
```sql
-- View all history entries
SELECT * FROM note_edit_history;

-- View entries with user info
SELECT field_name, modified_by, timestamp 
FROM note_edit_history 
WHERE modified_by IS NOT NULL
ORDER BY timestamp DESC;

-- Count entries per user
SELECT modified_by, COUNT(*) as edit_count
FROM note_edit_history
WHERE modified_by IS NOT NULL
GROUP BY modified_by
ORDER BY edit_count DESC;
```

## Automated Testing Script

```bash
#!/bin/bash
# automated_test.sh - Run all tests

echo "Running Edit Log Enhancement Tests..."
echo "======================================"

# Clean build
echo "1. Clean build..."
./gradlew clean

# Run unit tests
echo "2. Running unit tests..."
./gradlew test --tests com.appp.avisos.NoteEditHistoryTest
if [ $? -ne 0 ]; then
    echo "❌ Unit tests failed"
    exit 1
fi
echo "✓ Unit tests passed"

# Run integration tests (requires device)
echo "3. Running integration tests..."
./gradlew connectedAndroidTest --tests com.appp.avisos.NoteEditHistoryDaoTest
if [ $? -ne 0 ]; then
    echo "❌ Integration tests failed"
    exit 1
fi
echo "✓ Integration tests passed"

# Build APK
echo "4. Building debug APK..."
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi
echo "✓ Build successful"

# Install
echo "5. Installing on device..."
./gradlew installDebug
if [ $? -ne 0 ]; then
    echo "❌ Installation failed"
    exit 1
fi
echo "✓ Installation successful"

echo ""
echo "======================================"
echo "✅ All automated tests passed!"
echo "📱 App installed successfully"
echo "👉 Proceed with manual testing checklist"
echo "======================================"
```

## Troubleshooting

### Issue: Tests fail with "Cannot find symbol: NoteEditHistory"
**Solution**: Run `./gradlew clean` then rebuild

### Issue: Database migration fails
**Solution**: Clear app data and reinstall
```bash
adb shell pm clear com.appp.avisos
./gradlew installDebug
```

### Issue: "modified_by column not found"
**Solution**: Verify database version is 6
```sql
PRAGMA user_version;  -- Should return 6
```

### Issue: History not showing username
**Solution**: 
1. Verify user is logged in via UserSelectionActivity
2. Check UserSessionManager has current user
3. Debug log in NoteEditorViewModel.recordEditHistory()

### Issue: Performance issues with large history
**Solution**:
1. Verify indexes exist: `EXPLAIN QUERY PLAN SELECT * FROM note_edit_history WHERE note_id = ?`
2. Check index_note_edit_history_note_id and index_note_edit_history_modified_by exist

## Success Criteria

All checks should pass:
- ✅ All 12 tests pass (6 unit + 6 integration)
- ✅ Build succeeds without errors
- ✅ App installs and launches
- ✅ Edit history shows username after timestamp
- ✅ Multiple users are tracked separately
- ✅ Filtering methods work correctly
- ✅ Performance is acceptable (< 1s for large histories)
- ✅ Database migration succeeds
- ✅ Cascade delete works
- ✅ No data leakage or security issues

## Reporting Issues

If any tests fail or issues are found:

1. **Capture Logs**:
```bash
adb logcat -c  # Clear logs
# Reproduce issue
adb logcat > issue_log.txt
```

2. **Database State**:
```bash
adb exec-out run-as com.appp.avisos cat databases/appp_avisos_db > db_dump.db
```

3. **Test Output**:
```bash
./gradlew test --tests com.appp.avisos.NoteEditHistoryTest > test_output.txt 2>&1
```

4. **Create Issue** with:
   - Expected behavior
   - Actual behavior
   - Steps to reproduce
   - Logs and database dump

## Next Steps After Verification

Once all tests pass:
1. ✅ Code review
2. ✅ Security review
3. ✅ Performance profiling
4. ✅ Merge to main branch
5. ✅ Deploy to production

---

**Version**: 1.0  
**Last Updated**: 2026-01-29  
**Database Version**: 6  
**Test Coverage**: 12 tests (6 unit + 6 integration)
