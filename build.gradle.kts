// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20-RC2" apply false
    id("com.google.gms.google-services") version "4.3.14" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.23" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.49")
    }
}
