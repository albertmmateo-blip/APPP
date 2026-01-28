# Windows Quick Start Guide - Appp Avisos

This guide helps you get started developing the Appp Avisos Android app on a Windows machine.

## Prerequisites Installation (30-60 minutes)

### Step 1: Install Java Development Kit (JDK)

1. Download **JDK 11 or later** from:
   - Oracle: https://www.oracle.com/java/technologies/downloads/
   - Or OpenJDK: https://adoptium.net/

2. Run the installer, use default settings

3. Set up environment variables:
   - Open "Edit system environment variables"
   - Click "Environment Variables"
   - Under "System variables", click "New":
     * Variable name: `JAVA_HOME`
     * Variable value: `C:\Program Files\Java\jdk-11.x.x` (your JDK path)
   - Find "Path" in System variables, click "Edit", add:
     * `%JAVA_HOME%\bin`

4. Verify installation:
   ```cmd
   java -version
   javac -version
   ```

### Step 2: Install Android Studio

1. Download Android Studio from: https://developer.android.com/studio

2. Run the installer:
   - Select all components (Android SDK, Android Virtual Device)
   - Choose installation location (default is fine)
   - Allow ~10 GB disk space

3. First launch setup:
   - Choose "Standard" installation
   - Accept licenses
   - Wait for SDK components to download

4. Configure SDK:
   - Open Android Studio
   - Go to: File → Settings → Appearance & Behavior → System Settings → Android SDK
   - Under "SDK Platforms":
     * ✓ Android 13.0 (API 33) [or latest]
     * ✓ Android 7.0 (API 24) [minimum required]
   - Under "SDK Tools":
     * ✓ Android SDK Build-Tools
     * ✓ Android SDK Platform-Tools
     * ✓ Android Emulator
     * ✓ Intel x86 Emulator Accelerator (HAXM) [if using Intel CPU]
   - Click "Apply" to download

5. Set up environment variables:
   - Add `ANDROID_HOME`:
     * Variable name: `ANDROID_HOME`
     * Variable value: `C:\Users\YourUsername\AppData\Local\Android\Sdk`
   - Add to Path:
     * `%ANDROID_HOME%\platform-tools`
     * `%ANDROID_HOME%\tools`

6. Verify installation:
   ```cmd
   adb version
   ```

### Step 3: Enable Virtualization (for Emulator)

**For Intel CPUs:**
1. Restart computer
2. Enter BIOS (usually F2, F10, or Del during boot)
3. Enable "Intel VT-x" or "Intel Virtualization Technology"
4. Save and exit BIOS

**For AMD CPUs:**
1. Restart computer
2. Enter BIOS
3. Enable "AMD-V" or "SVM Mode"
4. Save and exit BIOS

**Install HAXM (Intel only):**
1. Open Android Studio SDK Manager
2. SDK Tools → Intel x86 Emulator Accelerator (HAXM)
3. Or download from: https://github.com/intel/haxm/releases
4. Run installer with default settings

### Step 4: Create an Android Virtual Device (Emulator)

1. In Android Studio: Tools → Device Manager

2. Click "Create Device"

3. Select hardware:
   - Category: Phone
   - Device: Pixel 4 or Pixel 5
   - Click "Next"

4. Select system image:
   - Release Name: S (API 31) or later
   - Download if needed (click Download link)
   - Click "Next"

5. Verify configuration:
   - Name: "Appp_Test_Device"
   - Startup orientation: Portrait
   - Graphics: Automatic
   - Click "Finish"

6. Start emulator:
   - Click ▶️ (Play button) next to your AVD
   - Wait 1-2 minutes for boot
   - You should see Android home screen

### Step 5: Install Git (Optional but Recommended)

1. Download Git for Windows: https://git-scm.com/download/win

2. Run installer:
   - Use default settings
   - Select "Git from the command line and also from 3rd-party software"
   - Select "Use Visual Studio Code as Git's default editor" (or your preferred editor)

3. Verify:
   ```cmd
   git --version
   ```

## Creating Your First Project (10 minutes)

### Step 1: Create New Project

1. Open Android Studio

2. Click "New Project"

3. Select "Empty Activity"

4. Configure project:
   - Name: `ApppAvisos`
   - Package name: `com.appp.avisos`
   - Save location: `C:\AndroidProjects\ApppAvisos`
   - Language: **Kotlin** (recommended)
   - Minimum SDK: API 24 ("Nougat"; Android 7.0)
   - Click "Finish"

5. Wait for Gradle sync to complete (2-5 minutes)

### Step 2: Enable ViewBinding

1. Open `app/build.gradle.kts` (or `app/build.gradle` if using Groovy)

2. Add inside `android` block:
   ```kotlin
   buildFeatures {
       viewBinding = true
   }
   ```

3. Click "Sync Now" at the top

### Step 3: Add Dependencies

1. In `app/build.gradle.kts`, add to `dependencies` block:

```kotlin
dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    // Material Design
    implementation("com.google.android.material:material:1.11.0")
    
    // Constraint Layout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    
    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

2. Add KSP plugin at the top of the file:
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
}
```

3. Click "Sync Now"

### Step 4: Run Your First Build

1. Click the green ▶️ (Run) button in toolbar

2. Select your emulator from the dropdown

3. Wait for build to complete (first build takes 3-5 minutes)

4. App should launch on emulator showing "Hello World"

🎉 **Success!** You now have a working Android development environment.

## Common Windows Issues & Solutions

### Issue: "JAVA_HOME is not set"

**Solution:**
```cmd
# Check if JAVA_HOME is set
echo %JAVA_HOME%

# If empty, set it:
setx JAVA_HOME "C:\Program Files\Java\jdk-11.x.x"

# Restart Command Prompt and verify
echo %JAVA_HOME%
```

### Issue: "adb is not recognized"

**Solution:**
```cmd
# Add Android SDK to Path permanently:
setx PATH "%PATH%;%ANDROID_HOME%\platform-tools"

# Or temporarily in current session:
set PATH=%PATH%;C:\Users\YourUsername\AppData\Local\Android\Sdk\platform-tools

# Restart Command Prompt
adb version
```

### Issue: Emulator won't start / Black screen

**Solution:**
1. Check virtualization is enabled in BIOS
2. Install/reinstall HAXM
3. Try using a different system image (ARM instead of x86)
4. Increase RAM allocation:
   - Device Manager → Edit AVD → Show Advanced Settings
   - Increase RAM to 2048 MB

### Issue: Gradle sync fails

**Solution:**
```cmd
# Clear Gradle cache
cd C:\Users\YourUsername\.gradle
rmdir /s /q caches

# In Android Studio:
# File → Invalidate Caches → Invalidate and Restart
```

### Issue: "SDK location not found"

**Solution:**
1. Create/edit `local.properties` in project root:
```properties
sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```
(Note: Use double backslashes `\\` or forward slashes `/`)

2. Sync project with Gradle files

### Issue: Slow build times

**Solution:**
1. Add to `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true
```

2. Enable Offline mode:
   - File → Settings → Build, Execution, Deployment → Gradle
   - ✓ Offline work

## Useful Windows Commands

### Gradle Commands (run in project root)

```cmd
# Clean build
gradlew.bat clean

# Build debug APK
gradlew.bat assembleDebug

# Install debug APK to connected device
gradlew.bat installDebug

# Run tests
gradlew.bat test

# Check for dependency updates
gradlew.bat dependencyUpdates
```

### ADB Commands

```cmd
# List connected devices
adb devices

# Install APK
adb install app\build\outputs\apk\debug\app-debug.apk

# Uninstall app
adb uninstall com.appp.avisos

# View logs
adb logcat

# Clear app data
adb shell pm clear com.appp.avisos

# Take screenshot
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png

# Screen recording
adb shell screenrecord /sdcard/demo.mp4
# Press Ctrl+C to stop
adb pull /sdcard/demo.mp4
```

### Project Management

```cmd
# Open project in Android Studio from command line
cd C:\AndroidProjects\ApppAvisos
start studio64.exe

# Open project folder in File Explorer
explorer .

# View project structure
tree /F /A
```

## Development Workflow on Windows

### Daily Development Flow:

1. **Start emulator:**
   ```cmd
   # List available AVDs
   emulator -list-avds
   
   # Start specific AVD
   emulator -avd Appp_Test_Device
   ```

2. **Open project in Android Studio:**
   - File → Open → Select project folder

3. **Make code changes**

4. **Build and run:**
   - Click ▶️ (Run) button
   - Or: Shift+F10

5. **View logs:**
   - Logcat panel at bottom of Android Studio
   - Filter by your package name

6. **Debug:**
   - Click 🐛 (Debug) button
   - Or: Shift+F9
   - Set breakpoints by clicking line numbers

### Git Workflow (if using Git):

```cmd
# Navigate to project
cd C:\AndroidProjects\ApppAvisos

# Check status
git status

# Stage changes
git add .

# Commit
git commit -m "Implemented note creation feature"

# Push to remote
git push origin main
```

## Performance Tips for Windows

1. **Exclude project from Windows Defender:**
   - Windows Security → Virus & threat protection
   - Manage settings → Exclusions → Add exclusion
   - Add: `C:\AndroidProjects\` and `C:\Users\YourUsername\.gradle\`

2. **Use SSD for Android SDK and projects:**
   - Significantly faster builds and emulator performance

3. **Allocate enough RAM:**
   - Close unnecessary applications while developing
   - Recommended: 8GB+ RAM for comfortable development

4. **Use physical device for testing:**
   - Faster than emulator
   - Enable Developer Options and USB Debugging
   - Connect via USB

## Next Steps

Now that your environment is set up, proceed to the [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md) and follow the prompts starting from **Phase 1: Project Setup** (you've already done Prompt 1.1).

### Recommended Learning Path:

1. Complete Phase 1 (skip 1.1, you've done it)
2. Follow Phase 2 to set up the database
3. Test database operations
4. Continue with Phase 3 for UI
5. And so on...

### Getting Help:

- **Android Documentation:** https://developer.android.com/docs
- **Stack Overflow:** Tag your questions with `android` and `kotlin`
- **Android Studio Help:** Help → Help in Android Studio
- **Community:** Reddit r/androiddev

## Summary Checklist

Before starting development, ensure:

- [ ] JDK installed and JAVA_HOME set
- [ ] Android Studio installed
- [ ] Android SDK downloaded (API 24+)
- [ ] Emulator created and tested
- [ ] New project created successfully
- [ ] Dependencies added and synced
- [ ] First build runs successfully
- [ ] Emulator launches app
- [ ] Git installed (optional)
- [ ] Read IMPLEMENTATION_GUIDE.md

**You're ready to start building Appp Avisos!** 🚀

Good luck with your Android development journey!
