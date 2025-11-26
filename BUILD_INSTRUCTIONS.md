# Build Instructions - StandBy Mode App

This document provides complete instructions for building the StandBy Mode Android application.

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 21**
   - Download from: https://www.oracle.com/java/technologies/downloads/#java21
   - Install to: `C:\Program Files\Java\jdk-21`

2. **Android SDK**
   - Included with Android Studio OR
   - Install standalone SDK command-line tools

3. **Git** (optional, for version control)

### Environment Setup

**Windows PowerShell**:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:ANDROID_HOME = "C:\Users\<YourUsername>\AppData\Local\Android\Sdk"
```

Add to system PATH (optional, for persistent setup):
- `C:\Program Files\Java\jdk-21\bin`
- `%ANDROID_HOME%\platform-tools`

---

## Building the Project

### 1. Clean Build (Recommended)

```powershell
cd d:\Standby
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
./gradlew clean build
```

**Expected Output**:
```
BUILD SUCCESSFUL in Xm Ys
```

### 2. Debug APK Only

```powershell
./gradlew assembleDebug
```

**Output Location**: `app/build/outputs/apk/debug/app-debug.apk`

### 3. Release APK (Unsigned)

```powershell
./gradlew assembleRelease
```

**Output Location**: `app/build/outputs/apk/release/app-release-unsigned.apk`

---

## Installation

### Install to Connected Device

1. **Enable USB Debugging** on Android device:
   - Settings → About Phone → Tap "Build Number" 7 times
   - Settings → Developer Options → USB Debugging (ON)

2. **Connect device via USB**

3. **Install Debug APK**:
   ```powershell
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Or install via file manager**:
   - Copy APK to device
   - Open with file manager
   - Tap to install (allow unknown sources if prompted)

---

## Troubleshooting

### Issue: "JAVA_HOME is set to an invalid directory"

**Solution**:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

Make sure path does **NOT** include `\bin` at the end.

### Issue: "SDK location not found"

**Solution**:
Create/update `local.properties`:
```
sdk.dir=C\:\\Users\\<YourUsername>\\AppData\\Local\\Android\\Sdk
```

### Issue: Build fails with "Daemon not running"

**Solution**:
```powershell
./gradlew --stop
./gradlew clean build
```

### Issue: ViewBinding not found

**Solution**:
Ensure `build.gradle.kts` has:
```kotlin
buildFeatures {
    viewBinding = true
}
```

Then clean and rebuild.

---

## Build Variants

The project supports two build variants:

- **debug**: Includes debugging info, not optimized
- **release**: Optimized, minified (requires signing for distribution)

Build specific variant:
```powershell
./gradlew assembleDebug
./gradlew assembleRelease
```

---

## Gradle Tasks

### Useful Commands

```powershell
# List all tasks
./gradlew tasks

# Clean build directory
./gradlew clean

# Run unit tests
./gradlew test

# Run connected Android tests
./gradlew connectedAndroidTest

# Generate lint report
./gradlew lint

# Dependency tree
./gradlew dependencies
```

---

## Project Configuration Files

### Key Files
- `build.gradle.kts` (root) - Project-level build config
- `app/build.gradle.kts` - App module build config
- `gradle.properties` - Gradle configurations
- `local.properties` - Local SDK path (gitignored)
- `settings.gradle.kts` - Project settings

### Dependencies

Defined in `app/build.gradle.kts`:
```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    // ... see file for complete list
}
```

Version catalog in `gradle/libs.versions.toml`

---

## Development Workflow

### Recommended Development Cycle

1. **Make Code Changes**
2. **Clean Build**:
   ```powershell
   ./gradlew clean assembleDebug
   ```
3. **Install on Device**:
   ```powershell
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
4. **Test Features**
5. **Check Logs**:
   ```powershell
   adb logcat -s StandByMode
   ```

### Incremental Build (Faster)

```powershell
./gradlew assembleDebug  # No clean step
```

Use when only Kotlin code changed (not resources/manifest).

---

## CI/CD Integration

For automated builds:

```yaml
# Example GitHub Actions
- name: Build Debug APK
  run: |
    chmod +x ./gradlew
    ./gradlew assembleDebug
    
- name: Upload APK
  uses: actions/upload-artifact@v3
  with:
    name: app-debug
    path: app/build/outputs/apk/debug/app-debug.apk
```

---

## Signing for Release

To sign release APK:

1. **Generate Keystore**:
   ```powershell
   keytool -genkey -v -keystore release.keystore -alias standby_key -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Update `build.gradle.kts`**:
   ```kotlin
   signingConfigs {
       create("release") {
           storeFile = file("release.keystore")
           storePassword = "your_password"
           keyAlias = "standby_key"
           keyPassword = "your_password"
       }
   }
   ```

3. **Build Signed APK**:
   ```powershell
   ./gradlew assembleRelease
   ```

---

## Build Time Optimization

### Enable Gradle Daemon

In `gradle.properties`:
```
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
```

### Increase Heap Size

In `gradle.properties`:
```
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m
```

---

## Support

- **Gradle Version**: 8.2.0
- **Android Gradle Plugin**: 8.13.1
- **Kotlin Version**: 1.9.0+

For issues, check:
- Gradle daemon logs: `.gradle/daemon/`
- Build logs: `app/build/outputs/logs/`
