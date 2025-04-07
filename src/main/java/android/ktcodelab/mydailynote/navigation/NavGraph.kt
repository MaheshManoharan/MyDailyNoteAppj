package android.ktcodelab.mydailynote.navigation

import android.ktcodelab.mydailynote.R
import android.ktcodelab.mydailynote.connectivity.ConnectivityObserver
import android.ktcodelab.mydailynote.data.repository.MongoDB
import android.ktcodelab.mydailynote.data.repository.RequestState
import android.ktcodelab.mydailynote.model.MoodModel
import android.ktcodelab.mydailynote.pinlock.ChangePinLockScreen
import android.ktcodelab.mydailynote.pinlock.PinLockScreen
import android.ktcodelab.mydailynote.pref.ModeViewModel
import android.ktcodelab.mydailynote.pref.UserPref
import android.ktcodelab.mydailynote.presentation.components.DisplayAlertDialog
import android.ktcodelab.mydailynote.presentation.screens.auth.AuthenticationScreen
import android.ktcodelab.mydailynote.presentation.screens.auth.AuthenticationViewModel
import android.ktcodelab.mydailynote.presentation.screens.home.HomeScreen
import android.ktcodelab.mydailynote.presentation.screens.home.HomeViewModel
import android.ktcodelab.mydailynote.presentation.screens.settings.SettingsScreen
import android.ktcodelab.mydailynote.presentation.screens.write.WriteScreen
import android.ktcodelab.mydailynote.presentation.screens.write.WriteViewModel
import android.ktcodelab.mydailynote.util.Constants.APP_ID
import android.ktcodelab.mydailynote.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController,
    userPref: UserPref,
    modeViewModel: ModeViewModel,
) {

    NavHost(
        startDestination = startDestination,
        navController = navController
    ) {
        pinScreenRoute(
            navController = navController,
            navigateToDestination = {
                navController.popBackStack()

                val user = App.create(APP_ID).currentUser

                if (user != null && user.loggedIn)
                    navController.navigate(Screen.Home.route)
                else
                    navController.navigate(Screen.Authentication.route)
            }
        )

        changePinScreenRoute(
            navigateBack = {
                navController.popBackStack()
            }
        )

        authenticationRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            }
        )

        homeRoute(
            navigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            navigateToWriteWithArgs = {
                navController.navigate(route = Screen.Write.passNoteId(noteId = it))
            },
            navigateToAuth = {

                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            userPref = userPref,
            modeViewModel = modeViewModel,
            navigateToSettings = {
                navController.navigate(Screen.SettingsScreen.route)
            }
        )

        writeRoute(
            onBackPressed = { navController.popBackStack() }
        )

        settingsRoute(
            navigateToCreatePin = {navController.navigate(Screen.PinScreen.route)},
            navigateToChangePin = {navController.navigate(Screen.ChangePinScreen.route)},
            onBackPressed = { navController.popBackStack() }
        )
    }
}

//---------------------------------------- Pin Screen ----------------------------------------

fun NavGraphBuilder.pinScreenRoute(
    navController: NavController,
    navigateToDestination: () -> Unit
) {


    composable(route = Screen.PinScreen.route){

        val context = LocalContext.current

        PinLockScreen(
            title = { pinExists ->
                Text(
                    text = if (pinExists) "Enter your pin" else "Create pin",
                    color = Color.White,
                    fontSize = 22.sp
                )
            },
            color = MaterialTheme.colorScheme.primary,
            onPinCorrect = {
                // pin is correct, navigate to Next Screen
                navigateToDestination()
            },
            onPinIncorrect = {
                // pin is incorrect, show error
            },
            onPinCreated = {
                // pin created for the first time, navigate or hide pin lock
                navController.popBackStack()
                Toast.makeText(context, "Pin Successfully Setup.", Toast.LENGTH_SHORT).show()
            },

        )
    }
}

//---------------------------------------- Change Pin Screen ----------------------------------------

fun NavGraphBuilder.changePinScreenRoute(
    navigateBack: () -> Unit = {}
) {

    composable(route = Screen.ChangePinScreen.route){

        val context =  LocalContext.current

        ChangePinLockScreen(
            title = { authenticated ->
                Text(
                    text = if (authenticated) "Enter new pin" else "Enter your pin",
                    color = Color.White,
                    fontSize = 22.sp
                )
            },
            color = MaterialTheme.colorScheme.primary,
            onPinIncorrect = { Toast.makeText(context, "Incorrect Pin", Toast.LENGTH_SHORT).show() },
            onPinChanged = {
                navigateBack()
                Toast.makeText(context, "Pin Successfully Changed", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

//---------------------------------------- Authentication Screen ----------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit,
) {

    composable(route = Screen.Authentication.route) {

        val viewModel: AuthenticationViewModel = viewModel()
        val authenticated by viewModel.authenticated
        val loadingState by viewModel.loadingState
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        AuthenticationScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onSuccessfulFirebaseSignIn = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully logged in!")
                        viewModel.setLoading(false)
                    },
                    onError = {
                        messageBarState.addError(it)
                        viewModel.setLoading(false)
                    }
                )
            },
            onFailedFirebaseSignIn = {
                messageBarState.addError(it)
                viewModel.setLoading(false)
            },

            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToHome = navigateToHome,
        )

    }
}

//---------------------------------------- Home Screen ----------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeRoute(

    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    navigateToAuth: () -> Unit,
    userPref: UserPref,
    modeViewModel: ModeViewModel,
    navigateToSettings: () -> Unit
) {

    composable(route = Screen.Home.route) {

        val viewModel: HomeViewModel = hiltViewModel()
        val notes by viewModel.notes
        val network = viewModel.network

        var isSuccess by remember {
            mutableStateOf(true)
        }

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        var signOutDialogOpened by remember {
            mutableStateOf(false)
        }

        var deleteAllDialogOpened by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = notes) {

            if (notes !is RequestState.Loading) {

                if (isSuccess){
                    viewModel.getNotes()
                    isSuccess = false
                }
            }
        }


        HomeScreen(
            notes = notes,
            drawerState = drawerState,
            onMenuClicked = {

                scope.launch {
                    drawerState.open()
                }
            },
            onSignOutClicked = {
                signOutDialogOpened = true
            },
            onDeleteAllClicked = {

                deleteAllDialogOpened = true
            },
            dateIsSelected = viewModel.dateIsSelected,
            onDateSelected = {viewModel.getNotes(zonedDateTime = it)},
            onDateReset = {viewModel.getNotes()},
            navigateToWrite = {
                scope.launch { drawerState.close() }
                navigateToWrite()
            },
            navigateToWriteWithArgs = navigateToWriteWithArgs,
            userPref = userPref,
            modeViewModel = modeViewModel,
            navigateToSettings = {
                scope.launch { drawerState.close() }
                navigateToSettings()

            }
        )


        /*------------------MongoDB Sync----------------------*/

        LaunchedEffect(key1 = Unit) {
            MongoDB.configureTheRealm()
        }

        DisplayAlertDialog(
            title = context.getString(R.string.sign_out),
            message = context.getString(R.string.sign_out_alert_dialog_message),
            dialogOpened = signOutDialogOpened,
            onCloseDialog = { signOutDialogOpened = false },
            onYesClicked = {

                if (network == ConnectivityObserver.Status.Available){

                    scope.launch(Dispatchers.IO) {

                        val user = App.create(APP_ID).currentUser

                        if (user != null) {
                            withContext(Dispatchers.Main) {

                                navigateToAuth()
                            }
                            user.logOut()
                        }
                    }
                } else {
                    navigateToAuth()
                }

            }
        )

        DisplayAlertDialog(
            title = context.getString(R.string.delete_all_alert_dialog_title),
            message = context.getString(R.string.delete_all_alert_dialog_message),
            dialogOpened = deleteAllDialogOpened,
            onCloseDialog = { deleteAllDialogOpened = false },
            onYesClicked = {

                viewModel.deleteAllNotes(

                    onSuccess = {
                        Toast.makeText(context, context.getString(R.string.all_notes_deleted_toast_message), Toast.LENGTH_SHORT).show()

                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onError = {

                        Toast.makeText(
                            context,
                            if (it.message == "No Internet Connection!") "We need an internet connection for this operation."
                            else it.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        )
    }

}

//---------------------------------------- Write Screen ----------------------------------------
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
fun NavGraphBuilder.writeRoute(
    onBackPressed: () -> Unit
) {

    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARGUMENT_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {

        val viewModel: WriteViewModel = hiltViewModel()
        val context = LocalContext.current
        val uiState = viewModel.uiState
        val pagerState = rememberPagerState()
        val galleryState = viewModel.galleryState
        val pageNumber by remember {
            derivedStateOf { pagerState.currentPage }
        }

        WriteScreen(
            uiState = uiState,
            moodName = { MoodModel.values()[pageNumber].name },
            backgroundColor = {MoodModel.values()[pageNumber].backgroundColor},
            pagerState = pagerState,
            galleryState = galleryState,
            onTitleChanged = { viewModel.setTitle(title = it) },
            onDescriptionChanged = { viewModel.setDescription(description = it) },
            onDeleteConfirmed = {
                viewModel.deleteNote(
                    onSuccess = {
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    },
                    onError = { message ->

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onDateTimeUpdated = { viewModel.updateDateTime(zonedDateTime = it) },
            onBackPressed = onBackPressed,
            onSavedClicked = {

                viewModel.upsertNote(note = it.apply { mood = MoodModel.values()[pageNumber].name },
                    onSuccess = { onBackPressed() },
                    onError = { message ->

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onImageSelect = {

                //Image Type
                val type = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"

                viewModel.addImage(image = it, imageType = type)
            },
        ) {
            galleryState.removeImage(it)
        }
    }
}

//---------------------------------------- Settings Screen ----------------------------------------
fun NavGraphBuilder.settingsRoute(
    navigateToCreatePin: () -> Unit,
    navigateToChangePin: () -> Unit,
    onBackPressed: () -> Unit
){
    composable(route = Screen.SettingsScreen.route){

        SettingsScreen(
            navigateToCreatePin = navigateToCreatePin,
            navigateToChangePin = navigateToChangePin,
            navigateBack = onBackPressed
        )
    }
}