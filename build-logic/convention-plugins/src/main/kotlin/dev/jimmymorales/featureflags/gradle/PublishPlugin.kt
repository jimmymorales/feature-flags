package dev.jimmymorales.featureflags.gradle

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

@Suppress("UnstableApiUsage")
public abstract class PublishPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply<MavenPublishPlugin>()
        configure<MavenPublishBaseExtension> {
            publishToMavenCentral(host = SonatypeHost.S01, automaticRelease = true)
            signAllPublications()
        }

        apply<DokkaPlugin>()
        tasks.withType<DokkaTask>().configureEach {
            outputDirectory.set(rootDir.resolve("../docs/0.x"))
            dokkaSourceSets.configureEach {
                skipDeprecated.set(true)
            }
        }
    }
}
