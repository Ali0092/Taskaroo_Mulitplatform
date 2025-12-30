/**
 * Data models for user preferences
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.modal

import org.jetbrains.compose.resources.DrawableResource

/**
 * Represents a user preference item with an icon and title.
 * Used for displaying preference options in the application settings.
 *
 * @property icon The drawable resource for the preference icon
 * @property title The title or label for the preference item (default: empty string)
 */
data class PrefsModel(val icon: DrawableResource, val title:String = "")