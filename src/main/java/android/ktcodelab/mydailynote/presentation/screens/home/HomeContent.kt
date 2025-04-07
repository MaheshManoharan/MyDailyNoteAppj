package android.ktcodelab.mydailynote.presentation.screens.home

import android.annotation.SuppressLint
import android.ktcodelab.mydailynote.model.NoteModel
import android.ktcodelab.mydailynote.presentation.components.NoteHolder
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.painter.Painter
import android.ktcodelab.mydailynote.R

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    dailyNotes: Map<LocalDate, List<NoteModel>>,
    onClick: (String) -> Unit,
) {

    if (dailyNotes.isNotEmpty()) {

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(bottom = 20.dp)

        ) {

            //Sticky Header
            dailyNotes.forEach { (localDate, notes) ->

                stickyHeader(key = localDate) {

                    DateHeader(localDate = localDate)
                }

                //Note Items
                items(
                    items = notes,
                    key = { it._id.toString() }
                ) {
                    NoteHolder(note = it, onClick = onClick)
                }

            }
        }

    } else {

        EmptyPage()
    }
}


@Composable
fun DateHeader(localDate: LocalDate) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(horizontalAlignment = Alignment.End) {

            Text(
                text = String.format("%02d", localDate.dayOfMonth),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Light
                )
            )

            Text(
                text = localDate.dayOfWeek.toString()
                    .take(3), //Here 'take(3)' means First 3 Character of Day name, Like -> 'Tue'
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = FontWeight.Light
                )
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(horizontalAlignment = Alignment.Start) {

            Text(
                text = localDate.month.toString().lowercase().replaceFirstChar { it.titlecase() },
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Light
                )
            )

            Text(
                text = "${localDate.year}",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = FontWeight.Light
                )
            )
        }
    }
}

@Composable
fun EmptyPage(
    image: Painter = painterResource(id = R.drawable.ic_sad_emoji),
    title: String = "Nothing to show |  Add your notes..."
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {

        Image(
            painter = image,
            contentDescription = "No Notes or Error Image",
            modifier = Modifier.size(140.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Normal
        )
    }
}
