package scarlet.believe.remember.home.secured

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import scarlet.believe.remember.db.SecretNote

class SecuredViewModel(application: Application) : AndroidViewModel(application) {

    private var secretRepository : SecuredRepository = SecuredRepository(application)

    fun getAllSecretNotes() = secretRepository.getAllSecretNotes()

    fun addSecretNote(secretNote: SecretNote) { secretRepository.addSecretNote(secretNote) }

    fun updateSecretNote(secretNote: SecretNote) { secretRepository.updateSecretNote(secretNote) }

    fun deleteSecretNote(secretNote: SecretNote) { secretRepository.deleteSecretNote(secretNote) }

}