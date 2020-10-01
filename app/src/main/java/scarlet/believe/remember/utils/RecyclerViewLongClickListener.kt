package scarlet.believe.remember.utils

import android.view.View
import scarlet.believe.remember.db.Note

interface RecyclerViewLongClickListener {
    fun onItemLongClicked(view : View,position: Int) : Boolean
}