/**
 * Notes list screen displaying all notes in a 2-column grid.
 *
 * This screen serves as the main interface for viewing notes.
 * It displays notes in a staggered grid layout with title and date,
 * and provides a FAB for creating new notes.
 *
 * @author Muhammad Ali
 * @date 2026-01-28
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.edit_icon
import taskaroo.composeapp.generated.resources.no_task

class NotesScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current
        val coroutineScope = rememberCoroutineScope()

        var notes by remember { mutableStateOf<List<NoteData>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var noteToDelete by remember { mutableStateOf<NoteData?>(null) }

        // Load notes from database - re-fetch whenever this screen becomes the top screen
        LaunchedEffect(navigator.lastItem) {
            try {
                isLoading = true
                notes = databaseHelper.getAllNotes()
            } catch (e: Exception) {
                e.printStackTrace()
                notes = emptyList()
            } finally {
                isLoading = false
            }
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navigator.push(CreateNoteScreen())
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.edit_icon),
                        contentDescription = "Create Note",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            ) {
                TaskarooTopAppBar(
                    title = "Your notes",
                    canShowNavigationIcon = true,
                    onBackButtonClick = {
                        navigator.pop()
                    }
                )

                Spacer(Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading notes...",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                } else if (notes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.no_task),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No notes yet\ntap + to create one",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(notes, key = { it.timestampMillis }) { note ->
                            NoteCard(
                                noteData = note,
                                onClick = {
                                    navigator.push(CreateNoteScreen(noteTimestampToEdit = note.timestampMillis))
                                },
                                onLongClick = {
                                    noteToDelete = note
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        DeleteConfirmationDialog(
            showDialog = showDeleteDialog,
            taskTitle = noteToDelete?.title?.ifEmpty { "this note" } ?: "Note",
            itemType = "Note",
            onConfirm = {
                noteToDelete?.let { note ->
                    coroutineScope.launch {
                        try {
                            databaseHelper.deleteNote(note.timestampMillis)
                            notes = notes.filter { it.timestampMillis != note.timestampMillis }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                showDeleteDialog = false
                noteToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                noteToDelete = null
            }
        )
    }
}


/**
 * Individual note card displaying title, content preview, and formatted date
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteCard(
    noteData: NoteData,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { onLongClick() }
                ),
            border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = noteData.title.ifEmpty { "Untitled" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (noteData.content.isNotBlank()) {
                    Text(
                        text = noteData.content,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = noteData.formattedDate,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}
