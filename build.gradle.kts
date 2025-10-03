plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
    compileOnly(name = "strikepractice-api", version = "", ext = "jar")
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
        relocate("org.bstats", "${project.group}.bstats")
    }
    
    build {
        dependsOn(shadowJar)
    }
}