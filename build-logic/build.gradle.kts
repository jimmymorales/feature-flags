import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverExtension

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.samWithReceiver) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.dependencyAnalysis)
    alias(libs.plugins.detekt)
}

configure<DetektExtension> {
    toolVersion = libs.versions.detekt.get()
    allRules = true
    buildUponDefaultConfig = true
    config.from(rootProject.file("../config/detekt/detekt.yaml"))
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
    }
}

val ktfmtVersion = libs.versions.ktfmt.get()
allprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<SpotlessExtension> {
        format("misc") {
            target("*.md", ".gitignore")
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlin {
            target("src/**/*.kt")
            ktfmt(ktfmtVersion).googleStyle()
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlinGradle {
            target("src/**/*.kts")
            ktfmt(ktfmtVersion).googleStyle()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

data class KotlinBuildConfig(val kotlin: String, val kotlinJvmTarget: String) {
    // Left as a toe-hold for any future needs
    private val extraArgs = arrayOf<String>()

    val kotlinCompilerArgs: List<String> = listOf(
        "-progressive",
        "-Xinline-classes",
        "-Xjsr305=strict",
        "-opt-in=kotlin.contracts.ExperimentalContracts",
        "-opt-in=kotlin.experimental.ExperimentalTypeInference",
        "-opt-in=kotlin.ExperimentalStdlibApi",
        "-opt-in=kotlin.time.ExperimentalTime",
        // Match JVM assertion behavior: https://publicobject.com/2019/11/18/kotlins-assert-is-not-like-javas-assert/
        "-Xassertions=jvm",
        // Potentially useful for static analysis tools or annotation processors.
        "-Xemit-jvm-type-annotations",
        "-Xproper-ieee754-comparisons",
        // Enable new jvm-default behavior
        // https://blog.jetbrains.com/kotlin/2020/07/kotlin-1-4-m3-generating-default-methods-in-interfaces/
        "-Xjvm-default=all",
        // https://kotlinlang.org/docs/whatsnew1520.html#support-for-jspecify-nullness-annotations
        "-Xtype-enhancement-improvements-strict-mode",
        "-Xjspecify-annotations=strict",
        // Enhance not null annotated type parameter's types to definitely not null types (@NotNull T => T & Any)
        "-Xenhance-type-parameter-types-to-def-not-null",
        // Use fast implementation on Jar FS.
        // This may speed up compilation time, but currently it's an experimental mode
        // TODO toe-hold but we can't use it yet because it emits a warning that fails with -Werror
        // https://youtrack.jetbrains.com/issue/KT-54928
        // "-Xuse-fast-jar-file-system",
        // Support inferring type arguments based on only self upper bounds of the corresponding type parameters
        "-Xself-upper-bound-inference",
    ) + extraArgs
}

val kotlinVersion = libs.versions.kotlin.get()
val kotlinJvmTarget = libs.versions.jvm.target.get()
val kotlinBuildConfig = KotlinBuildConfig(kotlinVersion, kotlinJvmTarget)

subprojects {
    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get().removeSuffix("-ea").toInt()))
            }
        }

        tasks.withType<JavaCompile>().configureEach {
            options.release.set(libs.versions.jvm.target.get().toInt())
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                languageVersion = "1.7"
                apiVersion = "1.7"
                // Gradle forces a lower version of kotlin, which results in warnings that prevent use of
                // this sometimes. https://github.com/gradle/gradle/issues/16345
                allWarningsAsErrors = false
                jvmTarget = kotlinBuildConfig.kotlinJvmTarget
                // We use class SAM conversions because lambdas compiled into invokedynamic are not
                // Serializable, which causes accidental headaches with Gradle configuration caching. It's
                // easier for us to just use the previous anonymous classes behavior
                @Suppress("SuspiciousCollectionReassignment")
                freeCompilerArgs += (kotlinBuildConfig.kotlinCompilerArgs + "-Xsam-conversions=class")
                    // -progressive is useless when running on an older language version but new compiler version
                    .filter { it != "-progressive" }
                    // We should be able to remove this in Gradle 8 when it upgrades to Kotlin 1.7
                    .plus("-opt-in=kotlin.RequiresOptIn")
            }
        }

        extensions.configure<KotlinProjectExtension> {
            explicitApi()
        }

        // Reimplement kotlin-dsl's application of this function for nice DSLs
        apply(plugin = "kotlin-sam-with-receiver")
        configure<SamWithReceiverExtension> {
            annotation("org.gradle.api.HasImplicitReceiver")
        }
    }
}

dependencyAnalysis {
    abi {
        exclusions {
            ignoreInternalPackages()
            ignoreGeneratedCode()
        }
    }
    dependencies {
        bundle("agp") {
            primary("com.android.tools.build:gradle")
            includeGroup("com.android.tools.build")
            includeDependency("com.google.code.findbugs:jsr305")
        }
    }
}
