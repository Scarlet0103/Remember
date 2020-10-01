package scarlet.believe.remember.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlin.reflect.KClass

@Dao
interface NoteDao {

    @Insert
    fun addNote(note : Note)

    @Insert
    fun addLabel(label: Label)

    @Query("SELECT * FROM Label ORDER BY id ")
    fun getAllLabels() : LiveData<List<Label>>

    @Query("SELECT * FROM Note ORDER BY id DESC")
    fun getAllNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE isarchieved = 0 ORDER BY id DESC")
    fun getNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE isarchieved = 1 ORDER BY id DESC")
    fun getArchievedNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE label = :label AND isArchieved = 0 ORDER BY id DESC")
    fun getLabeledNotes(label : String) : LiveData<List<Note>>

    @Insert
    fun addMultipleNotes(vararg note : Note)

    @Update
    fun updateNote(note: Note)

    @Update
    fun updateLabel(label : Label)

    @Query("UPDATE Note SET label = :nLabel WHERE label = :oLabel")
    fun updateLabelNote(oLabel : String,nLabel: String)

    @Delete
    fun deleteLabel(label: Label)

    @Delete
    fun deleteNote(note: Note)

    @Query("UPDATE Note SET label = NULL WHERE label = :Label")
    fun deleteLabelNote(Label : String)

    @Query("DELETE FROM Note where isarchieved = 1")
    suspend fun deleteAllArchievedNotes()

}