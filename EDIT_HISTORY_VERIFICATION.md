# Edit History Enhancement - Verification Guide

## Prerequisites
- Android device or emulator with API 24+
- Android Studio installed
- Repository cloned and project opened

## Build and Install

```bash
# Navigate to project directory
cd /path/to/APPP

# Build the APK
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# Or build and install in one step
./gradlew installDebug
```

## Manual Verification Steps

### Test 1: Database Migration
**Objective:** Verify database migrates from v7 to v8 without data loss

**Steps:**
1. If you have an existing installation with v7 database, note any existing edit history
2. Install the new version
3. Launch the app
4. Verify app starts without crashes
5. Check existing notes still have their edit history

**Expected Result:** ✅ App starts successfully, existing data is preserved

---

### Test 2: Creating First Edition
**Objective:** Verify edition #1 is created when editing a note

**Steps:**
1. Launch app and select user "Pedro"
2. Navigate to any category (e.g., "Trucar")
3. Create a new note:
   - Name: "Test Note"
   - Body: "Test Body"
   - Save
4. View the note detail
5. Click "Edita" button
6. Modify both name and body:
   - Name: "Updated Test Note"
   - Body: "Updated Body"
7. Save
8. View note detail
9. Click "View edit history"

**Expected Result:** 
- ✅ Shows "Edició #1 per Pedro a les [timestamp]"
- ✅ Edition is clickable (has arrow icon)

**Screenshot:** Take a screenshot of the edit history list

---

### Test 3: Viewing Edition Details
**Objective:** Verify clicking an edition shows all changes in that edition

**Steps:**
1. From the edit history list shown in Test 2
2. Click on "Edició #1 per Pedro..."
3. Observe the Edition Detail screen

**Expected Result:**
- ✅ Shows edition header: "Edició #1 per Pedro a les [timestamp]"
- ✅ Shows "Changes in this edition:" header
- ✅ Shows two change cards:
  - Note Name: "Test Note" → "Updated Test Note"
  - Note Body: "Test Body" → "Updated Body"
- ✅ Back button works

**Screenshot:** Take a screenshot of the edition detail view

---

### Test 4: Multiple Editions
**Objective:** Verify multiple editions are tracked correctly

**Steps:**
1. From note detail, click "Edita" again
2. Change only the Contact field:
   - Contact: "John Doe"
3. Save
4. View note detail
5. Click "View edit history"

**Expected Result:**
- ✅ Shows two editions in chronological order (newest first):
  - "Edició #2 per Pedro a les [timestamp2]"
  - "Edició #1 per Pedro a les [timestamp1]"
- ✅ Both are clickable

**Screenshot:** Take a screenshot showing both editions

---

### Test 5: Edition #2 Details
**Objective:** Verify edition #2 shows only the contact change

**Steps:**
1. From the edit history list
2. Click on "Edició #2 per Pedro..."

**Expected Result:**
- ✅ Shows edition header: "Edició #2 per Pedro a les [timestamp]"
- ✅ Shows only ONE change card:
  - Contact: "(empty)" → "John Doe"

**Screenshot:** Take a screenshot of edition #2 detail

---

### Test 6: Different User Attribution
**Objective:** Verify different users are tracked correctly

**Steps:**
1. From main screen, click logout/user icon
2. Select a different user "Joan"
3. Navigate to the same note from Test 2
4. Click "Edita"
5. Toggle the "Urgent" checkbox
6. Save
7. View note detail
8. Click "View edit history"

**Expected Result:**
- ✅ Shows three editions:
  - "Edició #3 per Joan a les [timestamp3]"
  - "Edició #2 per Pedro a les [timestamp2]"
  - "Edició #1 per Pedro a les [timestamp1]"

**Screenshot:** Take a screenshot showing all three editions with different users

---

### Test 7: Verify Edition #3 Details
**Steps:**
1. Click on "Edició #3 per Joan..."

**Expected Result:**
- ✅ Shows edition header with Joan's name
- ✅ Shows only ONE change card:
  - Urgent: "No" → "Yes"

---

### Test 8: Collapsible History
**Objective:** Verify the edit history section can be collapsed/expanded

**Steps:**
1. View any note with edit history
2. Observe initial state (collapsed)
3. Click "View edit history" button
4. Observe expanded state with edition list
5. Click "Hide edit history" button
6. Observe collapsed state

**Expected Result:**
- ✅ Initially collapsed (button text: "View edit history", down arrow)
- ✅ When expanded: shows edition list, button text changes to "Hide edit history", up arrow
- ✅ When collapsed again: hides edition list
- ✅ State persists during configuration changes (rotation)

---

### Test 9: No Edit History
**Objective:** Verify behavior when note has no edit history

**Steps:**
1. Create a brand new note
2. View the note detail immediately after creation (don't edit)

**Expected Result:**
- ✅ Edit history section is completely hidden (not visible)
- ✅ No "View edit history" button shown

---

### Test 10: Navigation Flow
**Objective:** Verify all navigation works correctly

**Steps:**
1. Main Activity → Note Detail (view note)
2. Note Detail → Edition List (click "View edit history")
3. Edition List → Edition Detail (click an edition)
4. Edition Detail → Note Detail (click "Back")
5. Note Detail → Main Activity (click "Enrere")

**Expected Result:**
- ✅ All navigation flows work smoothly
- ✅ Back button in Edition Detail returns to Note Detail
- ✅ Android back button works as expected

---

### Test 11: Accessibility
**Objective:** Verify accessibility features

**Steps:**
1. Enable TalkBack on the device
2. Navigate through the edit history
3. Focus on edition items
4. Focus on the arrow icon

**Expected Result:**
- ✅ Edition titles are read correctly
- ✅ Arrow icon has proper content description: "View changes"
- ✅ All interactive elements are focusable
- ✅ Navigation is logical and predictable

---

### Test 12: Responsive Design
**Objective:** Verify UI adapts to different screen sizes

**Steps:**
1. Test on phone in portrait mode
2. Rotate to landscape mode
3. Test on tablet if available

**Expected Result:**
- ✅ Edition list items are properly sized
- ✅ Text doesn't overflow
- ✅ Touch targets are appropriate size
- ✅ Cards have proper spacing

---

## Automated Testing

### Run Unit Tests
```bash
./gradlew test

# Or specific test
./gradlew test --tests NoteEditHistoryTest
```

**Expected Result:**
- ✅ All tests pass
- ✅ New edition number tests pass

### Run Instrumentation Tests
```bash
./gradlew connectedAndroidTest

# Or specific test
./gradlew connectedAndroidTest --tests NoteEditHistoryDaoTest
```

**Expected Result:**
- ✅ All DAO tests pass
- ✅ New `getMaxEditionNumber` test passes
- ✅ New `groupEditsByEditionNumber` test passes

---

## Edge Cases to Verify

### Edge Case 1: Rapid Edits
**Steps:**
1. Edit a note
2. Save
3. Immediately edit again
4. Save again

**Expected Result:**
- ✅ Two separate editions created (#1 and #2)
- ✅ Timestamps are different

### Edge Case 2: No Changes Made
**Steps:**
1. Edit a note
2. Don't change anything
3. Save

**Expected Result:**
- ✅ No new edition is created (no changes recorded)
- ✅ Edition number doesn't increment

### Edge Case 3: Multiple Fields in One Edit
**Steps:**
1. Edit a note
2. Change name, body, contact, and urgent flag all at once
3. Save
4. View edition detail

**Expected Result:**
- ✅ Single edition created
- ✅ All four changes shown in edition detail
- ✅ All changes have same timestamp

### Edge Case 4: Long Text Values
**Steps:**
1. Edit a note with very long text in body
2. View edition detail

**Expected Result:**
- ✅ Text is properly truncated with ellipsis in list view
- ✅ Scrollable in detail view
- ✅ No UI breaking

---

## Performance Verification

### Test Database Performance
**Steps:**
1. Create a note
2. Edit it 50 times (change different fields)
3. View edit history

**Expected Result:**
- ✅ List loads quickly (< 1 second)
- ✅ Scrolling is smooth
- ✅ No ANR (Application Not Responding)

---

## Regression Testing

Verify existing functionality still works:

1. ✅ Creating new notes
2. ✅ Editing notes
3. ✅ Deleting notes (moving to recycle bin)
4. ✅ Finalizing notes
5. ✅ Restoring notes from recycle bin
6. ✅ User selection and switching
7. ✅ Category filtering
8. ✅ Search functionality
9. ✅ Factures subcategories

---

## Checklist

Use this checklist during verification:

- [ ] Database migration successful
- [ ] First edition created correctly
- [ ] Edition details show all changes
- [ ] Multiple editions tracked
- [ ] Edition #2 isolated correctly
- [ ] Different users attributed correctly
- [ ] Collapsible history works
- [ ] No history scenario handled
- [ ] Navigation flows work
- [ ] Accessibility features work
- [ ] Responsive design verified
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Edge cases handled
- [ ] Performance is acceptable
- [ ] No regressions in existing features

---

## Bug Reporting

If issues are found, report with:
- Steps to reproduce
- Expected behavior
- Actual behavior
- Screenshots/logs
- Device/emulator info
- Android version

---

## Sign-off

**Tester Name:** ________________
**Date:** ________________
**All Tests Passed:** ☐ Yes  ☐ No
**Notes:** ________________________________
