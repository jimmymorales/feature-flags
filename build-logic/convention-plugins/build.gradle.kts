@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `embedded-kotlin`
    `java-gradle-plugin`
    kotlin("jvm")
    alias(libs.plugins.bestPracticesPlugin)
}

group = "dev.jimmymorales.featureflags.gradle"

gradlePlugin {
    plugins {
        create("PublishPlugin") {
            id = "${project.group}.publish"
            implementationClass = "${project.group}.$name"
        }
    }
}

dependencies {
    implementation(gradleKotlinDsl())

    implementation(platform(kotlin("bom", version = libs.versions.kotlin.get())))
    implementation(kotlin("gradle-plugin", version = libs.versions.kotlin.get()))
    implementation(kotlin("reflect", version = libs.versions.kotlin.get()))

    implementation(libs.gradlePlugin.dokka)
    implementation(libs.gradlePlugin.mavenPublish)
}
