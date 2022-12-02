package dev.jimmymorales.featureflags.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

public abstract class PublishPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply<MavenPublishPlugin>()
        configure<PublishingExtension> {
            repositories {

            }
        }
    }
}
