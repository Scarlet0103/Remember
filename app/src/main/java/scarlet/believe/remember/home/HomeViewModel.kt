package scarlet.believe.remember.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import scarlet.believe.remember.db.Label
import scarlet.believe.remember.db.Note

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var homeRepository : HomeRepository = HomeRepository(application)

    fun getAllNotes() = homeRepository.getAllNotes()

    fun getNotes() = homeRepository.getNotes()

    fun getArchieveNotes() = homeRepository.getArchievedNotes()

    fun getAllLabels() = homeRepository.getAllLabels()

    fun getLabledNotes(label : String) = homeRepository.getLabeledNotes(label)

    fun addNote(note : Note) { homeRepository.addNote(note) }

    fun addLabel(label: Label) { homeRepository.addLabel(label) }

    fun updateNote(note: Note) { homeRepository.updateNote(note) }

    fun updateLabel(label: Label) { homeRepository.updateLabel(label) }

    fun updateLabelNote(oLabel : String,nLabel: String) { homeRepository.updateLabelNote(oLabel,nLabel) }

    fun deleteNote(note: Note) { homeRepository.deleteNote(note) }

    fun deleteLabel(label: Label) { homeRepository.deleteLabel(label) }

    fun deleteLabelNote(label : String) {homeRepository.deleteLabelNote(label)}

}