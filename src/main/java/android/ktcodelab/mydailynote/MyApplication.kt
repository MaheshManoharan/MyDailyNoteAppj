package android.ktcodelab.mydailynote

import android.app.Application
import android.ktcodelab.mydailynote.pinlock.PinManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize pin manager in application
        PinManager.initialize(this)
    }
}