package android.ktcodelab.mydailynote.presentation.screens.write

import android.annotation.SuppressLint
import android.ktcodelab.mydailynote.data.repository.GalleryImage
import android.ktcodelab.mydailynote.data.repository.GalleryState
import android.ktcodelab.mydailynote.model.MoodModel
import android.ktcodelab.mydailynote.model.NoteModel
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import java.time.ZonedDateTime

@OptIn(ExperimentalPagerApi::class)
@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun WriteScreen(
    uiState: UiState,
    pagerState: PagerState,
    onDeleteConfirmed: () -> Unit,
    onBackPressed: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    moodName: () -> String,
    backgroundColor: () -> Color,
    onSavedClicked: (NoteModel) -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    galleryState: GalleryState,
    onImageSelect: (Uri) -> Unit,
    onImageDeleteClicked: (GalleryImage) -> Unit,
) {

    var selectedGalleryImage by remember {

        mutableStateOf<GalleryImage?>(null)
    }


    //Update the mood when selecting an existing Note
    LaunchedEffect(key1 = uiState.mood) {

        pagerState.scrollToPage(MoodModel.valueOf(uiState.mood.name).ordinal)
    }

    Scaffold(

        topBar = {

            WriteTopBar(
                selectedNote = uiState.selectedNote,
                moodName = moodName,
                backgroundColor = backgroundColor,
                onDeleteConfirmed = onDeleteConfirmed,
                onBackPressed = onBackPressed,
                onDateTimeUpdated = onDateTimeUpdated
            )
        },
        content = { paddingValues ->

            WriteContent(
                uiState = uiState,
                pagerState = pagerState,
                galleryState = galleryState,
                paddingValues = paddingValues,
                title = uiState.title,
                onTitleChanged = onTitleChanged,
                description = uiState.description,
                onDescriptionChanged = onDescriptionChanged,
                onSavedClicked = onSavedClicked,
                onImageSelect = onImageSelect,
                onImageClicked = { selectedGalleryImage = it },
                backgroundColor = backgroundColor,
            )



            AnimatedVisibility(visible = selectedGalleryImage != null) {

                Dialog(
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                    onDismissRequest = { selectedGalleryImage = null }
                ) {

                    if (selectedGalleryImage != null) {

                            ZoomableImage(
                                selectedGalleryImage = selectedGalleryImage!!,
                                onCloseClicked = { selectedGalleryImage = null }
                            ) {

                                if (selectedGalleryImage != null) {

                                    onImageDeleteClicked(selectedGalleryImage!!)
                                    selectedGalleryImage = null
                                }
                            }
                    }
                }
            }
        }
    )
}

@Composable
fun ZoomableImage(
    selectedGalleryImage: GalleryImage,
    onCloseClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }


        Box(
            modifier = Modifier
                .pointerInput(Unit) {

                    detectTransformGestures { _, pan, zoom, _ ->

                        scale = maxOf(1f, minOf(scale * zoom, 5f))

                        val maxX = (size.width * (scale - 1)) / 2

                        val minX = -maxX

                        offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))

                        val maxY = (size.height * (scale - 1)) / 2

                        val minY = -maxY

                        offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
                    }
                }


        ) {

            AsyncImage(

                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(

                        scaleX = maxOf(0.5f, minOf(3f, scale)),
                        scaleY = maxOf(0.5f, minOf(3f, scale)),
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(selectedGalleryImage.image.toString())
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Fit,
                contentDescription = "Gallery Image"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Button(onClick = onCloseClicked) {

                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")

                    Text(text = "Close")

                }

                Button(onClick = onDeleteClicked) {

                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")

                    Text(text = "Delete")

                }
            }
        }
}