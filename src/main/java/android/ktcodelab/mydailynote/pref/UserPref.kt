package android.ktcodelab.mydailynote.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPref(private val context: Context) {

    companion object {

        private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("modeSettings")

        val MODE_KEY = booleanPreferencesKey("mode")
    }

    fun getMode(isSystemDarkMode: Boolean): Flow<Boolean> = context.dataStore.data.map { pref ->

        pref[MODE_KEY] ?: isSystemDarkMode
    }

    suspend fun saveMode(isDarkModeEnabled: Boolean) {

        context.dataStore.edit { pref ->

            pref[MODE_KEY] = isDarkModeEnabled
        }
    }

}