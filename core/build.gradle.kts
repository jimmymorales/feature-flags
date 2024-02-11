import co.touchlab.faktory.versionmanager.VersionManager
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.kmmbridge)
}

val libName = "FeatureFlagsCore"
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.target.get()))
        vendor.set(JvmVendorSpec.AZUL)
    }

    jvm()

    val applyCommonKotlinNativeConfiguration: KotlinNativeTarget.() -> Unit = {
        binaries.framework(libName)
    }
    iosX64(applyCommonKotlinNativeConfiguration)
    iosArm64(applyCommonKotlinNativeConfiguration)
    iosSimulatorArm64(applyCommonKotlinNativeConfiguration)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.stately.collections)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

kmmbridge {
    mavenPublishArtifacts()
    spm()
    versionManager.set(object : VersionManager {
        override fun getVersion(project: Project): String {
            // For some reason project.version is retuning unspecified
            return project.rootProject.findProperty("VERSION_NAME").toString()
        }
    })
}
