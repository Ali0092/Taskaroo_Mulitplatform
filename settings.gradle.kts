/*
 * Taskaroo Gradle Settings Configuration
 *
 * Configures Gradle project settings including repository sources,
 * dependency resolution, and module structure for the Taskaroo
 * Kotlin Multiplatform application.
 *
 * Author: Muhammad Ali
 * Date: 2025-12-30
 * Portfolio: https://muhammadali0092.netlify.app/
 */

rootProject.name = "Taskaroo"

// Enable type-safe project accessors for better IDE support
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

/**
 * Plugin Management Configuration
 * Defines repositories for resolving Gradle plugins
 */
pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

/**
 * Dependency Resolution Management
 * Configures repositories for resolving project dependencies
 */
dependencyResolutionManagement {
    repositories {
        // Google's Maven repository for Android and Compose dependencies
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        // Maven Central for Kotlin and other open-source dependencies
        mavenCentral()
    }
}

// Include the main Compose application module
include(":composeApp")