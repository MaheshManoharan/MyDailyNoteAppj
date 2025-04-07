package android.ktcodelab.mydailynote.presentation.components

import android.ktcodelab.mydailynote.model.MoodModel
import android.ktcodelab.mydailynote.model.NoteModel
import android.ktcodelab.mydailynote.ui.theme.*
import android.ktcodelab.mydailynote.util.fetchImagesFromFirebase
import android.ktcodelab.mydailynote.util.toInstant
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@Composable
fun NoteHolder(
    note: NoteModel,
    onClick: (String) -> Unit,
) {
    val localDensity = LocalDensity.current

    val context = LocalContext.current

    var componentHeight by remember {
        mutableStateOf(0.dp)
    }

    var galleryOpened by remember {

        mutableStateOf(false)
    }

    var galleryLoading by remember {

        mutableStateOf(false)
    }

    val downloadedImages = remember {
        mutableStateListOf<Uri>()
    }

    LaunchedEffect(key1 = galleryOpened) {

        if (galleryOpened && downloadedImages.isEmpty()) {

            galleryLoading = true

            fetchImagesFromFirebase(

                remoteImagePaths = note.images,
                onImageDownload = {image ->

                    downloadedImages.add(image)
                },
                onImageDownloadFailed = {

                    Toast.makeText(context, "Images not uploaded yet. wait a little bit or try again.",
                        Toast.LENGTH_SHORT).show()

                    galleryLoading = false
                    galleryOpened = false
                },
                onReadyToDisplay = {

                    galleryLoading = false
                    galleryOpened = true
                }
            )
        }
    }


    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) { onClick(note._id.toString()) }
    ) {

        Spacer(modifier = Modifier.width(14.dp))

        Surface(
            modifier = Modifier
                .width(2.dp)
                .height(componentHeight + 14.dp),
            tonalElevation = Elevation.level1
        ) {

        }

        Spacer(modifier = Modifier.width(20.dp))

        Surface(
            modifier = Modifier
                .clip(shape = Shapes().medium)
                .onGloballyPositioned {

                    //Calculate the Height of Whole Component
                    componentHeight = with(localDensity) { it.size.height.toDp() }
                },
            tonalElevation = Elevation.level1
        ) {

            //Note Content

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MoodModel.valueOf(note.mood).backgroundColor)
            ) {

                NoteHeader(
                    moodName = note.mood, noteTile = note.title, time = note.date.toInstant())

                Text(
                    modifier = Modifier
                        .padding(all = 14.dp),
                    text = note.description,
                    style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize, color = md_theme_dark_surface),
                    maxLines = 8,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )

                if (note.images.isNotEmpty()) {

                    ShowGalleryButton(
                        galleryOpened = galleryOpened,
                        galleryLoading = galleryLoading,
                        onClick = {
                            galleryOpened = !galleryOpened
                        }
                    )
                }

                AnimatedVisibility(
                    visible = galleryOpened && !galleryLoading,
                    enter = fadeIn() + expandVertically(
                        animationSpec = spring(

                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {

                    Column(Modifier.padding(14.dp)) {

                        Gallery(images = downloadedImages)
                    }
                }
            }
        }
    }
}


@Composable
fun NoteHeader(
    moodName: String,
    noteTile: String,
    time: Instant
) {
    val mood by remember {
        mutableStateOf(MoodModel.valueOf(moodName))
    }

    val getTitle by remember {
        mutableStateOf(noteTile)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(mood.containerColor)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = mood.icon),
                contentDescription = "Mood Icon",
                Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(7.dp))

            Text(
                modifier = Modifier
                    .width(150.dp),
                text = getTitle,
                color = mood.contentColor,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = SimpleDateFormat("hh:mm a", Locale.US).format(Date.from(time)),
            color = mood.contentColor,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
    }
}

@Composable
fun ShowGalleryButton(
    galleryOpened: Boolean,
    galleryLoading: Boolean,
    onClick: () -> Unit
) {

    TextButton(onClick = onClick) {

        Text(
            text = if (galleryOpened) {

                if (galleryLoading) {

                    "Loading"

                } else {
                    "Hide Gallery"
                }

            } else "Show Gallery",

            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = md_theme_dark_primaryContainer
        )
    }
}