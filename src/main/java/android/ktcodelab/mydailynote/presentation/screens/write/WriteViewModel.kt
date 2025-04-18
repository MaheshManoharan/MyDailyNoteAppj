package android.ktcodelab.mydailynote.presentation.screens.write

import android.ktcodelab.mydailynote.data.database.ImageToDeleteDao
import android.ktcodelab.mydailynote.data.database.ImageToUploadDao
import android.ktcodelab.mydailynote.data.database.entity.ImageToDelete
import android.ktcodelab.mydailynote.data.database.entity.ImageToUpload
import android.ktcodelab.mydailynote.data.repository.GalleryImage
import android.ktcodelab.mydailynote.data.repository.GalleryState
import android.ktcodelab.mydailynote.data.repository.MongoDB
import android.ktcodelab.mydailynote.model.MoodModel
import android.ktcodelab.mydailynote.model.NoteModel
import android.ktcodelab.mydailynote.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import android.ktcodelab.mydailynote.data.repository.RequestState
import android.ktcodelab.mydailynote.util.fetchImagesFromFirebase
import android.ktcodelab.mydailynote.util.toRealmInstant
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(

    private val savedStateHandle: SavedStateHandle,

    private val imageToUploadDao: ImageToUploadDao,

    private val imageToDeleteDao: ImageToDeleteDao

) : ViewModel() {

    val galleryState = GalleryState()

    var uiState by mutableStateOf(UiState())
        private set

    init {
        getNoteIdArgument()

        fetchSelectedNote()
    }

    private fun getNoteIdArgument() {

        uiState = uiState.copy(

            selectedNoteId = savedStateHandle.get<String>(

                key = WRITE_SCREEN_ARGUMENT_KEY
            )
        )
    }

    private fun fetchSelectedNote() {

        if (uiState.selectedNoteId != null) {

            viewModelScope.launch(Dispatchers.Main) {

                MongoDB.getSelectedNote(noteId = io.realm.kotlin.types.ObjectId.Companion.from(uiState.selectedNoteId!!))
                    .catch {
                        emit(RequestState.Error(Exception("Note is already deleted.")))
                    }
                    .collect { note ->
                        if (note is RequestState.Success) {


                            setSelectedNote(note = note.data)

                            setTitle(title = note.data.title)

                            setDescription(description = note.data.description)

                            setMood(mood = MoodModel.valueOf(note.data.mood))


                            fetchImagesFromFirebase(

                                remoteImagePaths = note.data.images,
                                onImageDownload = {downloadedImage ->

                                    galleryState.addImage(

                                        GalleryImage(

                                            image = downloadedImage,
                                            remoteImagePath = extractImagePath(

                                                fullImageUrl = downloadedImage.toString()
                                            )
                                        )
                                    )
                                }
                            )
                        }
                    }
            }
        }

    }

    private fun setSelectedNote(note: NoteModel) {

        uiState = uiState.copy(selectedNote = note)
    }

    fun setTitle(title: String) {

        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {

        uiState = uiState.copy(description = description)
    }

    private fun setMood(mood: MoodModel) {

        uiState = uiState.copy(mood = mood)
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime) {

        uiState = uiState.copy(updateDateTime = zonedDateTime.toInstant().toRealmInstant())

    }

    /*-------------------------------UPDATE & INSERT LOGIC--------------*/

    fun upsertNote(
        note: NoteModel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {

            if (uiState.selectedNoteId != null) {

                updateNote(note = note, onSuccess = onSuccess, onError = onError)
            } else {
                insertNote(note = note, onSuccess = onSuccess, onError = onError)
            }


        }

    }
    /*-------------------------------Insert Note------------------------*/

    private suspend fun insertNote(
        note: NoteModel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.insertNote(note = note.apply {

            if (uiState.updateDateTime != null) {
                date = uiState.updateDateTime!!
            }
        })

        if (result is RequestState.Success) {

            uploadImagesToFirebase()

            withContext(Dispatchers.Main) {

                onSuccess()
            }
        } else if (result is RequestState.Error) {

            withContext(Dispatchers.Main) {

                onError(result.error.message.toString())
            }
        }
    }
 /*------------------------------Update Note---------------------*/

    private suspend fun updateNote(

        note: NoteModel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.updateNote(note = note.apply {
            _id = ObjectId.Companion.from(uiState.selectedNoteId!!)
            date = if (uiState.updateDateTime != null) {

                uiState.updateDateTime!!
            } else {
                uiState.selectedNote!!.date
            }
        })

        if (result is RequestState.Success) {

            uploadImagesToFirebase()

            deleteImagesFromFirebase()

            withContext(Dispatchers.Main) {

                onSuccess()
            }
        } else if (result is RequestState.Error){

            withContext(Dispatchers.Main) {

                onError(result.error.message.toString())
            }
        }
    }

    /*------------------------------Delete Note---------------------*/

    fun deleteNote(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch (Dispatchers.IO){

            if (uiState.selectedNoteId != null) {

                val result = MongoDB.deleteNote(id = ObjectId.Companion.from(uiState.selectedNoteId!!))

                if (result is RequestState.Success) {

                    withContext(Dispatchers.Main) {

                        uiState.selectedNote?.let { deleteImagesFromFirebase(images = it.images) }
                        onSuccess()
                    }

                } else if (result is RequestState.Error) {

                    withContext(Dispatchers.Main) {onError(result.error.message.toString())}
                }
            }
        }
    }

    fun addImage(image: Uri, imageType: String) {

        val remoteImagePath = "images/${FirebaseAuth.getInstance().currentUser?.uid}/" +
                "${image.lastPathSegment}-${System.currentTimeMillis()}.$imageType"

        galleryState.addImage(

            GalleryImage(

                image = image,

                remoteImagePath = remoteImagePath
            )
        )
    }

    private fun uploadImagesToFirebase() {

        val storage = FirebaseStorage.getInstance().reference

        galleryState.images.forEach { galleryImage ->

            val imagePath = storage.child(galleryImage.remoteImagePath)

            imagePath.putFile(galleryImage.image)
                .addOnProgressListener {

                    val sessionUri = it.uploadSessionUri

                    if(sessionUri != null) {

                        viewModelScope.launch(Dispatchers.IO) {

                            imageToUploadDao.addImageToUpload(

                                ImageToUpload(
                                    remoteImagePath = galleryImage.remoteImagePath,
                                    imageUri = galleryImage.image.toString(),
                                    sessionUri = sessionUri.toString()
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun deleteImagesFromFirebase(images: List<String>? = null) {

        val storage = FirebaseStorage.getInstance().reference

        if (images != null) {

            images.forEach{ remotePath ->

                storage.child(remotePath).delete()
                    .addOnFailureListener {

                        viewModelScope.launch(Dispatchers.IO) {

                            imageToDeleteDao.addImageToDelete(

                                ImageToDelete(remoteImagePath = remotePath)
                            )
                        }
                    }
            }
        } else {
            galleryState.imagesToBeDeleted.map { it.remoteImagePath }.forEach { remotePath ->

                storage.child(remotePath).delete()
                    .addOnFailureListener {

                        viewModelScope.launch(Dispatchers.IO) {

                            imageToDeleteDao.addImageToDelete(

                                ImageToDelete(remoteImagePath = remotePath)
                            )
                        }
                    }
            }
        }

    }

    private fun extractImagePath(fullImageUrl: String): String {

        val chunks = fullImageUrl.split("%2F")
        val imageName = chunks[2].split("?").first()

        return "images/${Firebase.auth.currentUser?.uid}/$imageName"
    }

}

data class UiState(

    val selectedNoteId: String? = null,
    val selectedNote: NoteModel? = null,
    val title: String = "",
    val description: String = "",
    val mood: MoodModel = MoodModel.Neutral,
    val updateDateTime: RealmInstant? = null,
)