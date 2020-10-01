package scarlet.believe.remember.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Dimension
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listview_home_activity.view.*
import scarlet.believe.remember.R
import scarlet.believe.remember.db.Note
import scarlet.believe.remember.utils.RecyclerViewClickListner
import scarlet.believe.remember.utils.RecyclerViewLongClickListener
import java.io.File


class HomeAdapter(val noteList: MutableList<Note>, val listner: RecyclerViewClickListner,var longlistner : RecyclerViewLongClickListener) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    class HomeViewHolder(view : View) : RecyclerView.ViewHolder(view)

    private var params : ViewGroup.MarginLayoutParams? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = (LayoutInflater.from(parent.context).inflate(R.layout.listview_home_activity,parent,false))
        val viewHolder = HomeViewHolder(view)
        params = view.note_title.layoutParams as ViewGroup.MarginLayoutParams
        view.setOnClickListener {
            listner.onRecyclerViewItemClick(view,noteList[viewHolder.adapterPosition])
        }
        view.setOnLongClickListener{
            longlistner.onItemLongClicked(view,viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun getItemId(position: Int): Long {
        return noteList[position].id.hashCode().toLong()
    }

    override fun getItemCount(): Int = noteList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        if(noteList[position].title==null) holder.itemView.note_title.visibility = View.GONE
        else holder.itemView.note_title.text = noteList[position].title
        if(noteList[position].note==null) holder.itemView.note_msg.visibility = View.GONE
        else holder.itemView.note_msg.text = noteList[position].note
        if(noteList[position].label!=null){
            holder.itemView.note_label.visibility = View.VISIBLE
            holder.itemView.note_label.text = noteList[position].label
            params?.topMargin = 0
        }
        if(noteList[position].imageString!=null){
            val imgFile = File(noteList[position].imageString)
            if (imgFile.exists()) {
                //val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                val image = BitmapFactory.decodeFile(imgFile.absolutePath,options)
                //val scaledBitmap = ImageResizer().reduceBitmapSize(bitmap,230400)
                holder.itemView.note_image.visibility = View.VISIBLE
                holder.itemView.note_image.setImageBitmap(image)
            }
        }
        //holder.itemView.note_card.setOnClickListener { listner.onRecyclerViewItemClick(holder.itemView.note_card,noteList[position]) }
    }




}