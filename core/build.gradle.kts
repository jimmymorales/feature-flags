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

object NoOpVersionManager : VersionManager {
    override fun getVersion(project: Project, versionPrefix: String): String = versionPrefix
    override fun recordVersion(project: Project, versionString: String) {}
}
kmmbridge {
    frameworkName.set(libName)
    versionManager.set(NoOpVersionManager)
    mavenPublishArtifacts()
    spm(spmDirectory = rootProject.projectDir.path, commitManually = true)
}
