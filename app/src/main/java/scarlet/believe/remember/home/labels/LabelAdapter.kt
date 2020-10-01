package scarlet.believe.remember.home.labels

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listview_label_fragment.view.*
import org.w3c.dom.Text
import scarlet.believe.remember.R
import scarlet.believe.remember.db.Label
import scarlet.believe.remember.utils.ListViewClickListner

class LabelAdapter(val labelList : MutableList<Label> ,val labelNames : MutableList<String>, val listner : ListViewClickListner ) : RecyclerView.Adapter<LabelAdapter.LabelViewHolder>(){

    class LabelViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_label_fragment, parent, false)
        val viewHolder = LabelViewHolder(view)
        view.findViewById<TextView>(R.id.text_listview).setOnClickListener {
            listner.listviewClickListner(view,labelList[viewHolder.adapterPosition],viewHolder.adapterPosition)
        }
        view.findViewById<ImageButton>(R.id.editBtn_listview).setOnClickListener {
            listner.listviewClickListner(view,labelList[viewHolder.adapterPosition],viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun getItemCount(): Int = labelList.size

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {

        holder.itemView.text_listview.text = labelList[position].labelName

    }

}