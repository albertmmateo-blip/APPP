# Note Editor Activity - Feature Flow Diagrams

## 1. CREATE Mode Flow

```
┌─────────────────────────────────────────────────────────────┐
│                        MainActivity                          │
│  - User taps FAB (Floating Action Button)                  │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Intent (no extras)
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   NoteEditorActivity                         │
│  onCreate() called                                           │
├─────────────────────────────────────────────────────────────┤
│  loadNoteFromIntent()                                        │
│  ├─ noteId = 0 (no EXTRA_NOTE_ID)                          │
│  ├─ CREATE mode detected                                    │
│  ├─ All fields empty                                        │
│  └─ Delete button hidden (visibility = GONE)               │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ User fills form
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      Form Fields                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Name:  [Buy milk                    ]  ← Required   │    │
│  │ Body:  [Need to buy:                ]  ← Required   │    │
│  │        [- Whole milk                ]               │    │
│  │        [- Almond milk               ]               │    │
│  │ Contact: [Supermarket              ]  ← Optional   │    │
│  │ Category: [Notes ▼]                   ← Pre-selected│    │
│  └─────────────────────────────────────────────────────┘    │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ User taps Save
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    Validation (lines 142-154)                │
│  ├─ Check name not empty  ✓                                 │
│  ├─ Check body not empty  ✓                                 │
│  └─ isValid = true                                          │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Validation passed
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              Create Note Object (lines 172-180)              │
│  Note(                                                       │
│    id = 0,                          ← Auto-generated        │
│    name = "Buy milk",                                        │
│    body = "Need to buy:\n- Whole milk\n- Almond milk",     │
│    contact = "Supermarket",                                  │
│    category = "Notes",                                       │
│    createdDate = 1738103419000L,    ← Current timestamp    │
│    modifiedDate = 1738103419000L    ← Same as created      │
│  )                                                           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Save via ViewModel
                         ▼
┌─────────────────────────────────────────────────────────────┐
│          NoteEditorViewModel.saveNote() (lines 31-50)        │
│  viewModelScope.launch {                                     │
│    try {                                                     │
│      if (note.id == 0)  ← True for new notes               │
│        repository.insertNote(note)  ← Database insert       │
│      onSuccess()                                            │
│    } catch (e: Exception) {                                 │
│      onError(e.message)                                     │
│    }                                                         │
│  }                                                           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Insert successful
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    Success Handling                          │
│  ├─ Toast: "Note saved successfully"                        │
│  └─ finish() ─→ Returns to MainActivity                     │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. EDIT Mode Flow

```
┌─────────────────────────────────────────────────────────────┐
│                        MainActivity                          │
│  - User taps existing note item in list                    │
│  - openNoteEditor(note) called (lines 159-174)             │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Intent with extras:
                         │ - EXTRA_NOTE_ID = 42
                         │ - EXTRA_NOTE_NAME = "Buy milk"
                         │ - EXTRA_NOTE_BODY = "..."
                         │ - EXTRA_NOTE_CONTACT = "Supermarket"
                         │ - EXTRA_NOTE_CATEGORY = "Notes"
                         │ - EXTRA_NOTE_CREATED_DATE = 1738100000000L
                         │ - EXTRA_NOTE_MODIFIED_DATE = 1738100000000L
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   NoteEditorActivity                         │
│  onCreate() called                                           │
├─────────────────────────────────────────────────────────────┤
│  loadNoteFromIntent() (lines 73-103)                        │
│  ├─ noteId = 42 (EXTRA_NOTE_ID exists)                     │
│  ├─ EDIT mode detected                                      │
│  ├─ Load all note data from intent extras                  │
│  ├─ Create Note object and store in editingNote           │
│  ├─ Populate all form fields with existing data           │
│  └─ Delete button visible (visibility = VISIBLE)           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Form pre-filled
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      Form Fields                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Name:  [Buy milk                    ]              │    │
│  │ Body:  [Need to buy:                ]              │    │
│  │        [- Whole milk                ]              │    │
│  │        [- Almond milk               ]              │    │
│  │ Contact: [Supermarket              ]              │    │
│  │ Category: [Notes ▼]                               │    │
│  │                                                     │    │
│  │ [Save]  [Delete]  [Cancel]  ← Delete visible      │    │
│  └─────────────────────────────────────────────────────┘    │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ User modifies body
                         │ (adds "- Soy milk")
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      Modified Form                           │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Name:  [Buy milk                    ]              │    │
│  │ Body:  [Need to buy:                ]  ← Modified  │    │
│  │        [- Whole milk                ]              │    │
│  │        [- Almond milk               ]              │    │
│  │        [- Soy milk                  ]              │    │
│  │ Contact: [Supermarket              ]              │    │
│  │ Category: [Notes ▼]                               │    │
│  └─────────────────────────────────────────────────────┘    │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ User taps Save
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    Validation (lines 142-154)                │
│  ├─ Check name not empty  ✓                                 │
│  ├─ Check body not empty  ✓                                 │
│  └─ isValid = true                                          │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Validation passed
                         ▼
┌─────────────────────────────────────────────────────────────┐
│           Update Note Object (lines 163-170)                 │
│  editingNote.copy(                                           │
│    name = "Buy milk",               ← Unchanged             │
│    body = "Need to buy:\n- Whole milk\n...\n- Soy milk",   │
│    contact = "Supermarket",         ← Unchanged             │
│    category = "Notes",              ← Unchanged             │
│    modifiedDate = 1738103419000L    ← NEW timestamp        │
│  )                                                           │
│  Note: createdDate is preserved (not in copy)               │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Update via ViewModel
                         ▼
┌─────────────────────────────────────────────────────────────┐
│          NoteEditorViewModel.saveNote() (lines 31-50)        │
│  viewModelScope.launch {                                     │
│    try {                                                     │
│      if (note.id == 0)  ← False for existing notes         │
│      else                                                    │
│        repository.updateNote(note)  ← Database update       │
│      onSuccess()                                            │
│    } catch (e: Exception) {                                 │
│      onError(e.message)                                     │
│    }                                                         │
│  }                                                           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Update successful
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    Success Handling                          │
│  ├─ Toast: "Note saved successfully"                        │
│  └─ finish() ─→ Returns to MainActivity                     │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Validation Error Flow

```
┌─────────────────────────────────────────────────────────────┐
│                   NoteEditorActivity                         │
│  User fills form partially                                  │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ User taps Save
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      Form Data                               │
│  name = ""          ← EMPTY (invalid)                       │
│  body = "Test note"  ← OK                                   │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ saveNote() called
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                Validation Logic (lines 142-154)              │
│                                                              │
│  var isValid = true                                          │
│                                                              │
│  if (name.isEmpty()) {  ← TRUE                              │
│    binding.textInputLayoutNoteName.error =                  │
│      getString(R.string.error_empty_name)                   │
│    isValid = false                                          │
│  }                                                           │
│                                                              │
│  if (body.isEmpty()) {  ← FALSE                             │
│    // Validation passed                                     │
│  } else {                                                    │
│    binding.textInputLayoutNoteBody.error = null             │
│  }                                                           │
│                                                              │
│  if (!isValid) {  ← TRUE                                    │
│    return  ← EXIT, DON'T SAVE                              │
│  }                                                           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Validation failed, exit early
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      UI Error Display                        │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Name:  [                           ]  ← EMPTY       │    │
│  │        ⚠ Note name cannot be empty  ← Error shown  │    │
│  │                                                     │    │
│  │ Body:  [Test note                  ]  ← OK         │    │
│  │                                                     │    │
│  │ Contact: [                         ]              │    │
│  │ Category: [Notes ▼]                               │    │
│  │                                                     │    │
│  │ [Save]  [Cancel]                                   │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                              │
│  - Save operation blocked                                   │
│  - User stays on form                                       │
│  - Can correct error and try again                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. Delete Confirmation Flow

```
┌─────────────────────────────────────────────────────────────┐
│                   NoteEditorActivity                         │
│  EDIT mode - note loaded                                    │
│  Delete button visible                                      │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ User taps Delete
                         ▼
┌─────────────────────────────────────────────────────────────┐
│         showDeleteConfirmationDialog() (lines 199-208)       │
│  AlertDialog.Builder(this)                                   │
│    .setTitle("Delete Note")                                 │
│    .setMessage("Are you sure you want to delete this       │
│                 note? This action cannot be undone.")       │
│    .setPositiveButton("Delete") { _, _ ->                   │
│        deleteNote()  ← Only if user confirms               │
│    }                                                         │
│    .setNegativeButton("Cancel", null)                       │
│    .show()                                                   │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Dialog shown
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  Confirmation Dialog                         │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  Delete Note                                        │    │
│  │                                                     │    │
│  │  Are you sure you want to delete this note?        │    │
│  │  This action cannot be undone.                     │    │
│  │                                                     │    │
│  │              [Delete]    [Cancel]                  │    │
│  └─────────────────────────────────────────────────────┘    │
└───────────┬──────────────────────────┬──────────────────────┘
            │                          │
            │ Cancel tapped            │ Delete tapped
            ▼                          ▼
┌───────────────────────┐  ┌──────────────────────────────────┐
│  Dialog dismissed     │  │  deleteNote() (lines 213-226)    │
│  Note preserved       │  │  editingNote?.let { note ->      │
│  User returns to form │  │    viewModel.deleteNote(         │
└───────────────────────┘  │      note = note,                │
                           │      onSuccess = {               │
                           │        Toast("Note deleted")     │
                           │        finish()                  │
                           │      },                          │
                           │      onError = { error ->        │
                           │        Toast("Delete failed")    │
                           │      }                           │
                           │    )                             │
                           │  }                               │
                           └────────────┬─────────────────────┘
                                        │
                                        │ Delete via ViewModel
                                        ▼
                           ┌──────────────────────────────────┐
                           │ NoteEditorViewModel.deleteNote() │
                           │   (lines 59-72)                  │
                           │ viewModelScope.launch {          │
                           │   try {                          │
                           │     repository.deleteNote(note)  │
                           │     onSuccess()                  │
                           │   } catch (e: Exception) {       │
                           │     onError(e.message)           │
                           │   }                              │
                           │ }                                │
                           └────────────┬─────────────────────┘
                                        │
                                        │ Delete successful
                                        ▼
                           ┌──────────────────────────────────┐
                           │    Success Handling              │
                           │ ├─ Toast: "Note deleted"         │
                           │ ├─ Note removed from database    │
                           │ └─ finish() ─→ MainActivity      │
                           └──────────────────────────────────┘
```

---

## 5. Component Interaction Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         MainActivity                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐                  │   │
│  │ │Trucar│  │Encarr│  │Factur│  │Notes │  ← Tabs          │   │
│  │ └──────┘  └──────┘  └──────┘  └──────┘                  │   │
│  │                                                           │   │
│  │ ┌─────────────────────────────────────────┐              │   │
│  │ │ Note 1: Buy milk              [icon]   │ ← Tap opens  │   │
│  │ │ Created: 25/01/2026 10:30              │   editor     │   │
│  │ └─────────────────────────────────────────┘              │   │
│  │ ┌─────────────────────────────────────────┐              │   │
│  │ │ Note 2: Call plumber          [icon]   │              │   │
│  │ │ Created: 24/01/2026 14:20              │              │   │
│  │ └─────────────────────────────────────────┘              │   │
│  │                                                           │   │
│  │                                            ┌────┐         │   │
│  │                                            │ +  │ ← FAB   │   │
│  │                                            └────┘         │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                   │
│  Observes: MainViewModel.notes (LiveData)                       │
└────────────────────────┬──────────────────┬─────────────────────┘
                         │                  │
              FAB tapped │        Note tapped │
                         │                  │
         Intent(no extras)         Intent(with note data)
                         │                  │
                         ▼                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                    NoteEditorActivity                            │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ CREATE MODE           │        EDIT MODE                 │   │
│  │ ─────────────────────────────────────────────────────────│   │
│  │ Name:  [         ]    │  Name:  [Buy milk           ]   │   │
│  │ Body:  [         ]    │  Body:  [Need to buy:       ]   │   │
│  │        [         ]    │         [- Whole milk       ]   │   │
│  │        [         ]    │         [- Almond milk      ]   │   │
│  │ Contact: [       ]    │  Contact: [Supermarket      ]   │   │
│  │ Category: [Notes▼]    │  Category: [Notes▼]             │   │
│  │                       │                                  │   │
│  │ [Save]  [Cancel]      │  [Save]  [Delete]  [Cancel]     │   │
│  │         ^             │          ^                       │   │
│  │         Delete hidden │          Delete visible          │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                   │
│  Uses: NoteEditorViewModel                                       │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ save/delete operations
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                   NoteEditorViewModel                            │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ saveNote(note, onSuccess, onError)                       │   │
│  │ ├─ Launches coroutine                                    │   │
│  │ ├─ Calls repository.insertNote() or .updateNote()       │   │
│  │ └─ Invokes callbacks                                     │   │
│  │                                                           │   │
│  │ deleteNote(note, onSuccess, onError)                     │   │
│  │ ├─ Launches coroutine                                    │   │
│  │ ├─ Calls repository.deleteNote()                         │   │
│  │ └─ Invokes callbacks                                     │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                   │
│  Uses: NoteRepository                                            │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ data operations
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                      NoteRepository                              │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ suspend fun insertNote(note): Long                       │   │
│  │ suspend fun updateNote(note)                             │   │
│  │ suspend fun deleteNote(note)                             │   │
│  │ fun getAllNotes(): LiveData<List<Note>>                  │   │
│  │ fun getNotesByCategory(cat): LiveData<List<Note>>        │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                   │
│  Uses: NoteDao                                                   │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ database operations
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                        NoteDao (Room)                            │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ @Insert                                                   │   │
│  │ suspend fun insertNote(note: Note): Long                 │   │
│  │                                                           │   │
│  │ @Update                                                   │   │
│  │ suspend fun updateNote(note: Note)                       │   │
│  │                                                           │   │
│  │ @Delete                                                   │   │
│  │ suspend fun deleteNote(note: Note)                       │   │
│  │                                                           │   │
│  │ @Query("SELECT * FROM notes ORDER BY modified_date DESC")│   │
│  │ fun getAllNotes(): LiveData<List<Note>>                  │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                   │
│  Accesses: AppDatabase                                           │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ SQL operations
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                     SQLite Database                              │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Table: notes                                            │   │
│  │  ┌─────┬──────┬──────┬─────────┬──────────┬────────┐    │   │
│  │  │ id  │ name │ body │ contact │ category │ dates  │    │   │
│  │  ├─────┼──────┼──────┼─────────┼──────────┼────────┤    │   │
│  │  │  1  │ Buy  │ Need │ Super   │ Notes    │ ...    │    │   │
│  │  │     │ milk │ ...  │ market  │          │        │    │   │
│  │  ├─────┼──────┼──────┼─────────┼──────────┼────────┤    │   │
│  │  │  2  │ Call │ Fix  │ Joe     │ Trucar   │ ...    │    │   │
│  │  │     │ plum │ ...  │ Plumber │          │        │    │   │
│  │  └─────┴──────┴──────┴─────────┴──────────┴────────┘    │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                   │
│  Storage: /data/data/com.appp.avisos/databases/appp_avisos_db   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 6. Timestamp Management

```
┌─────────────────────────────────────────────────────────────────┐
│                    CREATE New Note                               │
│                                                                   │
│  User creates note at: 2026-01-28 22:30:19                      │
│                                                                   │
│  Note object created:                                            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ id = 0                                                     │  │
│  │ name = "Buy groceries"                                     │  │
│  │ body = "Milk, eggs, bread"                                 │  │
│  │ category = "Notes"                                         │  │
│  │ createdDate = 1738103419000L  ← System.currentTimeMillis()│  │
│  │ modifiedDate = 1738103419000L ← Same as created           │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                   │
│  Saved to database with timestamps                               │
└─────────────────────────────────────────────────────────────────┘

                            ⬇ Time passes

┌─────────────────────────────────────────────────────────────────┐
│                    EDIT Existing Note                            │
│                                                                   │
│  User edits note at: 2026-01-29 10:15:42                        │
│                                                                   │
│  Original note loaded:                                           │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ id = 1                                                     │  │
│  │ name = "Buy groceries"                                     │  │
│  │ body = "Milk, eggs, bread"                                 │  │
│  │ category = "Notes"                                         │  │
│  │ createdDate = 1738103419000L  ← Original timestamp        │  │
│  │ modifiedDate = 1738103419000L ← Will be updated           │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                   │
│  User changes body to: "Milk, eggs, bread, butter"              │
│                                                                   │
│  Updated note object:                                            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ editingNote.copy(                                          │  │
│  │   name = "Buy groceries",          ← Unchanged            │  │
│  │   body = "Milk, eggs, bread, butter",  ← Changed          │  │
│  │   category = "Notes",              ← Unchanged            │  │
│  │   modifiedDate = 1738146942000L    ← NEW timestamp        │  │
│  │ )                                                          │  │
│  │                                                            │  │
│  │ Note: createdDate NOT in copy() → preserved from original │  │
│  │       modifiedDate explicitly set → updated to current    │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                   │
│  Final note in database:                                         │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ id = 1                                                     │  │
│  │ name = "Buy groceries"                                     │  │
│  │ body = "Milk, eggs, bread, butter"                         │  │
│  │ category = "Notes"                                         │  │
│  │ createdDate = 1738103419000L  ← PRESERVED from creation   │  │
│  │ modifiedDate = 1738146942000L ← UPDATED to current        │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                   │
│  ✓ Created date shows original creation time                    │
│  ✓ Modified date shows last edit time                           │
└─────────────────────────────────────────────────────────────────┘
```

---

## Legend

```
┌─────┐
│ Box │  = Component or screen
└─────┘

  ↓     = Flow direction

  ←     = Annotation or note

  ✓     = Success or correct behavior

  ⚠     = Warning or error state

[Button]  = UI element
```

---

**Document Purpose**: Visual reference for Note Editor Activity flows  
**Audience**: Developers, code reviewers, and maintainers  
**Last Updated**: 2026-01-28
