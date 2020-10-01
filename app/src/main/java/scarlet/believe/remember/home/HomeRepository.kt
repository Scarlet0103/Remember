package scarlet.believe.remember.home

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.*
import scarlet.believe.remember.db.Label
import scarlet.believe.remember.db.Note
import scarlet.believe.remember.db.NoteDao
import scarlet.believe.remember.db.NoteDatabase
import java.io.File
import java.util.*
import kotlin.coroutines.CoroutineContext

class HomeRepository(application : Application) : CoroutineScope {

    private var noteDao : NoteDao?

    init {
        val db = NoteDatabase.invoke(application)
        noteDao = db.getNoteDao()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    fun getAllNotes() = noteDao?.getAllNotes()
    //private suspend fun getAllNotesBG() = withContext(Dispatchers.IO){noteDao?.getAllNotes()}

    fun getNotes() = noteDao?.getNotes()

    fun getArchievedNotes() = noteDao?.getArchievedNotes()

    fun getAllLabels() = noteDao?.getAllLabels()

    fun getLabeledNotes(label : String) = noteDao?.getLabeledNotes(label)

    fun addNote(note: Note) = launch { addNoteBG(note) }
    private suspend fun addNoteBG(note : Note){
        withContext(Dispatchers.IO){
            noteDao?.addNote(note)
        }
    }

    fun addLabel(label: Label) = launch { addLabelBG(label) }
    private suspend fun addLabelBG(label: Label){
        withContext(Dispatchers.IO){
            noteDao?.addLabel(label)
        }
    }

    fun updateNote(note: Note) = launch { updateNoteBG(note) }
    private suspend fun updateNoteBG(note : Note){
        withContext(Dispatchers.IO){
            noteDao?.updateNote(note)
        }
    }

    fun updateLabel(label: Label) = launch { updateLabelBG(label) }
    private suspend fun updateLabelBG(label: Label){
        withContext(Dispatchers.IO){
            noteDao?.updateLabel(label)
        }
    }

    fun updateLabelNote(oLabel : String,nLabel : String) = launch { updateLabelNoteBG(oLabel,nLabel) }
    private suspend fun updateLabelNoteBG(oLabel: String,nLabel: String){
        withContext(Dispatchers.IO){
            noteDao?.updateLabelNote(oLabel,nLabel)
        }
    }

    fun deleteNote(note : Note) = launch { deleteNoteBG(note) }
    private suspend fun deleteNoteBG(note: Note){
        withContext(Dispatchers.IO){
            if(note.imageString!=null){
                val file = File(note.imageString!!)
                file.delete()
            }
            noteDao?.deleteNote(note)
        }
    }

    fun deleteLabel(label: Label) = launch { deleteLabelBG(label) }
    private suspend fun deleteLabelBG(label: Label){
        withContext(Dispatchers.IO){
            noteDao?.deleteLabel(label)
        }
    }

    fun deleteLabelNote(label : String) = launch { deleteLabelNoteBG(label) }
    private suspend fun deleteLabelNoteBG(label: String){
        withContext(Dispatchers.IO){
            noteDao?.deleteLabelNote(label)
        }
    }


}