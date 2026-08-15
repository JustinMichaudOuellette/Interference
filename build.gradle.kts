// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("kotlin_version", "2.2.10")
        set("appcompat_version", "1.6.1")
        set("recyclerview_version", "1.3.2")
        set("materialVersion", "1.11.0")
        set("lifeCycleVersion", "2.6.2")
        set("coroutinesVersion", "1.7.1")
        set("fragmentVersion", "1.6.2")
    }

    repositories {
        mavenCentral()
        google()
    }
    dependencies {

    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

subprojects {
    if (name == "library") {
        // No need to apply kotlin-android manually as compose plugin handles it
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
