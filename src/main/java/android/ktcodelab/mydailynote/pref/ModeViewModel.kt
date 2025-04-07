package android.ktcodelab.mydailynote.pref

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ModeViewModel: ViewModel() {

    var isDarkModeEnabled = mutableStateOf(false)
        private set

    fun setMode(isDarkMode: Boolean) {
        isDarkModeEnabled.value = isDarkMode
    }
}