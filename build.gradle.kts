buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        // We have to declare this here in order for kotlin-facets to be generated in iml files
        // https://youtrack.jetbrains.com/issue/KT-36331
        classpath(kotlin("gradle-plugin", libs.versions.kotlin.get()))
    }
}
