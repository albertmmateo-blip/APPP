# AI Agent Prompts Cheat Sheet - Appp Avisos

Quick reference of all prompts for implementing the Appp Avisos offline Android app. Use these prompts sequentially with AI coding agents.

## Phase 1: Project Setup (4 prompts)

### 1.1 Initialize Project
```
Create a new Android application project:
- Project name: "ApppAvisos"
- Package: "com.appp.avisos"
- Minimum SDK: API 24 (Android 7.0)
- Language: Kotlin
- Enable ViewBinding
```

### 1.2 Configure Dependencies
```
Add to build.gradle:
- Material Design Components
- Room Database (runtime, ktx, compiler)
- Lifecycle components (ViewModel, LiveData)
- RecyclerView, CardView
- ConstraintLayout
```

### 1.3 Define Colors and Themes
```
Create Material Design color scheme in colors.xml and themes.xml:
- Primary color (blue/teal)
- Secondary color
- Background, surface, error colors
- Dark/light text colors
```

### 1.4 Set Up Strings
```
Create string resources for:
- App name: "Appp Avisos"
- Category names: Trucar, Encarregar, Factures, Notes
- Form labels: Note Name, Note Body, Contact, Category
- Button labels: Save, Delete, Cancel
- Validation messages
```

---

## Phase 2: Database (4 prompts)

### 2.1 Create Note Entity
```
Create Room entity with fields:
- id (Int, primary key, auto-generated)
- name (String, required)
- body (String, required)
- contact (String?, optional)
- category (String, required: trucar/encarregar/factures/notes)
- createdDate (Long, timestamp)
- modifiedDate (Long, timestamp)
```

### 2.2 Create DAO
```
Create Room DAO with methods:
- insertNote(note): Long
- updateNote(note)
- deleteNote(note)
- getAllNotes(): LiveData<List<Note>>
- getNotesByCategory(category): LiveData<List<Note>>
- Sort by modifiedDate descending
```

### 2.3 Create Database Class
```
Create Room Database:
- Singleton pattern
- Version 1
- Database name: "appp_avisos_db"
- Include Note entity
- Destructive migration for dev
```

### 2.4 Create Repository
```
Create NoteRepository:
- Wrap DAO methods
- Execute database operations on background thread
- Return LiveData for UI
- Use Kotlin coroutines
```

---

## Phase 3: UI Layouts (4 prompts)

### 3.1 Main Activity Layout
```
Create activity_main.xml:
- AppBar with "Appp Avisos" title
- TabLayout with 4 tabs (icons + text)
- RecyclerView for notes list
- FloatingActionButton (+) bottom-right
```

### 3.2 Note Card Layout
```
Create item_note.xml:
- CardView with elevation
- Note name (bold, larger)
- Note body (2 lines max, ellipsize)
- Contact info with icon
- Modified date (small, gray)
- Ripple effect on click
```

### 3.3 Note Editor Layout
```
Create activity_note_editor.xml:
- TextInputLayout for Note Name (required)
- TextInputLayout for Note Body (multiline, required)
- TextInputLayout for Contact (optional)
- Spinner for Category
- Save, Delete, Cancel buttons
- ScrollView wrapper
```

### 3.4 RecyclerView Adapter
```
Create NotesAdapter:
- ViewHolder with ViewBinding
- Display note data
- Format dates: dd/MM/yyyy HH:mm
- Handle item click events
- Use DiffUtil for updates
```

---

## Phase 4: CRUD Operations (5 prompts)

### 4.1 Implement MainActivity
```
Implement MainActivity:
- Set up TabLayout (4 tabs)
- Set up RecyclerView with adapter
- Tab selection listener (filter by category)
- FAB click opens NoteEditorActivity
- Observe LiveData from ViewModel
- Handle empty states
```

### 4.2 Create MainViewModel
```
Create MainViewModel:
- MutableLiveData for current category
- LiveData<List<Note>> for filtered notes
- setCategory(category) method
- Use MediatorLiveData for reactive filtering
- Reference to NoteRepository
```

### 4.3 Implement Note Editor
```
Create NoteEditorActivity:
- CREATE mode (empty form)
- EDIT mode (load existing note)
- Validate name and body not empty
- Save note with timestamps
- Delete button (edit mode only)
- Confirmation dialog for delete
```

### 4.4 Create Editor ViewModel
```
Create NoteEditorViewModel:
- loadNote(id) for editing
- saveNote(name, body, contact, category)
- deleteNote()
- Input validation
- Set timestamps correctly
```

### 4.5 Confirmation Dialog
```
Create delete confirmation dialog:
- Title: "Delete Note?"
- Message: "This action cannot be undone"
- Positive: "DELETE" (red)
- Negative: "CANCEL"
- Use MaterialAlertDialogBuilder
```

---

## Phase 5: Testing (4 prompts)

### 5.1 Repository Unit Tests
```
Create tests for NoteRepository:
- Insert note
- Update note
- Delete note
- Retrieve by category
- Verify date sorting
Use in-memory database
```

### 5.2 Database Instrumentation Tests
```
Create Android instrumentation tests:
- Database CRUD operations
- Query filtering
- Date ordering
- Constraint validation
```

### 5.3 UI Tests
```
Create Espresso tests:
- App launches
- All tabs visible
- Create note flow
- Edit note flow
- Delete note flow
- Tab switching
```

### 5.4 Manual Testing
```
Manual test checklist:
- Create notes (all fields, required only)
- Validation errors (empty name/body)
- Edit and delete notes
- Tab switching
- Data persistence (close/reopen app)
- Offline functionality
- Edge cases (long text, special characters)
```

---

## Phase 6: Polish & Deployment (7 prompts)

### 6.1 App Icon & Splash
```
Design app branding:
- Create launcher icon (multiple densities)
- Adaptive icon for Android 8.0+
- Optional splash screen
```

### 6.2 Empty States
```
Add empty state views:
- "No notes yet" message
- Relevant icon
- "Tap + to create" hint
- Centered, friendly design
```

### 6.3 Input Validation
```
Enhanced validation:
- Real-time validation
- Clear error messages
- Character limits
- Error handling for database operations
```

### 6.4 Performance Optimization
```
Optimize:
- Index category and modifiedDate columns
- Use ViewBinding
- Implement DiffUtil
- Manage LiveData observers properly
```

### 6.5 Logging
```
Implement logging:
- Important events (create/edit/delete)
- Errors and exceptions
- Use Logcat with tags
- Do NOT log user content
```

### 6.6 Release Preparation
```
Final prep:
- Run lint, fix warnings
- Remove debug logs
- Clean unused resources
- Set versionName 1.0.0
- Configure ProGuard/R8
- Test on multiple devices
- Update documentation
```

### 6.7 Generate Signed APK
```
Build release APK:
- Generate keystore
- Configure signing in build.gradle
- Build → Generate Signed Bundle/APK
- Test signed APK
- Output: app-release.apk
```

---

## Quick Command Reference (Windows)

### Build Commands
```cmd
gradlew.bat clean
gradlew.bat assembleDebug
gradlew.bat installDebug
gradlew.bat test
```

### ADB Commands
```cmd
adb devices
adb install app-debug.apk
adb uninstall com.appp.avisos
adb logcat
```

---

## Implementation Checklist

**Project Setup:**
- [ ] Project created with correct package
- [ ] Dependencies added and synced
- [ ] Colors and themes defined
- [ ] String resources externalized

**Database:**
- [ ] Note entity created
- [ ] DAO with all methods
- [ ] Database class with singleton
- [ ] Repository implemented

**UI Layouts:**
- [ ] Main activity layout with tabs
- [ ] Note card layout
- [ ] Note editor layout
- [ ] RecyclerView adapter

**CRUD Operations:**
- [ ] MainActivity with tab switching
- [ ] MainViewModel for filtering
- [ ] NoteEditorActivity for create/edit
- [ ] Editor ViewModel
- [ ] Delete confirmation dialog

**Testing:**
- [ ] Repository unit tests
- [ ] Database instrumentation tests
- [ ] UI tests
- [ ] Manual testing completed

**Polish:**
- [ ] App icon added
- [ ] Empty states implemented
- [ ] Input validation enhanced
- [ ] Performance optimized
- [ ] Logging added
- [ ] Release build tested
- [ ] Signed APK generated

---

## Core Features Checklist

Must-have features:
- [ ] 4 category tabs
- [ ] Create notes (name, body, contact, category)
- [ ] Edit notes
- [ ] Delete notes with confirmation
- [ ] Filter by category
- [ ] Sort by modified date (newest first)
- [ ] Offline-only (no internet)
- [ ] Local SQLite database
- [ ] Material Design UI
- [ ] Date format: dd/MM/yyyy HH:mm
- [ ] Empty state handling
- [ ] Input validation
- [ ] Works on Android 7.0+

---

## Estimated Timeline

- **Week 1:** Phase 1-2 (Setup + Database)
- **Week 2:** Phase 3 (UI Layouts)
- **Week 3:** Phase 4 (CRUD Operations)
- **Week 4:** Phase 5 (Testing)
- **Week 5:** Phase 6 (Polish + Release)

Total: 3-5 weeks depending on experience level

---

For detailed instructions on each prompt, see [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md).

For Windows setup, see [WINDOWS_QUICK_START.md](WINDOWS_QUICK_START.md).
