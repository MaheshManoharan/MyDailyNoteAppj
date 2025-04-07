package android.ktcodelab.mydailynote.navigation

import android.ktcodelab.mydailynote.util.Constants.WRITE_SCREEN_ARGUMENT_KEY

sealed class Screen(val route: String) {

    object PinScreen: Screen(route = "pin_screen")

    object ChangePinScreen: Screen(route = "change_pin_screen")

    object SettingsScreen: Screen(route = "settings_screen")

    object Authentication: Screen(route = "authentication_screen")

    object Home: Screen(route = "home_screen")

    //1st 'WRITE_SCREEN_ARGUMENT_KEY' is a 'Key' & 2nd is 'Value' --- Here '?' mark means this arguments are Optional
    object Write: Screen(route = "write_screen?$WRITE_SCREEN_ARGUMENT_KEY={$WRITE_SCREEN_ARGUMENT_KEY}") {

        fun passNoteId(noteId: String) = "write_screen?$WRITE_SCREEN_ARGUMENT_KEY=$noteId"
    }
}
