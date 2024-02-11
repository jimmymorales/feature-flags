@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.dokka.gradle.DokkaTask

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.agp.app) apply false // needed for android sample
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.mavenPublish) apply false
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
