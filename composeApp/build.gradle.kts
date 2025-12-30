/*
 * Taskaroo Compose App Module Build Configuration
 *
 * This is the main application module build file that configures:
 * - Kotlin Multiplatform for Android and iOS targets
 * - Compose Multiplatform for shared UI
 * - SQLDelight for database management
 * - Platform-specific dependencies and configurations
 *
 * Target Platforms:
 * - Android: minSdk 24, targetSdk 36
 * - iOS: x64, ARM64, Simulator ARM64
 *
 * Author: Muhammad Ali
 * Date: 2025-12-30
 * Portfolio: https://muhammadali0092.netlify.app/
 */

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import kotlin.collections.set

// Apply required plugins for Kotlin Multiplatform, Android, Compose, and SQLDelight
plugins {
    alias(libs.plugins.kotlinMultiplatform)      // Kotlin Multiplatform support
    alias(libs.plugins.androidApplication)       // Android application
    alias(libs.plugins.composeMultiplatform)     // Compose Multiplatform UI
    alias(libs.plugins.composeCompiler)          // Compose compiler
    alias(libs.plugins.sqldelight)               // SQLDelight database
}

/**
 * Kotlin Multiplatform Configuration
 * Defines target platforms and their specific settings
 */
kotlin {
    androidTarget()

    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = false
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // SQLDelight Android Driver
            implementation(libs.sqldelight.android)
        }
        iosMain.dependencies {
            // SQLDelight Native Driver
            implementation(libs.sqldelight.native)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(compose.materialIconsExtended)

            // Voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)

            // Calendar
            implementation(libs.kotlinx.datetime)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
android {
    namespace = "com.dev.taskaroo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.dev.taskaroo"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

sqldelight {
    databases {
        create("TaskDatabase") {
            packageName.set("com.dev.taskaroo.database")
        }
    }
}

