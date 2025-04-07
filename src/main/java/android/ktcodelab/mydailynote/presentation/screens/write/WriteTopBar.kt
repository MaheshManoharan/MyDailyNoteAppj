package android.ktcodelab.mydailynote.presentation.screens.write

import android.ktcodelab.mydailynote.model.NoteModel
import android.ktcodelab.mydailynote.presentation.components.DisplayAlertDialog
import android.ktcodelab.mydailynote.ui.theme.DarkGray
import android.ktcodelab.mydailynote.util.toInstant
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteTopBar(

    selectedNote: NoteModel?,
    onDeleteConfirmed: () -> Unit,
    onBackPressed: () -> Unit,
    moodName: () -> String,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    backgroundColor: () -> Color

) {


    /*----------Date Time------------*/
    val dateDialog = rememberSheetState()
    val timeDialog = rememberSheetState()

    /*----------------------Note Date Time--------------- */

    //Current Date and Time when Creating New Note
    var currentDate by remember {
        mutableStateOf(LocalDate.now())
    }
    var currentTime by remember {
        mutableStateOf(LocalTime.now())
    }

    val formattedDate = remember(key1 = currentDate) {

        DateTimeFormatter.ofPattern("dd MMM yyyy").format(currentDate).uppercase()
    }

    val formattedTime = remember(key1 = currentTime) {

        DateTimeFormatter.ofPattern("hh:mm a").format(currentTime).uppercase()
    }

    var dateTimeUpdated by remember {
        mutableStateOf(false)
    }

    //Parse Date and Time from Selected Note
    val selectedNoteDateTime = remember(selectedNote) {

        if (selectedNote != null) {

            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(Date.from(selectedNote.date.toInstant())).uppercase()
        } else {
            "Unknown"
        }
    }

    /*--------------------Top Bar ----------------------*/
    CenterAlignedTopAppBar(

        navigationIcon = {

            IconButton(onClick = onBackPressed) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Arrow Icon",
                    tint = DarkGray
                )
            }
        },
        title = {

            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = moodName(),
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = DarkGray
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),

                    text = if (selectedNote != null && dateTimeUpdated) {

                        "$formattedDate, $formattedTime"

                    } else if (selectedNote != null) {
                        selectedNoteDateTime
                    }
                    else {
                        "$formattedDate, $formattedTime"
                    },
                    style = TextStyle(fontSize = MaterialTheme.typography.bodySmall.fontSize),
                    textAlign = TextAlign.Center,
                    color = DarkGray
                )
            }
        },
        actions = {

            if(dateTimeUpdated) {

                IconButton(onClick = {
                    currentDate = LocalDate.now()
                    currentTime = LocalTime.now()

                    dateTimeUpdated = false

                    onDateTimeUpdated(
                        ZonedDateTime.of(
                            currentDate,
                            currentTime,
                            ZoneId.systemDefault()
                        )
                    )
                }) {

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        tint = DarkGray
                    )
                }

            } else {

                IconButton(onClick = {dateDialog.show()}) {

                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date Icon",
                        tint = DarkGray
                    )
                }
            }

            if (selectedNote != null) {

                DeleteNoteAction(
                    selectedNote = selectedNote,
                    onDeleteConfirmed = onDeleteConfirmed
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(backgroundColor.invoke())
    )

    CalendarDialog(
        state = dateDialog,
        selection = CalendarSelection.Date {localDate ->

            currentDate = localDate

            timeDialog.show()
        },
        config = CalendarConfig(monthSelection = true, yearSelection = true)
    )

    ClockDialog(
        state = timeDialog,
        selection = ClockSelection.HoursMinutes { hours, minutes ->

            currentTime = LocalTime.of(hours, minutes)

            dateTimeUpdated = true

            onDateTimeUpdated (

                ZonedDateTime.of(

                    currentDate,
                    currentTime,
                    ZoneId.systemDefault()
                )
            )
        }
    )

}

@Composable
fun DeleteNoteAction(

    selectedNote: NoteModel?,
    onDeleteConfirmed: () -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }
    var openDialog by remember {
        mutableStateOf(false)
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        }
    ) {
        DropdownMenuItem(
            text = {
                Text(text = "Delete")
            },
            onClick = {
                openDialog = true
                expanded = false
            })
    }
    
    DisplayAlertDialog(
        title = "Delete",
        message = "Are you sure want to permanently delete this note '${selectedNote?.title}'?",
        dialogOpened = openDialog,
        onCloseDialog = { openDialog = false },
        onYesClicked = onDeleteConfirmed
    )
    IconButton(onClick = {expanded = !expanded}) {

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Overflow Menu Icon",
            tint = DarkGray
        )
    }
}