#!/bin/bash
# Edit Log Implementation Verification Script
# This script verifies that the edit log feature has been implemented correctly

echo "================================================"
echo "Edit Log Implementation Verification"
echo "================================================"
echo ""

# Check if all required files exist
echo "1. Checking for required files..."
files=(
    "app/src/main/java/com/appp/avisos/database/NoteEditHistory.kt"
    "app/src/main/java/com/appp/avisos/database/NoteEditHistoryDao.kt"
    "app/src/main/java/com/appp/avisos/adapter/EditHistoryAdapter.kt"
    "app/src/main/res/layout/item_edit_history.xml"
)

all_files_exist=true
for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✓ $file exists"
    else
        echo "   ✗ $file MISSING"
        all_files_exist=false
    fi
done
echo ""

# Check if modified files contain expected changes
echo "2. Checking for expected code changes..."

# Check AppDatabase version
if grep -q "version = 2" app/src/main/java/com/appp/avisos/database/AppDatabase.kt; then
    echo "   ✓ AppDatabase version incremented to 2"
else
    echo "   ✗ AppDatabase version not updated"
fi

# Check NoteEditHistory in entities
if grep -q "NoteEditHistory::class" app/src/main/java/com/appp/avisos/database/AppDatabase.kt; then
    echo "   ✓ NoteEditHistory added to database entities"
else
    echo "   ✗ NoteEditHistory not added to entities"
fi

# Check for recordEditHistory method
if grep -q "recordEditHistory" app/src/main/java/com/appp/avisos/viewmodel/NoteEditorViewModel.kt; then
    echo "   ✓ recordEditHistory method found in NoteEditorViewModel"
else
    echo "   ✗ recordEditHistory method not found"
fi

# Check for edit history section in layout
if grep -q "layoutEditHistorySection" app/src/main/res/layout/activity_note_detail.xml; then
    echo "   ✓ Edit history section added to note detail layout"
else
    echo "   ✗ Edit history section not found in layout"
fi

# Check for toggle button in layout
if grep -q "buttonToggleEditHistory" app/src/main/res/layout/activity_note_detail.xml; then
    echo "   ✓ Toggle button added to note detail layout"
else
    echo "   ✗ Toggle button not found in layout"
fi

# Check for RecyclerView in layout
if grep -q "recyclerViewEditHistory" app/src/main/res/layout/activity_note_detail.xml; then
    echo "   ✓ RecyclerView added to note detail layout"
else
    echo "   ✗ RecyclerView not found in layout"
fi

# Check string resources
echo ""
echo "3. Checking string resources..."
if grep -q "button_view_edit_history" app/src/main/res/values/strings.xml; then
    echo "   ✓ Edit history strings added"
else
    echo "   ✗ Edit history strings not found"
fi

echo ""
echo "4. Verifying implementation requirements..."

# Check that section has visibility control
if grep -A 5 'android:id="@+id/layoutEditHistorySection"' app/src/main/res/layout/activity_note_detail.xml | grep -q 'android:visibility="gone"'; then
    echo "   ✓ Edit history section hidden by default (GONE)"
else
    echo "   ⚠ Check that layoutEditHistorySection has visibility=\"gone\""
fi

# Check for setupEditHistoryObserver method
if grep -q "setupEditHistoryObserver" app/src/main/java/com/appp/avisos/NoteDetailActivity.kt; then
    echo "   ✓ setupEditHistoryObserver method found in NoteDetailActivity"
else
    echo "   ✗ setupEditHistoryObserver method not found"
fi

# Check for toggleEditHistory method
if grep -q "toggleEditHistory" app/src/main/java/com/appp/avisos/NoteDetailActivity.kt; then
    echo "   ✓ toggleEditHistory method found in NoteDetailActivity"
else
    echo "   ✗ toggleEditHistory method not found"
fi

echo ""
echo "5. Attempting to build the project..."
if command -v ./gradlew &> /dev/null; then
    ./gradlew assembleDebug --no-daemon 2>&1 | tail -20
    if [ ${PIPESTATUS[0]} -eq 0 ]; then
        echo "   ✓ Build successful"
    else
        echo "   ✗ Build failed - see output above"
    fi
else
    echo "   ⚠ Gradle wrapper not found, skipping build"
fi

echo ""
echo "================================================"
echo "Verification complete!"
echo "================================================"
echo ""
echo "Next steps:"
echo "1. Build the app: ./gradlew assembleDebug"
echo "2. Install on device/emulator: ./gradlew installDebug"
echo "3. Manual testing:"
echo "   - Create a note"
echo "   - Edit the note multiple times"
echo "   - View the note details"
echo "   - Verify edit history button appears"
echo "   - Click to expand/collapse edit history"
echo "   - Verify changes are displayed correctly"
