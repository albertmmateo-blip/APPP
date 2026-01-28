# Comprehensive Implementation Guide - Appp Avisos (Offline Android App)

## Overview

This guide provides a complete, step-by-step sequence of prompts to implement the **Appp Avisos** offline Android notes and reminders application. Each prompt is designed to be fed to AI coding agents or used as a development checklist.

**Target Platform:** Android 7.0 (Nougat) or later  
**Development Environment:** Windows  
**Architecture:** Native Android (Java/Kotlin) with local SQLite database  
**Storage:** Completely offline, local storage only

---

## Table of Contents

1. [Phase 1: Project Setup](#phase-1-project-setup)
2. [Phase 2: Database Implementation](#phase-2-database-implementation)
3. [Phase 3: UI Layout & Navigation](#phase-3-ui-layout--navigation)
4. [Phase 4: CRUD Operations](#phase-4-crud-operations)
5. [Phase 5: Testing & Validation](#phase-5-testing--validation)
6. [Phase 6: Polish & Deployment](#phase-6-polish--deployment)
7. [Windows-Specific Setup](#windows-specific-setup)

---

## Phase 1: Project Setup

### Prompt 1.1: Initialize Android Project

```
Create a new Android application project with the following specifications:
- Project name: "ApppAvisos"
- Package name: "com.appp.avisos"
- Minimum SDK: API 24 (Android 7.0 Nougat)
- Target SDK: Latest stable Android API
- Language: Kotlin (preferred) or Java
- Build system: Gradle with Kotlin DSL
- Include AndroidX libraries
- Activity type: Empty Activity with ViewBinding enabled

Set up the project structure with the following directories:
- app/src/main/java/com/appp/avisos/
- app/src/main/res/layout/
- app/src/main/res/values/
- app/src/main/res/drawable/
```

### Prompt 1.2: Configure Gradle Dependencies

```
Add the following dependencies to the app's build.gradle file:

Essential dependencies:
1. AndroidX Core KTX
2. AndroidX AppCompat
3. Material Design Components (com.google.android.material)
4. ConstraintLayout
5. Room Database (runtime, ktx, and compiler)
6. Lifecycle components (ViewModel, LiveData)
7. RecyclerView
8. CardView

Configure:
- ViewBinding: enabled
- Java compatibility: Java 8
- Kotlin compiler options if using Kotlin
```

### Prompt 1.3: Define App Colors and Themes

```
Create a Material Design color scheme for the app:

Define colors in res/values/colors.xml:
- Primary color: A pleasant blue or teal (for app bar)
- Primary variant: Darker shade of primary
- Secondary color: Complementary accent color
- Background color: Light neutral
- Surface color: White or very light gray
- Error color: Standard red for warnings
- Text colors: Dark gray for primary text, medium gray for secondary

Create themes in res/values/themes.xml:
- Base theme extending Material Components
- Define colorPrimary, colorPrimaryVariant, colorSecondary
- Set up status bar and navigation bar colors
- Configure action bar/app bar styling
```

### Prompt 1.4: Set Up String Resources

```
Create all string resources in res/values/strings.xml for:

App name and titles:
- App name: "Appp Avisos"
- Activity titles for each screen

Category names:
- Trucar (Calls)
- Encarregar (Orders)
- Factures (Bills)
- Notes (General Notes)

Form labels:
- Note Name, Note Body, Contact, Category
- Save, Delete, Cancel buttons

Messages:
- Validation errors (empty name/body)
- Delete confirmation dialog
- Success/error messages

Ensure all user-facing text is externalized for future localization.
```

---

## Phase 2: Database Implementation

### Prompt 2.1: Create Note Entity

```
Create a Room database entity class for Note with:

Fields:
- id: Int (Primary key, auto-generated)
- name: String (not null) - Short title of the note
- body: String (not null) - Main content
- contact: String? (nullable) - Optional contact information
- category: String (not null) - One of: "trucar", "encarregar", "factures", "notes"
- createdDate: Long (timestamp in milliseconds)
- modifiedDate: Long (timestamp in milliseconds)

Entity annotations:
- @Entity(tableName = "notes")
- @PrimaryKey(autoGenerate = true)
- @ColumnInfo for each field

Include validation:
- Non-empty name and body
- Valid category from enum or constant list
```

### Prompt 2.2: Create Data Access Object (DAO)

```
Create a Room DAO interface for Note operations:

Required methods:
1. @Insert: insertNote(note: Note): Long
2. @Update: updateNote(note: Note)
3. @Delete: deleteNote(note: Note)
4. @Query: getAllNotes(): LiveData<List<Note>>
5. @Query: getNotesByCategory(category: String): LiveData<List<Note>>
6. @Query: getNoteById(id: Int): Note?

Queries should:
- Sort by modifiedDate descending (newest first)
- Use LiveData for reactive updates
- Include proper SQL syntax for SQLite
```

### Prompt 2.3: Create Room Database Class

```
Create the Room Database class:

Configuration:
- @Database annotation with entities = [Note::class], version = 1
- Abstract class extending RoomDatabase
- Abstract method to get NoteDao
- Singleton pattern with getInstance() method
- Database name: "appp_avisos_db"

Features:
- Thread-safe initialization using synchronized block
- Fallback to destructive migration for development
- Optional: Pre-populate database with sample data for testing
```

### Prompt 2.4: Create Repository Pattern

```
Create a NoteRepository class to abstract data access:

Responsibilities:
- Hold reference to NoteDao
- Provide methods matching DAO operations
- Execute database operations on background thread
- Return LiveData for UI observation

Methods:
- getAllNotes(): LiveData<List<Note>>
- getNotesByCategory(category: String): LiveData<List<Note>>
- insertNote(note: Note)
- updateNote(note: Note)
- deleteNote(note: Note)

Use Kotlin coroutines or AsyncTask for background operations.
```

---

## Phase 3: UI Layout & Navigation

### Prompt 3.1: Create Main Activity Layout with TabLayout

```
Design the main activity layout (activity_main.xml):

Structure:
- Root: ConstraintLayout or CoordinatorLayout
- AppBar with title "Appp Avisos"
- TabLayout with 4 fixed tabs:
  * Trucar (with phone icon 📞)
  * Encarregar (with box icon 📦)
  * Factures (with receipt icon 🧾)
  * Notes (with note icon 📝)
- ViewPager2 or RecyclerView to show notes list
- FloatingActionButton (+) in bottom-right corner

Styling:
- Material Design elevation and shadows
- Proper spacing and margins
- Tab indicators with appropriate colors
- FAB with accent color
```

### Prompt 3.2: Create Note List Item Layout

```
Design the note card layout (item_note.xml):

CardView containing:
- Note name (TextView, bold, larger text)
- Note body (TextView, up to 2 lines, ellipsize)
- Contact info (TextView, smaller, with icon if provided)
- Modified date (TextView, smallest, gray color)

Layout:
- Use ConstraintLayout inside CardView
- Proper padding (16dp)
- Card elevation (4dp)
- Ripple effect for click feedback
- Margins between cards (8dp)

Icons:
- Person icon for contact field
- Calendar icon for date field
```

### Prompt 3.3: Create Note Editor Layout

```
Design the note editor/form layout (activity_note_editor.xml):

Form fields (top to bottom):
1. TextInputLayout with EditText for Note Name
   - Hint: "Note Name"
   - Required indicator
   - Max length: 100 characters

2. TextInputLayout with EditText for Note Body
   - Hint: "Note Body"
   - Multiline (minLines: 3)
   - Required indicator

3. TextInputLayout with EditText for Contact
   - Hint: "Contact (optional)"
   - Single line

4. Spinner/DropDown for Category
   - All 4 categories
   - Pre-selected based on current tab

Bottom action bar:
- SAVE button (primary)
- DELETE button (red/warning color)
- CANCEL button

Use ScrollView to handle keyboard and different screen sizes.
```

### Prompt 3.4: Implement RecyclerView Adapter

```
Create a NotesAdapter for RecyclerView:

Requirements:
- Extend RecyclerView.Adapter
- ViewHolder pattern with ViewBinding
- Display note data in card layout
- Format dates (dd/MM/yyyy HH:mm)
- Handle item click events (open editor)
- DiffUtil for efficient updates

Methods:
- onCreateViewHolder: Inflate item layout
- onBindViewHolder: Bind note data to views
- getItemCount: Return list size
- submitList: Update adapter data

Click listener:
- Interface or lambda for item clicks
- Pass note ID or full note object
```

---

## Phase 4: CRUD Operations

### Prompt 4.1: Implement MainActivity with Tabs

```
Implement MainActivity.kt to handle:

Setup:
1. Initialize ViewBinding
2. Set up TabLayout with 4 tabs
3. Initialize RecyclerView with adapter
4. Set up tab selection listener
5. Observe LiveData from ViewModel

Tab switching logic:
- When tab selected, filter notes by category
- Update RecyclerView with filtered notes
- Smooth transition animations

FAB click:
- Open NoteEditorActivity
- Pass current category as default
- Use Intent extras

Features:
- Handle empty states (show message when no notes)
- Pull-to-refresh (optional)
- Handle back press
```

### Prompt 4.2: Create ViewModel for MainActivity

```
Create MainViewModel extending ViewModel:

Properties:
- Private MutableLiveData for current category
- LiveData<List<Note>> exposed to Activity
- Reference to NoteRepository

Methods:
- setCategory(category: String): Update current filter
- getNotesForCategory(): Return filtered LiveData
- Use MediatorLiveData or switchMap for reactive filtering

Lifecycle:
- Survive configuration changes
- Clean up resources in onCleared()
```

### Prompt 4.3: Implement Note Editor Activity

```
Create NoteEditorActivity for create/edit:

Modes:
1. CREATE mode: Empty form with category pre-selected
2. EDIT mode: Load existing note data

Intent extras:
- "NOTE_ID" (Int) - For edit mode, -1 for create
- "CATEGORY" (String) - Pre-selected category

Form handling:
1. Load note if in edit mode
2. Populate fields with note data
3. Validate on save:
   - Name not empty
   - Body not empty
   - Show errors using TextInputLayout
4. On valid save:
   - Create/update note with timestamps
   - Save to database via repository
   - Return to MainActivity

Delete button:
- Only visible in edit mode
- Show confirmation dialog
- Delete from database
- Return to MainActivity
```

### Prompt 4.4: Create ViewModel for Note Editor

```
Create NoteEditorViewModel:

Properties:
- LiveData<Note?> for current note
- Repository reference
- Validation state

Methods:
- loadNote(id: Int): Load for editing
- saveNote(name, body, contact, category): Validate and save
- deleteNote(): Delete current note
- validateInput(): Return validation result

Save logic:
- If new note: set createdDate and modifiedDate to now
- If existing: update modifiedDate only
- Use coroutines for database operations
- Post result (success/error) to LiveData
```

### Prompt 4.5: Implement Delete Confirmation Dialog

```
Create a reusable confirmation dialog:

AlertDialog with:
- Title: "Delete Note?"
- Message: "This action cannot be undone"
- Positive button: "DELETE" (red)
- Negative button: "CANCEL"

Implementation:
- MaterialAlertDialogBuilder
- Handle positive click: Delete note
- Handle negative/cancel: Dismiss dialog
- Prevent accidental deletion

Optional: Include note name in message for clarity
```

---

## Phase 5: Testing & Validation

### Prompt 5.1: Create Unit Tests for Repository

```
Create unit tests for NoteRepository:

Test cases:
1. Insert note successfully
2. Update existing note
3. Delete note
4. Retrieve notes by category
5. Verify notes sorted by date (newest first)

Use:
- JUnit 4 or 5
- Room in-memory database
- Kotlin coroutines test utilities
- MockK or Mockito if needed

Test isolation:
- Create fresh database for each test
- Clean up after tests
```

### Prompt 5.2: Create Instrumentation Tests for Database

```
Create Android instrumentation tests:

Test scenarios:
1. Database creation and migration
2. CRUD operations work correctly
3. Query filtering by category
4. Date ordering
5. Constraint validation (non-null fields)

Use:
- AndroidX Test framework
- Espresso for UI tests (if needed)
- In-memory database for speed
- Run on emulator or device
```

### Prompt 5.3: Create UI Tests for Main Flow

```
Create Espresso UI tests for:

Test flows:
1. App launches successfully
2. All 4 tabs are visible and clickable
3. FAB opens editor activity
4. Create new note in each category
5. Verify note appears in correct tab
6. Edit existing note
7. Delete note with confirmation
8. Cancel operations work correctly

Assertions:
- Verify UI elements are displayed
- Check RecyclerView item count
- Validate text content
- Test navigation flow
```

### Prompt 5.4: Manual Testing Checklist

```
Perform manual testing:

Functional tests:
- [ ] Create note with all fields filled
- [ ] Create note with only required fields
- [ ] Try to save note with empty name (should show error)
- [ ] Try to save note with empty body (should show error)
- [ ] Edit existing note and verify changes saved
- [ ] Delete note and confirm it's removed
- [ ] Switch between tabs and verify notes filtered correctly
- [ ] Verify dates are formatted correctly
- [ ] Test on different Android versions (7.0+)
- [ ] Test on different screen sizes
- [ ] Rotate device and verify state preserved

Data persistence:
- [ ] Close app and reopen - notes should remain
- [ ] Force stop app - notes should remain
- [ ] Restart device - notes should remain

Offline functionality:
- [ ] Enable airplane mode
- [ ] Verify all features work without internet
- [ ] Disable airplane mode - app continues working

Edge cases:
- [ ] Very long note name (100+ characters)
- [ ] Very long note body (1000+ characters)
- [ ] Special characters in all fields
- [ ] Create 50+ notes, test performance
```

---

## Phase 6: Polish & Deployment

### Prompt 6.1: Add App Icon and Splash Screen

```
Design and implement app branding:

App icon:
- Create launcher icon in multiple resolutions
- Use Android Studio Image Asset tool
- Include adaptive icon for Android 8.0+
- Icons for: mipmap-mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi

Splash screen (optional):
- Simple splash activity with app logo
- 2-second delay or until data loaded
- Use theme to avoid white flash

Resources needed:
- Icon design (simple, recognizable)
- App colors matching brand
```

### Prompt 6.2: Implement Empty States

```
Add empty state views for better UX:

When no notes in category:
- Centered message: "No notes yet"
- Icon (relevant to category)
- Hint text: "Tap + to create your first note"

Empty state design:
- Subtle gray colors
- Large icon (48-64dp)
- Clear, friendly text
- Centered in screen

Implementation:
- Show/hide based on list size
- Animate transition
- Include in each tab
```

### Prompt 6.3: Add Input Validation and Error Handling

```
Enhance input validation:

Note editor:
- Real-time validation as user types
- Clear error messages:
  * "Name is required"
  * "Note body is required"
  * "Name cannot exceed 100 characters"
- Disable save button until form valid (optional)

Error handling:
- Try-catch for database operations
- Toast messages for errors
- Log errors for debugging
- Graceful degradation

Edge cases:
- Handle database corruption
- Handle storage full scenario
- Handle null/empty strings
```

### Prompt 6.4: Optimize Performance

```
Performance optimizations:

Database:
- Index on category column for fast filtering
- Index on modifiedDate for sorting
- Consider pagination for large lists

UI:
- Use ViewBinding instead of findViewById
- Implement DiffUtil in RecyclerView
- Lazy loading for images (if added later)
- Avoid nested layouts where possible

Memory:
- Properly manage LiveData observers
- Cancel coroutines when Activity destroyed
- Use weak references if needed
```

### Prompt 6.5: Add Analytics and Logging (Optional)

```
Implement logging for debugging:

Log important events:
- App launch
- Note created/updated/deleted
- Database operations
- Errors and exceptions

Use:
- Android Logcat with tags
- Different log levels (DEBUG, INFO, ERROR)
- Timber library (optional, better logging)

Privacy:
- Do NOT log user content (note names/bodies)
- Only log actions and errors
- No analytics/tracking (app is offline)
```

### Prompt 6.6: Prepare for Release

```
Final release preparation:

Code quality:
- Run lint checks and fix warnings
- Remove debug logs
- Clean up unused resources
- Format code consistently
- Add code comments for complex logic

Build configuration:
- Set versionName (1.0.0)
- Set versionCode (1)
- Configure ProGuard/R8 for release
- Enable minification
- Sign app with release keystore

Testing:
- Run all unit tests
- Run all instrumentation tests
- Perform full manual testing
- Test on multiple devices/emulators
- Test on minimum SDK version

Documentation:
- Update README.md
- Update USER_GUIDE.md
- Add inline code documentation
- Create CHANGELOG.md
```

### Prompt 6.7: Generate Signed APK

```
Build and sign release APK:

Steps:
1. Generate keystore (if not exists):
   - Use Android Studio or keytool
   - Store securely (never commit to git)
   
2. Configure signing in build.gradle:
   - Add signingConfigs
   - Use release config for release builds

3. Build release APK:
   - Build → Generate Signed Bundle/APK
   - Select APK
   - Choose keystore and passwords
   - Select release variant
   - Build

4. Test signed APK:
   - Install on device
   - Verify all features work
   - Check APK size

5. Output:
   - app/release/app-release.apk
   - Ready for distribution
```

---

## Windows-Specific Setup

### Setup 1: Install Required Software on Windows

```
Install the following on your Windows machine:

1. Java Development Kit (JDK):
   - Download JDK 11 or later
   - Add to PATH environment variable
   - Verify: `java -version` in cmd

2. Android Studio:
   - Download from developer.android.com
   - Install with default settings
   - Install Android SDK (API 24+)
   - Install Android Emulator

3. Android SDK Tools:
   - SDK Platform-Tools
   - SDK Build-Tools (latest)
   - Android SDK Command-line Tools

4. Optional but recommended:
   - Git for Windows
   - Visual Studio Code (for markdown editing)

5. Configure environment variables:
   - ANDROID_HOME: Path to Android SDK
   - Add platform-tools to PATH
```

### Setup 2: Create Android Virtual Device (Emulator)

```
Set up Android emulator in Android Studio:

1. Open AVD Manager:
   - Tools → Device Manager
   
2. Create Virtual Device:
   - Click "Create Device"
   - Select phone hardware (Pixel 4 recommended)
   - Select system image: API 24 (Nougat) or later
   - Configure AVD:
     * Name: "Appp_Avisos_Test"
     * Orientation: Portrait
     * Graphics: Automatic or Hardware
   
3. Start emulator:
   - Launch AVD
   - Wait for boot
   - Verify home screen loads

Alternative: Use physical Android device
- Enable Developer Options
- Enable USB Debugging
- Connect via USB
```

### Setup 3: Windows Command Line Tips

```
Useful Windows commands for Android development:

Building:
- gradlew.bat clean
- gradlew.bat build
- gradlew.bat assembleDebug
- gradlew.bat installDebug

Testing:
- gradlew.bat test
- gradlew.bat connectedAndroidTest

ADB commands:
- adb devices (list connected devices)
- adb install app-debug.apk
- adb uninstall com.appp.avisos
- adb logcat (view logs)

Gradle daemon:
- gradlew.bat --stop (stop daemon)
- gradlew.bat --status (check status)

Note: Use backslashes for paths on Windows
      Or use forward slashes (they work too)
```

---

## Implementation Order Summary

Follow this order for most efficient implementation:

**Week 1: Foundation**
1. Complete Phase 1 (Project Setup)
2. Complete Phase 2 (Database)
3. Test database operations

**Week 2: UI**
4. Complete Phase 3 (UI Layouts)
5. Start Phase 4 (CRUD - MainActivity first)
6. Test basic navigation

**Week 3: Features**
7. Complete Phase 4 (CRUD - Editor Activity)
8. Implement all operations
9. Manual testing

**Week 4: Quality**
10. Complete Phase 5 (Testing)
11. Fix bugs found during testing
12. Complete Phase 6 (Polish)

**Week 5: Release**
13. Final testing
14. Build signed APK
15. Documentation updates

---

## Quick Reference: Key Features Checklist

Core features that MUST be implemented:

- [ ] 4 category tabs (Trucar, Encarregar, Factures, Notes)
- [ ] Create notes with name, body, contact, category
- [ ] Edit existing notes
- [ ] Delete notes with confirmation
- [ ] View notes filtered by category
- [ ] Sort by modified date (newest first)
- [ ] Offline-only operation (no internet required)
- [ ] Local SQLite database
- [ ] Material Design UI
- [ ] Date formatting (dd/MM/yyyy HH:mm)
- [ ] Empty state handling
- [ ] Input validation
- [ ] Works on Android 7.0+

---

## Troubleshooting Common Issues

### Issue: Gradle build fails on Windows

**Solution:**
- Ensure JAVA_HOME is set correctly
- Check Android SDK path has no spaces
- Run `gradlew.bat clean` then rebuild
- Delete .gradle folder and sync again

### Issue: Emulator won't start

**Solution:**
- Enable virtualization in BIOS (VT-x/AMD-V)
- Install HAXM (Intel) or Hyper-V
- Try different system image
- Use ARM image if x86 fails

### Issue: Database not persisting

**Solution:**
- Check database initialization
- Verify singleton pattern
- Check app permissions (shouldn't need any for local DB)
- Clear app data and reinstall

### Issue: Notes not updating in RecyclerView

**Solution:**
- Verify LiveData observation
- Check DiffUtil implementation
- Ensure adapter.submitList() is called
- Check RecyclerView adapter is set

---

## Additional Resources

**Official Documentation:**
- Android Developer Guides: developer.android.com
- Material Design Guidelines: material.io
- Room Database: developer.android.com/training/data-storage/room
- RecyclerView: developer.android.com/guide/topics/ui/layout/recyclerview

**Code Examples:**
- Android Architecture Components Samples
- Google Samples on GitHub
- Android Sunflower sample app

**Tools:**
- Android Studio: developer.android.com/studio
- Gradle: gradle.org
- Kotlin: kotlinlang.org

---

## Notes for AI Agents

When using these prompts with AI coding agents:

1. **Feed prompts sequentially** - Complete each phase before moving to next
2. **Validate output** - Test each component after implementation
3. **Provide context** - Share previous outputs when relevant
4. **Adjust as needed** - Modify prompts based on your specific stack
5. **Test incrementally** - Don't wait until the end to test

Remember: This is a simple app by design. Don't over-engineer it.

---

## Conclusion

This comprehensive guide provides all the prompts needed to implement the Appp Avisos offline Android application from scratch. Follow the phases in order, test thoroughly, and you'll have a functional, polished app ready for use.

**Estimated Time:** 3-5 weeks for complete implementation (including learning if new to Android)

**Good luck with your implementation!** 🚀
