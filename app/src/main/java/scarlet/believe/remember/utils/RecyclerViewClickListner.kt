package scarlet.believe.remember.utils

import android.view.View
import scarlet.believe.remember.db.Note

interface RecyclerViewClickListner {
    fun onRecyclerViewItemClick(view : View, note : Note)
}