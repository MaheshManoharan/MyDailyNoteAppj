package android.ktcodelab.mydailynote.data.repository

import android.ktcodelab.mydailynote.model.NoteModel
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZonedDateTime

typealias Notes = RequestState<Map<LocalDate, List<NoteModel>>>

interface MongoRepository {

    fun configureTheRealm()

    fun getAllNotes() : Flow<Notes>

    fun getFilteredNotes(zonedDateTime: ZonedDateTime) : Flow<Notes>

    fun getSelectedNote(noteId: io.realm.kotlin.types.ObjectId): Flow<RequestState<NoteModel>>

    suspend fun insertNote(note: NoteModel): RequestState<NoteModel>

    suspend fun updateNote(note: NoteModel): RequestState<NoteModel>

    suspend fun deleteNote(id: ObjectId): RequestState<Boolean>

    suspend fun deleteAllNotes(): RequestState<Boolean>

}