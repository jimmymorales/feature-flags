@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        // Gradle's plugin portal proxies jcenter, which we don't want. To avoid this, we specify
        // exactly which dependencies to pull from here.
        exclusiveContent {
            forRepository(::gradlePluginPortal)
            filter {
                includeModule(
                    "com.autonomousapps.dependency-analysis",
                    "com.autonomousapps.dependency-analysis.gradle.plugin"
                )
                includeModule(
                    "com.autonomousapps.plugin-best-practices-plugin",
                    "com.autonomousapps.plugin-best-practices-plugin.gradle.plugin"
                )
                includeModule("com.autonomousapps", "plugin-best-practices-plugin")
                includeModule("com.diffplug.spotless", "com.diffplug.spotless.gradle.plugin")
                includeModule("org.gradle.kotlin.embedded-kotlin", "org.gradle.kotlin.embedded-kotlin.gradle.plugin")
                includeModule("org.gradle.kotlin", "gradle-kotlin-dsl-plugins")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"

include(":convention-plugins")

// https://docs.gradle.org/7.5/userguide/groovy_plugin.html#sec:groovy_compilation_avoidance
enableFeaturePreview("GROOVY_COMPILATION_AVOIDANCE")
