package android.ktcodelab.mydailynote.presentation.screens.home

import android.ktcodelab.mydailynote.connectivity.ConnectivityObserver
import android.ktcodelab.mydailynote.connectivity.NetworkConnectivityObserver
import android.ktcodelab.mydailynote.data.database.ImageToDeleteDao
import android.ktcodelab.mydailynote.data.database.entity.ImageToDelete
import android.ktcodelab.mydailynote.data.repository.MongoDB
import android.ktcodelab.mydailynote.data.repository.Notes
import android.ktcodelab.mydailynote.data.repository.RequestState
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

    private val connectivity: NetworkConnectivityObserver,

    private val imageToDeleteDao: ImageToDeleteDao


) : ViewModel() {

    private lateinit var allNotesJob: Job
    private lateinit var filteredNotesJob: Job

    var network by mutableStateOf(ConnectivityObserver.Status.Unavailable)

    var notes: MutableState<Notes> = mutableStateOf(RequestState.Idle)

    var dateIsSelected by mutableStateOf(false)
        private set

    init {
        getNotes()
        viewModelScope.launch {
            connectivity.observe().collect { network = it }
        }
    }

    //For Filter Notes
    fun getNotes(zonedDateTime: ZonedDateTime? = null) {

        dateIsSelected = zonedDateTime != null

        notes.value = RequestState.Loading

        if (dateIsSelected && zonedDateTime != null) {

            observeFilteredNotes(zonedDateTime = zonedDateTime)

        } else {
            observeAllNotes()
        }
    }

    private fun observeAllNotes() {

        allNotesJob = viewModelScope.launch {

            if (::filteredNotesJob.isInitialized) {

                filteredNotesJob.cancelAndJoin()
            }

            MongoDB.getAllNotes().collect { result ->

                notes.value = result
            }
        }
    }

    //Filter Notes
    private fun observeFilteredNotes(zonedDateTime: ZonedDateTime) {

       filteredNotesJob = viewModelScope.launch {

           if (::allNotesJob.isInitialized) {

               allNotesJob.cancelAndJoin()
           }

            MongoDB.getFilteredNotes(zonedDateTime = zonedDateTime).collect{ result ->

                notes.value = result
            }
        }
    }

    fun deleteAllNotes(

        onSuccess: () -> Unit,

        onError: (Throwable) -> Unit
    ) {

        //At first Check Network connection

        if (network == ConnectivityObserver.Status.Available) {


            /*------------------Delete All Images From Firebase---------------*/

            val userId = FirebaseAuth.getInstance().currentUser?.uid

            val imagesDirectory = "images/${userId}"

            val storage = FirebaseStorage.getInstance().reference

            storage.child(imagesDirectory)
                .listAll()
                .addOnSuccessListener {

                    it.items.forEach { ref ->

                        val imagePath = "images/${userId}/${ref.name}"

                        storage.child(imagePath).delete()
                            .addOnFailureListener {

                                viewModelScope.launch(Dispatchers.IO) {

                                    imageToDeleteDao.addImageToDelete(

                                        ImageToDelete(

                                            remoteImagePath = imagePath
                                        )
                                    )
                                }
                            }
                    }

                    /*------------------Also Delete From MongoDB---------------*/

                    viewModelScope.launch(Dispatchers.IO) {

                        val result = MongoDB.deleteAllNotes()

                        if (result is RequestState.Success) {

                            withContext(Dispatchers.Main) {

                                onSuccess()
                            }

                        } else if (result is RequestState.Error) {

                            withContext(Dispatchers.Main) {

                                onError(result.error)
                            }
                        }
                    }
                }
                .addOnFailureListener { onError(it) }
        } else {

            onError(Exception("No Internet Connection!"))
        }
    }
}