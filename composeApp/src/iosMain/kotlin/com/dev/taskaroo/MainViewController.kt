/**
 * iOS application entry point and view controller.
 *
 * Creates the main UIViewController for the iOS application using Compose Multiplatform's
 * ComposeUIViewController. This bridges the gap between UIKit (iOS) and Compose UI.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo

import androidx.compose.ui.window.ComposeUIViewController

/**
 * Creates the main UIViewController for the iOS app.
 *
 * This function is called from the iOS Swift/Objective-C code to create
 * the root view controller containing the Compose Multiplatform UI.
 *
 * @return ComposeUIViewController containing the App() composable
 */
fun MainViewController() = ComposeUIViewController { App() }