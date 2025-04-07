package android.ktcodelab.mydailynote.presentation.screens.write

import android.ktcodelab.mydailynote.R
import android.ktcodelab.mydailynote.admob.loadInterstitial
import android.ktcodelab.mydailynote.admob.showInterstitial
import android.ktcodelab.mydailynote.data.repository.GalleryImage
import android.ktcodelab.mydailynote.data.repository.GalleryState
import android.ktcodelab.mydailynote.model.MoodModel
import android.ktcodelab.mydailynote.model.NoteModel
import android.ktcodelab.mydailynote.presentation.components.GalleryUploader
import android.ktcodelab.mydailynote.ui.theme.DarkGray
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.android.gms.ads.MobileAds
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WriteContent(
    pagerState: PagerState,
    paddingValues: PaddingValues,
    uiState: UiState,
    title: String,
    onTitleChanged: (String) -> Unit,
    description: String,
    onDescriptionChanged: (String) -> Unit,
    onSavedClicked: (NoteModel) -> Unit,
    galleryState: GalleryState,
    onImageSelect: (Uri) -> Unit,
    onImageClicked: (GalleryImage) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: () -> Color,
) {

    val scrollState = rememberScrollState()

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    //------------------Admob Ads---------------------
    LaunchedEffect(key1 = Unit) {
        MobileAds.initialize(context)
        loadInterstitial(context = context)
    }

    //Scroll State
    LaunchedEffect(key1 = scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(

        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor.invoke())
            .imePadding()
            .navigationBarsPadding()
            .padding(top = paddingValues.calculateTopPadding())
            .padding(bottom = 24.dp)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(

            modifier = Modifier
                .weight(1f)
                .verticalScroll(state = scrollState)
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            HorizontalPager(
                state = pagerState,
                count = MoodModel.values().size
            ) { page ->

                AsyncImage(
                    modifier = Modifier.size(120.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(MoodModel.values()[page].icon)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Mood Image"
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = onTitleChanged,
                textStyle = TextStyle(color = DarkGray, fontSize = 18.sp, FontWeight.Bold),
                placeholder = { Text(text = context.getString(R.string.textfield_title_placeholder)) },
                colors = TextFieldDefaults.textFieldColors(

                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    unfocusedPlaceholderColor = Color.DarkGray.copy(alpha = 0.38f)
                ),
                keyboardOptions = KeyboardOptions(

                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        scope.launch {
                            scrollState.animateScrollTo(Int.MAX_VALUE)
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    }
                ),
                maxLines = 1,
                singleLine = true
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = onDescriptionChanged,
                textStyle = TextStyle(color = DarkGray, fontSize = 16.sp, lineHeight = 20.sp),
                placeholder = {
                    Text(text = context.getString(R.string.textfield_description_placeholder))
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    unfocusedPlaceholderColor = Color.DarkGray.copy(alpha = 0.38f)
                ),
                keyboardOptions = KeyboardOptions(

                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {

                        focusManager.clearFocus()
                    }
                )
            )
        }

        Column(
            verticalArrangement = Arrangement.Bottom
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            GalleryUploader(
                galleryState = galleryState,
                onAddClicked = { focusManager.clearFocus() },
                onImageSelect = onImageSelect,
                onImageClicked = onImageClicked
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                onClick = {

                    if (uiState.title.isNotEmpty() && uiState.description.isNotEmpty()) {

                        onSavedClicked(

                            NoteModel().apply {

                                this.title = uiState.title
                                this.description = uiState.description
                                this.images = galleryState.images.map { it.remoteImagePath }.toRealmList()
                            }
                        )
                        //------------------------Showing Interstitial ads------------------------------
                        showInterstitial(context)
                        //------------------------Interstitial ads------------------------------

                    } else {

                        Toast.makeText(context, "Fields cannot be empty.", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                shape = Shapes().small
            ) {

                Text(text = "Save")
            }
        }
    }
}