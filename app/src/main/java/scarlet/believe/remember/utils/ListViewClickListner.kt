package scarlet.believe.remember.utils

import android.view.View
import scarlet.believe.remember.db.Label
import scarlet.believe.remember.db.Note
import java.text.FieldPosition

interface ListViewClickListner {
    fun listviewClickListner(view : View, label: Label,position: Int)
}