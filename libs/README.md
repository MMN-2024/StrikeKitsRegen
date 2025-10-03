# StrikePractice Dependency

This plugin depends on the **StrikePractice** plugin which is not available in public Maven repositories. The plugin will compile without it but requires it to function properly.

## How to add the StrikePractice dependency:

1. Obtain the `strikepractice-api.jar` file from the plugin author or your server
2. Place the JAR file in this `libs/` directory with the exact name: `strikepractice-api.jar`
3. Run `./gradlew build` to compile the plugin

## Building without StrikePractice API:

The plugin will compile even without the StrikePractice API JAR file. However:
- The plugin will log an error on startup if StrikePractice is not available
- No functionality will work without StrikePractice installed on the server
- This allows you to compile and test the basic structure

The build.gradle.kts is already configured to look for the JAR in this directory using:
```kotlin
flatDir { dirs("libs") }
```

## Note

If you don't have access to the StrikePractice API JAR, the build will fail. You need to obtain the `strikepractice-api.jar` file and place it in this directory.

The plugin will not function correctly on a Minecraft server without StrikePractice installed.
