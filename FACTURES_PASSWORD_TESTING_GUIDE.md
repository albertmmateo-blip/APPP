# Factures Access Control - Testing Guide

## Overview
This document describes how to test the access control for the Factures category. Only Pedro can access Factures; no password is required.

## What Was Implemented

### Changes Made
1. **UserSessionManager.kt** - Manages user session:
   - `getCurrentUser()` - Returns the currently logged-in user
   - `isUserLoggedIn()` - Checks if any user is logged in
   - Session cleared on logout

2. **MainActivity.kt** - Simple access control:
   - Tab selection listener checks if user is Pedro
   - Shows access denied dialog for non-Pedro users
   - Pedro can access Factures immediately after login

### Security Features
- Access controlled by logged-in user
- Only Pedro can view Factures content
- Non-Pedro users see access denied dialog

## Manual Testing Steps

### Test 1: Pedro Can Access Factures
**Steps:**
1. Launch the app
2. Select "Pedro" from user selection screen
3. Tap on the "Factures" tab

**Expected Result:**
- Factures tab opens immediately
- Shows the 4 subcategory buttons (Compra, Venda, Compra Subcategories, Venda Subcategories)
- No password dialog appears

### Test 2: Pedro Can Navigate Between Tabs
**Steps:**
1. Login as Pedro
2. Access Factures tab
3. Navigate to another tab (e.g., "Trucar")
4. Return to Factures tab

**Expected Result:**
- Can access Factures tab repeatedly without any dialogs
- Navigation works smoothly

### Test 3: Non-Pedro User Cannot Access Factures
**Steps:**
1. Launch the app
2. Select any user EXCEPT Pedro (e.g., "Isa", "Lourdes", "Alexia", "Albert", or "Joan")
3. Try to tap on the "Factures" tab

**Expected Result:**
- Access denied dialog appears with message "Només l'usuari Pedro té accés a la categoria Factures."
- User cannot access Factures tab at all
- User remains on the previous tab

### Test 4: Access Control Persists After Logout
**Steps:**
1. Login as Pedro and access Factures tab successfully
2. Tap the menu button (three dots)
3. Select "Logout"
4. Login as Isa (or another non-Pedro user)
5. Try to access Factures tab

**Expected Result:**
- Access denied dialog appears for non-Pedro user

### Test 5: Pedro Can Still Access After Re-login
**Steps:**
1. Login as Pedro
2. Access Factures successfully
3. Logout
4. Login as Pedro again
5. Try to access Factures tab

**Expected Result:**
- Can access Factures immediately without any prompts

## Unit Tests
Unit tests have been updated in `UserSessionManagerTest.kt` to verify:
- ✅ User session management
- ✅ Logout clears session
- ✅ User validation

To run unit tests:
```bash
./gradlew test
```

## UI Text (Catalan)
- Access denied title: "Accés Denegat"
- Access denied message: "Només l'usuari Pedro té accés a la categoria Factures."

## Screenshots to Take During Testing
1. Pedro accessing Factures successfully
2. Access denied message for non-Pedro users
3. Factures subcategories view (4 buttons)

## Regression Testing
After implementing this feature, verify that:
- [x] Other tabs (Trucar, Encarregar, Notes) still work normally
- [x] Pedro can still create/edit/delete notes in other categories
- [x] Other users can still access their categories normally
- [x] Logout functionality still works
- [x] User switching still works
- [x] RecycleBin access still works for all users
- [x] No password prompt appears for Pedro when accessing Factures
