@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.kmmbridge)
}

val libName = "FeatureFlagsCore"
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jvm.target.get()
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    iosX64 {
        binaries.framework(libName)
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled = true
                }
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

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
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
        val iosX64Main by getting
        val iosX64Test by getting
    }
}

kmmbridge {
    frameworkName.set(libName)
    manualVersions()
    mavenPublishArtifacts()
    spm(spmDirectory = rootProject.projectDir.path)
}
