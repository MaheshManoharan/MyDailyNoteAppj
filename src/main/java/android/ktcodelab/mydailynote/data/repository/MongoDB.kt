package android.ktcodelab.mydailynote.data.repository

import android.ktcodelab.mydailynote.model.NoteModel
import android.ktcodelab.mydailynote.util.Constants.APP_ID
import android.ktcodelab.mydailynote.util.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object MongoDB : MongoRepository {

    private lateinit var realm: Realm

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {

        val user = App.create(APP_ID).currentUser

        if (user != null) {

            val config = SyncConfiguration.Builder(user, setOf(NoteModel::class))
                .initialSubscriptions { sub ->

                    add(
                        query = sub.query<NoteModel>("owner_id == $0", user.id),
                        name = "User's Notes"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    /*---------------------Get All Notes--------------------------------*/
    override fun getAllNotes(): Flow<Notes> {

        val user = App.create(APP_ID).currentUser

        return if (user != null) {

            try {
                realm.query<NoteModel>(query = "owner_id == $0", user.id)
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->

                        RequestState.Success(

                            data = result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }

                        )
                    }
            } catch (e: Exception) {

                flow { emit(RequestState.Error(e)) }
            }
        } else {

            flow { emit(RequestState.Error(UserNoteAuthenticatedException())) }
        }
    }


    /*---------------------Filter Notes--------------------------------*/

    override fun getFilteredNotes(zonedDateTime: ZonedDateTime): Flow<Notes> {

        val user = App.create(APP_ID).currentUser

        return if (user != null) {

            try {

                realm.query<NoteModel>(

                    "owner_id == $0 AND date < $1 AND date > $2",
                    user.id,
                    RealmInstant.from(

                        LocalDateTime.of(

                            zonedDateTime.toLocalDate().plusDays(1),
                            LocalTime.MIDNIGHT
                        ).toEpochSecond(zonedDateTime.offset), 0
                    ),

                    RealmInstant.from(

                        LocalDateTime.of(

                            zonedDateTime.toLocalDate(),
                            LocalTime.MIDNIGHT
                        ).toEpochSecond(zonedDateTime.offset), 0
                    ),

                ).asFlow().map { result ->

                    RequestState.Success(

                        data = result.list.groupBy {

                            it.date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                    )
                }

            } catch (e: Exception) {

                flow { emit(RequestState.Error(e)) }
            }

        } else {

            flow { emit(RequestState.Error(UserNoteAuthenticatedException())) }
        }
    }


    /*---------------------Fetch Note From MongoDB--------------------------------*/
    override fun getSelectedNote(noteId: io.realm.kotlin.types.ObjectId): Flow<RequestState<NoteModel>> {

        val user = App.create(APP_ID).currentUser

        return if (user != null) {

            try {
                realm.query<NoteModel>(query = "_id == $0", noteId).asFlow().map {

                    RequestState.Success(data = it.list.first())

                }

            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }

        } else {

            flow { emit(RequestState.Error(UserNoteAuthenticatedException())) }
        }

    }

    /*---------------------Add New Note--------------------------------*/
    override suspend fun insertNote(note: NoteModel): RequestState<NoteModel> {

        val user = App.create(APP_ID).currentUser

        return if (user != null) {

            realm.write {

                try {

                    val addedNote = copyToRealm(note.apply { owner_id = user.id })

                    RequestState.Success(data = addedNote)

                } catch (e: Exception) {
                    RequestState.Error(e)
                }

            }

        } else {

            RequestState.Error(UserNoteAuthenticatedException())
        }

    }

    /*------------------------UPDATE Note---------------------*/

    override suspend fun updateNote(note: NoteModel): RequestState<NoteModel> {

        val user = App.create(APP_ID).currentUser

        return if (user != null) {

            realm.write {

                val queriedNote = query<NoteModel>(query = "_id == $0", note._id).first().find()

                if (queriedNote != null) {
                    queriedNote.title = note.title
                    queriedNote.description = note.description
                    queriedNote.mood = note.mood
                    queriedNote.images = note.images
                    queriedNote.date = note.date

                    RequestState.Success(data = queriedNote)
                } else {

                    RequestState.Error(error = Exception("Note does not exist."))
                }

            }

        } else {

            RequestState.Error(UserNoteAuthenticatedException())
        }

    }

    /*--------------------------Delete Single Note-------------------------*/

    override suspend fun deleteNote(id: ObjectId): RequestState<Boolean> {

        val user = App.create(APP_ID).currentUser

        return if (user != null) {

            realm.write {

                val note = query<NoteModel>(query = "_id == $0 AND owner_id == $1", id, user.id)
                    .first().find()

                if (note != null) {

                    try {
                        delete(note)
                        RequestState.Success(data = true)

                    } catch (e: Exception) {

                        RequestState.Error(e)
                    }

                } else {
                    RequestState.Error(Exception("Note does not exist."))
                }

            }

        } else {

            RequestState.Error(UserNoteAuthenticatedException())
        }

    }

    /*--------------------------Delete All Notes-------------------------*/

    override suspend fun deleteAllNotes(): RequestState<Boolean> {

        val user = App.create(APP_ID).currentUser

        return if (user != null) {

            realm.write {

                val notes = this.query<NoteModel>("owner_id == $0", user.id).find()

                try {

                    delete(notes)

                    RequestState.Success(data = true)

                } catch (e: Exception) {

                    RequestState.Error(e)
                }

            }

        } else {

            RequestState.Error(UserNoteAuthenticatedException())
        }
    }

}

private class UserNoteAuthenticatedException : Exception("User is not Logged in.")