plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
}

group = project.findProperty("group") as String? ?: "me.balda.strikekitsregen"
version = project.findProperty("version") as String? ?: "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    flatDir {
        dirs("libs")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    // StrikePractice API - will be loaded at runtime if available
    if (file("libs/strikepractice-3.12.1.jar").exists()) {
        compileOnly(files("libs/strikepractice-3.12.1.jar"))
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
    
    javadoc {
        options.encoding = "UTF-8"
    }
    
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    
    shadowJar {
        archiveClassifier.set("")
        // Minimize the jar by removing unused classes
        minimize()
    }
    
    build {
        dependsOn(shadowJar)
    }
}