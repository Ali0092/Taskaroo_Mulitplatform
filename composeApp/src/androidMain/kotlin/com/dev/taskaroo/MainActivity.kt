/**
 * Android application entry point.
 *
 * Main activity that serves as the Android entry point for the Taskaroo application.
 * Sets up edge-to-edge display and initializes the Compose UI hierarchy.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dev.taskaroo.preferences.AndroidPreferencesManager

/**
 * Main activity for the Android application.
 *
 * Extends ComponentActivity to provide Compose compatibility and sets up
 * the entire app's UI hierarchy through the App() composable.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting.
     * Enables edge-to-edge display and sets the Compose content.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AndroidPreferencesManager.initialize(this)

        setContent {
            App()
        }
    }
}
