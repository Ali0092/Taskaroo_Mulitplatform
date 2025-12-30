/*
 * Taskaroo Root Build Configuration
 *
 * This is the root build file for the Taskaroo Kotlin Multiplatform project.
 * It declares all plugins used across subprojects without applying them,
 * allowing each module to selectively apply required plugins.
 *
 * Author: Muhammad Ali
 * Date: 2025-12-30
 * Portfolio: https://muhammadali0092.netlify.app/
 */

plugins {
    // Kotlin Multiplatform and Compose Multiplatform plugins
    // Using 'apply false' prevents loading plugins multiple times in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false  // Android application plugin
    alias(libs.plugins.androidLibrary) apply false      // Android library plugin
    alias(libs.plugins.composeMultiplatform) apply false  // Compose Multiplatform UI
    alias(libs.plugins.composeCompiler) apply false     // Compose compiler plugin
    alias(libs.plugins.kotlinMultiplatform) apply false  // Kotlin Multiplatform plugin
}