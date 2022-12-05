@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.dokka.gradle.DokkaTask

buildscript {
    dependencies {
        // We have to declare this here in order for kotlin-facets to be generated in iml files
        // https://youtrack.jetbrains.com/issue/KT-36331
        classpath(kotlin("gradle-plugin", libs.versions.kotlin.get()))
        classpath(kotlin("sam-with-receiver", libs.versions.kotlin.get()))
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.agp.app) apply false // needed for android sample
}

subprojects {
    pluginManager.withPlugin("com.vanniktech.maven.publish") {
        apply(plugin = "org.jetbrains.dokka")

        tasks.withType<DokkaTask>().configureEach {
            outputDirectory.set(rootDir.resolve("../docs/0.x"))
            dokkaSourceSets.configureEach {
                skipDeprecated.set(true)
            }
        }

        configure<MavenPublishBaseExtension> {
            publishToMavenCentral(host = com.vanniktech.maven.publish.SonatypeHost.S01, automaticRelease = true)
            signAllPublications()
        }
    }
}
