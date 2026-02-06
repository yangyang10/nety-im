// Top-level build file where you can add configuration options common to all sub-projects/modules.
apply(from = "config.gradle")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    id("io.github.gradle-nexus.publish-plugin") version "1.3.0" apply false
}