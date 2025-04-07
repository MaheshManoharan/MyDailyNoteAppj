package android.ktcodelab.mydailynote.data.database.entity

import android.ktcodelab.mydailynote.util.Constants.IMAGE_TO_DELETE_TABLE
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = IMAGE_TO_DELETE_TABLE)
data class ImageToDelete(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val remoteImagePath: String
)
