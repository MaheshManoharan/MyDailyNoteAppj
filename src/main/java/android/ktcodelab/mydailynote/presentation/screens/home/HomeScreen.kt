package android.ktcodelab.mydailynote.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.ktcodelab.mydailynote.R
import android.ktcodelab.mydailynote.admob.AdMobAds
import android.ktcodelab.mydailynote.data.repository.Notes
import android.ktcodelab.mydailynote.data.repository.RequestState
import android.ktcodelab.mydailynote.pref.ModeViewModel
import android.ktcodelab.mydailynote.pref.UserPref
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import java.time.ZonedDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    notes: Notes,
    drawerState: DrawerState,
    onSignOutClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    onMenuClicked: () -> Unit,
    dateIsSelected: Boolean,
    onDateSelected: (ZonedDateTime) -> Unit,
    onDateReset: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    userPref: UserPref,
    modeViewModel: ModeViewModel,
    navigateToSettings: () -> Unit = {}
) {

    //STATEs
    var padding by remember {
        mutableStateOf(PaddingValues())
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior() //For Sticky Header


    NavigationDrawer(
        drawerState = drawerState,
        onSignOutClicked = onSignOutClicked,
        onDeleteAllClicked = onDeleteAllClicked,
        navigateToWrite = navigateToWrite,
        navigateToSettings = navigateToSettings,

    ) {

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {

                HomeTopBar(
                    scrollBehavior = scrollBehavior,
                    onMenuClicked = onMenuClicked,
                    dateIsSelected = dateIsSelected,
                    onDateSelected = onDateSelected,
                    onDateReset = onDateReset,
                    userPref = userPref,
                    modeViewModel = modeViewModel
                )
            },
            bottomBar = {

               AdMobAds()
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(
                            end = padding.calculateEndPadding(LayoutDirection.Ltr)
                        ),
                    onClick = navigateToWrite
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            },
            content = {

                padding = it
                when (notes) {

                    is RequestState.Success -> {

                        HomeContent(
                            paddingValues = it,
                            dailyNotes = notes.data,
                            onClick = navigateToWriteWithArgs,
                        )
                    }
                    is RequestState.Error -> {

                        EmptyPage(
                            image = painterResource(id = R.drawable.ic_error),
                            title = "${notes.error.message}"
                        )
                    }
                    is RequestState.Loading -> {

                        Box(Modifier.fillMaxSize(), Alignment.Center) {

                            CircularProgressIndicator(color = Color.Red)
                        }
                    }
                    else -> {}
                }
            }
        )
    }
}

@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    onSignOutClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToSettings: () -> Unit,
    content: @Composable () -> Unit
) {

    /*-----------------------------Navigation Drawer---------------------------*/
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(

                content = {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {

                        Box {
                            Image(
                                modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = null
                            )

                            Text(
                                modifier = Modifier.align(Alignment.BottomCenter),
                                text = stringResource(R.string.app_name),
                                fontSize = 34.sp,
                                fontFamily = FontFamily(Font(R.font.bulletto_killa))
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp)
                                .background(Color.LightGray)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.Start
                    ) {

                        Spacer(modifier = Modifier.height(16.dp))

                        NavigationDrawerItem(
                            label = {
                                Row(Modifier.padding(horizontal = 12.dp)) {

                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null,
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = stringResource(R.string.home_text),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            selected = true,
                            onClick = {}
                        )

                        NavigationDrawerItem(
                            label = {
                                Row(Modifier.padding(horizontal = 12.dp)) {

                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = stringResource(R.string.create_note_text),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            selected = false,
                            onClick = navigateToWrite
                        )

                        NavigationDrawerItem(
                            label = {
                                Row(Modifier.padding(horizontal = 12.dp)) {

                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = stringResource(R.string.settings_text),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            selected = false,
                            onClick = navigateToSettings
                        )


                        NavigationDrawerItem(
                            label = {

                                Row(Modifier.padding(horizontal = 12.dp)) {

                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete All Logo"
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = "Delete All Notes",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            selected = false,
                            onClick = onDeleteAllClicked
                        )

                        NavigationDrawerItem(
                            label = {

                                Row(Modifier.padding(horizontal = 12.dp)) {

                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = null,
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = stringResource(R.string.sign_out),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            selected = false,
                            onClick = onSignOutClicked
                        )

                    }

                }
            )
        },
        content = content
    )
}