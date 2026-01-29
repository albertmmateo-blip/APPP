# Factures Password Authentication - Testing Guide

## Overview
This document describes how to test the newly implemented password authentication for the Factures category.

## What Was Implemented

### Changes Made
1. **UserSessionManager.kt** - Added password validation and authentication state management:
   - `validateFacturesPassword(password: String)` - Validates if password equals "mixo"
   - `isFacturesAuthenticated()` - Checks if user has been authenticated for Factures
   - `setFacturesAuthenticated(authenticated: Boolean)` - Sets authentication state
   - Password constant: "mixo"

2. **MainActivity.kt** - Added password dialog and tab access control:
   - Tab selection listener intercepts attempts to access Factures tab
   - Shows password dialog for Pedro when not authenticated
   - Shows access denied dialog for non-Pedro users
   - Programmatic tab change flag to avoid recursive dialog triggers

### Security Features
- Password is required only once per session for Pedro
- Password is cleared on logout
- Non-Pedro users cannot access Factures at all
- Password is case-sensitive ("mixo" not "MIXO")

## Manual Testing Steps

### Test 1: Pedro with Correct Password
**Steps:**
1. Launch the app
2. Select "Pedro" from user selection screen
3. Try to tap on the "Factures" tab
4. A password dialog should appear with title "Accés a Factures"
5. Enter password: "mixo"
6. Tap "Acceptar"

**Expected Result:**
- Dialog closes
- Factures tab opens showing the 4 subcategory buttons (Passades, Per passar, Per pagar, Per cobrar)

### Test 2: Pedro with Incorrect Password
**Steps:**
1. Launch the app
2. Select "Pedro" from user selection screen
3. Try to tap on the "Factures" tab
4. Enter password: "wrong"
5. Tap "Acceptar"

**Expected Result:**
- Error dialog appears with message "Contrasenya incorrecta"
- After dismissing error, user remains on the previous tab (not Factures)

### Test 3: Pedro - Password Remembered During Session
**Steps:**
1. Launch the app
2. Select "Pedro" from user selection screen
3. Access Factures tab with correct password "mixo"
4. Navigate to another tab (e.g., "Trucar")
5. Return to Factures tab

**Expected Result:**
- No password dialog should appear the second time
- Factures tab opens immediately

### Test 4: Password Reset on Logout
**Steps:**
1. Login as Pedro and authenticate Factures access with password
2. Access Factures tab successfully
3. Tap the menu button (three dots)
4. Select "Logout"
5. Login as Pedro again
6. Try to access Factures tab

**Expected Result:**
- Password dialog should appear again (authentication was cleared)

### Test 5: Non-Pedro User Cannot Access Factures
**Steps:**
1. Launch the app
2. Select any user EXCEPT Pedro (e.g., "Isa", "Lourdes", "Alexia", "Albert", or "Joan")
3. Try to tap on the "Factures" tab

**Expected Result:**
- Access denied dialog appears with message "Només l'usuari Pedro té accés a la categoria Factures."
- User cannot access Factures tab at all

### Test 6: Cancel Password Dialog
**Steps:**
1. Launch the app
2. Select "Pedro" from user selection screen
3. Try to tap on the "Factures" tab
4. Tap "Cancel·lar" button on password dialog

**Expected Result:**
- Dialog closes
- User remains on the previous tab
- Factures tab is not accessed

## Edge Cases to Test

### Edge Case 1: Case Sensitivity
- Password "MIXO" should be rejected
- Password "Mixo" should be rejected
- Only "mixo" (lowercase) should work

### Edge Case 2: Empty Password
- Entering empty string should be rejected

### Edge Case 3: Spaces in Password
- Password " mixo" (with space) should be rejected
- Password "mixo " (with space) should be rejected

## Unit Tests
Unit tests have been created in `UserSessionManagerTest.kt` to verify:
- ✅ Correct password validation
- ✅ Incorrect password rejection
- ✅ Authentication state management
- ✅ Logout clears authentication
- ✅ User validation

To run unit tests:
```bash
./gradlew test
```

## Known Limitations
1. Password is stored as a constant in the code (not encrypted in a secure storage)
2. Password is the same across all installations
3. No password recovery mechanism
4. No way for admin to change password without code modification

## UI Text (Catalan)
- Dialog title: "Accés a Factures"
- Dialog message: "Aquesta categoria està restringida. Si us plau, introdueix la contrasenya:"
- Password hint: "Contrasenya"
- Positive button: "Acceptar"
- Negative button: "Cancel·lar"
- Error title: "Error"
- Error message: "Contrasenya incorrecta"
- Access denied title: "Accés Denegat"
- Access denied message: "Només l'usuari Pedro té accés a la categoria Factures."

## Screenshots to Take During Testing
1. Password dialog appearance
2. Successful access to Factures subcategories
3. Error message for incorrect password
4. Access denied message for non-Pedro users

## Regression Testing
After implementing this feature, verify that:
- [ ] Other tabs (Trucar, Encarregar, Notes) still work normally
- [ ] Pedro can still create/edit/delete notes in other categories
- [ ] Other users can still access their categories normally
- [ ] Logout functionality still works
- [ ] User switching still works
- [ ] RecycleBin access still works for all users
