package android.ktcodelab.mydailynote.model

import android.ktcodelab.mydailynote.util.toRealmInstant
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.Instant

open class NoteModel: RealmObject {

    @PrimaryKey
    var _id: ObjectId = io.realm.kotlin.types.ObjectId.create()
    var owner_id: String = ""
    var mood: String = MoodModel.Neutral.name
    var title: String = ""
    var description: String = ""
    var images: RealmList<String> = realmListOf()
    var date: RealmInstant = Instant.now().toRealmInstant()
}