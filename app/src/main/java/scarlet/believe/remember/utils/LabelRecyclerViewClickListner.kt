package scarlet.believe.remember.utils

import android.view.View
import scarlet.believe.remember.db.Label
import scarlet.believe.remember.db.Note

interface LabelRecyclerViewClickListner {
    fun onRecyclerViewItemClick(view : View, label : String)
}