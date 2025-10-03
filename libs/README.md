# StrikePractice Dependency

This plugin depends on the **StrikePractice** plugin which is not available in public Maven repositories.

## How to add the StrikePractice dependency:

1. Obtain the `strikepractice-3.12.1.jar` file from the plugin author or your server
2. Place the JAR file in this `libs/` directory with the exact name: `strikepractice-3.12.1.jar`
3. Run `./gradlew build` to compile the plugin

## Current Status:

✅ **StrikePractice JAR is present**: `strikepractice-3.12.1.jar`
- The plugin should now compile with full API access
- All functionality will be available when StrikePractice is installed on the server

The build.gradle.kts is already configured to look for the JAR in this directory using:
```kotlin
flatDir { dirs("libs") }
```

## Building:

Run `./gradlew build` to compile the plugin. The compiled JAR will be in `build/libs/`.

## Note

The plugin will not function correctly on a Minecraft server without StrikePractice 3.12.1+ installed.