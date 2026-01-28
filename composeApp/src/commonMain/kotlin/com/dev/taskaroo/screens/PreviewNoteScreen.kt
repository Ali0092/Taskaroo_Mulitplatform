/**
 * Note preview screen for viewing note details.
 *
 * This screen displays note details in a read-only format with options
 * to edit or delete the note via top bar action buttons.
 *
 * @author Muhammad Ali
 * @date 2026-01-28
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.common.DeleteConfirmationDialog
import com.dev.taskaroo.common.TaskarooTopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.modal.NoteData
import kotlinx.coroutines.launch
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.delete_icon
import taskaroo.composeapp.generated.resources.edit_icon

class PreviewNoteScreen(
    private val noteTimestamp: Long
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        var noteData by remember { mutableStateOf<NoteData?>(null) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(true) }

        // Load note data from database
        LaunchedEffect(noteTimestamp) {
            try {
                isLoading = true
                noteData = databaseHelper.getNoteByTimestamp(noteTimestamp)
                isLoading = false
            } catch (e: Exception) {
                println("PreviewNoteScreen: Error loading note - ${e.message}")
                isLoading = false
                navigator.pop()
            }
        }

        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top App Bar with Edit and Delete buttons
                TaskarooTopAppBar(
                    title = "Note",
                    canShowNavigationIcon = true,
                    otherIcon = Res.drawable.edit_icon,
                    trailingIcon = Res.drawable.delete_icon,
                    onBackButtonClick = {
                        navigator.pop()
                    },
                    onOtherIconClick = {
                        // Navigate to CreateNoteScreen in edit mode
                        navigator.push(CreateNoteScreen(noteTimestampToEdit = noteTimestamp))
                    },
                    onTrailingIconClick = {
                        // Show delete confirmation dialog
                        showDeleteDialog = true
                    }
                )

                // Display note information if loaded
                noteData?.let { note ->
                    // Formatted date
                    Text(
                        text = note.formattedDate,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )

                    // Note Title
                    if (note.title.isNotBlank()) {
                        Text(
                            text = note.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Note Content
                    if (note.content.isNotBlank()) {
                        Text(
                            text = note.content,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth(),
                            lineHeight = 26.sp
                        )
                    }

                    // Add some bottom spacing
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Show loading indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading note...",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        DeleteConfirmationDialog(
            showDialog = showDeleteDialog,
            taskTitle = noteData?.title?.ifEmpty { "this note" } ?: "Note",
            itemType = "Note",
            onConfirm = {
                coroutineScope.launch {
                    try {
                        databaseHelper.deleteNote(noteTimestamp)
                        showDeleteDialog = false
                        navigator.pop()
                    } catch (e: Exception) {
                        println("PreviewNoteScreen: Error deleting note - ${e.message}")
                        showDeleteDialog = false
                    }
                }
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}
