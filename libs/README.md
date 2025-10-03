# StrikePractice Dependency

This plugin depends on the **StrikePractice** plugin which is not available in public Maven repositories.

## How to add the StrikePractice dependency:

1. Obtain the `StrikePractice.jar` file from the plugin author or your server
2. Place the JAR file in this `libs/` directory
3. Uncomment the following line in `build.gradle.kts`:
   ```kotlin
   compileOnly(files("libs/StrikePractice.jar"))
   ```
4. Run `./gradlew build` to compile the plugin

## Alternative: Build without StrikePractice

If you don't have access to the StrikePractice JAR, the build will fail. You need to either:
- Get the StrikePractice.jar file
- Or contact the plugin developer for access to the dependency

The plugin will not function correctly on a Minecraft server without StrikePractice installed.
