package android.ktcodelab.mydailynote.util


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.ktcodelab.mydailynote.data.database.entity.ImageToDelete
import android.ktcodelab.mydailynote.data.database.entity.ImageToUpload
import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import io.realm.kotlin.types.RealmInstant
import java.time.Instant


//Convert RealmInstant to Normal Instant

fun RealmInstant.toInstant(): Instant {

    val sec: Long = epochSeconds

    val nano: Int = nanosecondsOfSecond

    return if (sec >= 0) {

        Instant.ofEpochSecond(sec, nano.toLong())
    } else {
        Instant.ofEpochSecond(sec - 1, 1_000_000 + nano.toLong())
    }
}

fun Instant.toRealmInstant(): RealmInstant {
    val sec: Long = this.epochSecond
    val nano: Int = this.nano

    return if (sec >= 0) {

        RealmInstant.from(sec, nano)

    } else {

        RealmInstant.from(sec + 1, -1_000_000 + nano)
    }
}


//GET IMAGE URL FROM FIREBASE

fun fetchImagesFromFirebase(
    remoteImagePaths: List<String>,
    onImageDownload: (Uri) -> Unit,
    onImageDownloadFailed: (Exception) -> Unit = {},
    onReadyToDisplay: () -> Unit = {}
) {

    if (remoteImagePaths.isNotEmpty()) {

        remoteImagePaths.forEachIndexed { index, remoteImagePath ->

            if (remoteImagePath.trim().isNotEmpty()) {

                FirebaseStorage.getInstance().reference.child(remoteImagePath.trim()).downloadUrl
                    .addOnSuccessListener{

                        onImageDownload(it)

                        if (remoteImagePaths.lastIndexOf(remoteImagePaths.last()) == index) {

                            onReadyToDisplay()
                        }
                    }.addOnFailureListener{
                        onImageDownloadFailed(it)
                    }
            }
        }
    }
}

fun retryUploadingImageToFirebase(

    imageToUpload: ImageToUpload,

    onSuccess: () -> Unit
) {

    val storage = FirebaseStorage.getInstance().reference

    storage.child(imageToUpload.remoteImagePath).putFile(

        imageToUpload.imageUri.toUri(),
        storageMetadata {  },
        imageToUpload.sessionUri.toUri()
    ).addOnSuccessListener { onSuccess() }
}

fun retryDeletingImageFromFirebase(

    imageToDelete: ImageToDelete,

    onSuccess: () -> Unit
) {

    val storage = FirebaseStorage.getInstance().reference

    storage.child(imageToDelete.remoteImagePath).delete()
        .addOnSuccessListener { onSuccess() }
}

// find the current activity from a composable
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}