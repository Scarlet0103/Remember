package scarlet.believe.remember.home.secured

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import scarlet.believe.remember.db.*
import kotlin.coroutines.CoroutineContext

class SecuredRepository(application: Application) : CoroutineScope {

    private var noteDao : SecretNoteDao?

    init {
        val db = SecretNoteDatabase.invoke(application)
        noteDao = db.getSecretNoteDao()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    fun getAllSecretNotes() = noteDao?.getAllSecretNotes()
    //private suspend fun getAllNotesBG() = withContext(Dispatchers.IO){noteDao?.getAllNotes()}

    fun addSecretNote(secretNote: SecretNote) = launch { addSecretNoteBG(secretNote) }
    private suspend fun addSecretNoteBG(secretNote: SecretNote){
        withContext(Dispatchers.IO){
            noteDao?.addSecretNote(secretNote)
        }
    }

    fun updateSecretNote(secretNote: SecretNote) = launch { updateSecretNoteBG(secretNote) }
    private suspend fun updateSecretNoteBG(secretNote: SecretNote){
        withContext(Dispatchers.IO){
            noteDao?.updateSecretNote(secretNote)
        }
    }

    fun deleteSecretNote(secretNote: SecretNote) = launch { deleteSecretNoteBG(secretNote) }
    private suspend fun deleteSecretNoteBG(secretNote: SecretNote){
        withContext(Dispatchers.IO){
            noteDao?.deleteSecretNote(secretNote)
        }
    }


}