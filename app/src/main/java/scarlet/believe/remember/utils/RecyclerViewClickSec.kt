package scarlet.believe.remember.utils

import android.view.View
import scarlet.believe.remember.db.Note
import scarlet.believe.remember.db.SecretNote
import java.text.FieldPosition

interface RecyclerViewClickSec {
    fun onRecyclerViewItemClick(view : View, secretNote: SecretNote,position: Int)
}