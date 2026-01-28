# Android Project Setup

## Project Structure Created

This Android project has been initialized with the following specifications:

### Project Configuration
- **Project Name**: ApppAvisos
- **Package Name**: com.appp.avisos
- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Target SDK**: API 35 (Latest stable)
- **Language**: Kotlin
- **Build System**: Gradle with Kotlin DSL
- **ViewBinding**: Enabled
- **AndroidX**: Included

### Directory Structure

```
app/
├── build.gradle.kts              # App-level Gradle build file
├── proguard-rules.pro            # ProGuard configuration
└── src/
    ├── androidTest/
    │   └── java/com/appp/avisos/ # Instrumented tests
    ├── main/
    │   ├── AndroidManifest.xml   # App manifest
    │   ├── java/com/appp/avisos/
    │   │   └── MainActivity.kt   # Main activity with ViewBinding
    │   └── res/
    │       ├── drawable/         # Drawable resources
    │       ├── layout/
    │       │   └── activity_main.xml
    │       ├── mipmap-*/         # App icons (all densities)
    │       ├── values/
    │       │   ├── colors.xml
    │       │   ├── strings.xml
    │       │   ├── themes.xml
    │       │   └── ic_launcher_background.xml
    │       └── xml/
    │           ├── backup_rules.xml
    │           └── data_extraction_rules.xml
    └── test/
        └── java/com/appp/avisos/ # Unit tests
```

### Key Files Created

1. **build.gradle.kts** (root): Project-level build configuration
2. **settings.gradle.kts**: Project settings and module includes
3. **app/build.gradle.kts**: App module configuration with:
   - Android plugin configuration
   - Kotlin plugin configuration
   - ViewBinding enabled
   - AndroidX dependencies (Core, AppCompat, Material, ConstraintLayout)
   - Testing dependencies (JUnit, Espresso)

4. **MainActivity.kt**: Empty activity with ViewBinding implementation
5. **activity_main.xml**: Basic layout with TextView
6. **AndroidManifest.xml**: App manifest with MainActivity declaration

### Dependencies Included

- **androidx.core:core-ktx:1.15.0**
- **androidx.appcompat:appcompat:1.7.0**
- **com.google.android.material:material:1.12.0**
- **androidx.constraintlayout:constraintlayout:2.2.0**
- **junit:junit:4.13.2** (test)
- **androidx.test.ext:junit:1.2.1** (androidTest)
- **androidx.test.espresso:espresso-core:3.6.1** (androidTest)

### Build Configuration

- **Android Gradle Plugin**: 8.3.0
- **Kotlin**: 1.9.22
- **Gradle Wrapper**: 8.9
- **Java Version**: 17 (sourceCompatibility and targetCompatibility)
- **Kotlin JVM Target**: 17

### Gradle Wrapper

The project includes gradle wrapper scripts:
- `gradlew` (Unix/Linux/Mac)
- `gradlew.bat` (Windows)
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`

### Building the Project

To build the project (requires internet access to download dependencies):

```bash
./gradlew build
```

To run on an emulator or device:

```bash
./gradlew installDebug
```

### .gitignore

A comprehensive `.gitignore` file has been created to exclude:
- Build artifacts (`build/`, `*.apk`, `*.aab`)
- Gradle cache (`.gradle/`)
- IDE files (`.idea/`, `*.iml`)
- Local configuration (`local.properties`)
- Generated files

### Next Steps

The project structure is ready for development. You can now:
1. Open the project in Android Studio
2. Sync Gradle dependencies
3. Run the app on an emulator or device
4. Start implementing the app features

**Note**: The project requires internet access on first build to download Android Gradle Plugin and dependencies from Maven repositories.
