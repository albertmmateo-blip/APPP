# Next Steps for Manual Verification

## Build and Install the App

To test the implementation, you need to build and install the app on an Android device or emulator:

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

The APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Manual Testing Checklist

### Test 1: Pedro with Correct Password ✓
1. Launch the app
2. Select "Pedro" from user selection screen
3. Tap on the "Factures" tab
4. You should see a password dialog with:
   - Title: "Accés a Factures"
   - Message: "Aquesta categoria està restringida..."
   - Password input field (showing dots)
5. Enter: `mixo`
6. Tap "Acceptar"
7. **Expected**: Factures tab opens showing 4 subcategory buttons

### Test 2: Pedro with Wrong Password ✓
1. Launch the app
2. Select "Pedro"
3. Tap "Factures" tab
4. Enter wrong password: `wrong`
5. Tap "Acceptar"
6. **Expected**: Error dialog "Contrasenya incorrecta"
7. After dismissing error, you should still be on the previous tab

### Test 3: Password Remembered in Session ✓
1. Launch app as Pedro
2. Enter correct password "mixo" and access Factures
3. Navigate to another tab (e.g., "Trucar")
4. Come back to "Factures" tab
5. **Expected**: No password dialog, immediate access

### Test 4: Password Cleared on Logout ✓
1. Login as Pedro
2. Access Factures with password
3. Tap menu (⋮) → Logout
4. Login as Pedro again
5. Try to access Factures
6. **Expected**: Password dialog appears again

### Test 5: Non-Pedro User Access Denied ✓
1. Login as any user except Pedro (try "Isa", "Lourdes", etc.)
2. Tap on "Factures" tab
3. **Expected**: Access denied dialog
4. Message: "Només l'usuari Pedro té accés a la categoria Factures."
5. Cannot access Factures at all

### Test 6: Cancel Dialog ✓
1. Login as Pedro
2. Tap "Factures" tab
3. Tap "Cancel·lar" button on password dialog
4. **Expected**: Dialog closes, remains on previous tab

### Test 7: Dialog Dismissal (Back Button) ✓
1. Login as Pedro
2. Tap "Factures" tab
3. Press device back button
4. **Expected**: Dialog closes, remains on previous tab

## What to Look For

### UI Elements
- ✅ Password input field shows dots (not plain text)
- ✅ Dialog has proper title and message in Catalan
- ✅ Dialog has "Acceptar" and "Cancel·lar" buttons
- ✅ Error message is clear and in Catalan
- ✅ Access denied message is clear

### Behavior
- ✅ Password is case-sensitive ("mixo" works, "MIXO" doesn't)
- ✅ Empty password is rejected
- ✅ Wrong password shows error
- ✅ Correct password grants access
- ✅ Access persists during session
- ✅ Access cleared on logout
- ✅ Non-Pedro users cannot access at all

### Edge Cases
- ✅ Rapid tab switching doesn't bypass authentication
- ✅ Rotating device doesn't bypass authentication
- ✅ Dialog cannot be dismissed accidentally
- ✅ No crashes or ANRs during authentication

## Screenshots to Take

Please take screenshots of:
1. Password dialog appearance
2. Successful access to Factures (4 subcategory buttons)
3. Error message for wrong password
4. Access denied message for non-Pedro user

Save screenshots with descriptive names:
- `01_password_dialog.png`
- `02_factures_access_granted.png`
- `03_wrong_password_error.png`
- `04_access_denied_non_pedro.png`

## Running Unit Tests

Before manual testing, verify that unit tests pass:

```bash
./gradlew test

# Or specifically for UserSessionManager tests
./gradlew test --tests UserSessionManagerTest
```

All 12 tests should pass.

## Regression Testing

Verify that existing functionality still works:
- [ ] Login/logout still works
- [ ] Other tabs (Trucar, Encarregar, Notes) work normally
- [ ] Creating/editing notes works
- [ ] RecycleBin access works
- [ ] User switching works

## If You Find Issues

If you encounter any issues:

1. **Check LogCat** for error messages:
   ```bash
   adb logcat | grep -i "error\|exception"
   ```

2. **Clear app data** and try again:
   ```bash
   adb shell pm clear com.appp.avisos
   ```

3. **Check the implementation** in:
   - `UserSessionManager.kt` - Password validation
   - `MainActivity.kt` - Dialog and tab handling

## Expected Outcome

After testing, you should confirm:
- ✅ Password authentication works correctly
- ✅ Pedro can access Factures with password "mixo"
- ✅ Other users cannot access Factures
- ✅ Password persists during session
- ✅ Password cleared on logout
- ✅ No crashes or unexpected behavior
- ✅ UI is clear and user-friendly

## Success Criteria

The implementation is successful if:
1. Pedro cannot access Factures without entering "mixo"
2. Other users cannot access Factures at all
3. Password is remembered during the session
4. Password is cleared when Pedro logs out
5. Wrong passwords are rejected with clear error message
6. No crashes or unexpected behavior
7. UI is clear and professional

## Support

For questions or issues, refer to:
- `FACTURES_PASSWORD_TESTING_GUIDE.md` - Detailed testing procedures
- `FACTURES_PASSWORD_IMPLEMENTATION_SUMMARY.md` - Technical details
- Code comments in `UserSessionManager.kt` and `MainActivity.kt`
