/**
 * Note creation and editing screen with auto-save functionality.
 *
 * This screen provides a minimal interface for creating and editing notes.
 * Features include large text inputs for title and content, automatic cursor
 * focus on the content field, and auto-save on back navigation.
 *
 * @author Muhammad Ali
 * @date 2026-01-28
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.common.TaskarooTopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.modal.NoteData
import com.dev.taskaroo.utils.currentTimeMillis
import kotlinx.coroutines.launch

class CreateNoteScreen(
    private val noteTimestampToEdit: Long? = null
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()
        val focusRequester = remember { FocusRequester() }

        // Determine if we're in edit mode
        val isEditMode = noteTimestampToEdit != null
        var existingNote by remember { mutableStateOf<NoteData?>(null) }

        // Form states
        var noteTitle by remember { mutableStateOf("") }
        var noteContent by remember { mutableStateOf("") }

        // Load existing note if in edit mode
        LaunchedEffect(noteTimestampToEdit) {
            if (noteTimestampToEdit != null) {
                try {
                    existingNote = databaseHelper.getNoteByTimestamp(noteTimestampToEdit)
                    existingNote?.let { note ->
                        noteTitle = note.title
                        noteContent = note.content
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Request focus on content field after a short delay
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(300)
            focusRequester.requestFocus()
        }

        // Auto-save function
        fun saveNote() {
            if (noteTitle.isNotBlank() || noteContent.isNotBlank()) {
                coroutineScope.launch {
                    try {
                        val timestampMillis = if (isEditMode) {
                            noteTimestampToEdit!!
                        } else {
                            currentTimeMillis()
                        }

                        val noteData = NoteData(
                            timestampMillis = timestampMillis,
                            title = noteTitle.trim(),
                            content = noteContent.trim()
                        )

                        if (isEditMode) {
                            databaseHelper.updateNote(noteData)
                        } else {
                            databaseHelper.insertNote(noteData)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
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
                TaskarooTopAppBar(
                    title = if (isEditMode) "Edit note" else "New note",
                    canShowNavigationIcon = true,
                    onBackButtonClick = {
                        saveNote()
                        navigator.pop()
                    }
                )

                Spacer(Modifier.height(8.dp))

                // Title input
                BasicTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (noteTitle.isEmpty()) {
                            Text(
                                text = "Title",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        }
                        innerTextField()
                    }
                )

                // Content input
                BasicTextField(
                    value = noteContent,
                    onValueChange = { noteContent = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (noteContent.isEmpty()) {
                            Text(
                                text = "Start typing...",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
