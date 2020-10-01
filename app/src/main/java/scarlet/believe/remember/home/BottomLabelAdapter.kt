package scarlet.believe.remember.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_label_listview.view.*
import scarlet.believe.remember.R
import scarlet.believe.remember.db.Label
import scarlet.believe.remember.utils.LabelRecyclerViewClickListner

class BottomLabelAdapter(val labelsList: MutableList<String>, val listner: LabelRecyclerViewClickListner) : RecyclerView.Adapter<BottomLabelAdapter.BottomLabelViewHolder>() {

    class BottomLabelViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomLabelViewHolder {
        val view = (LayoutInflater.from(parent.context).inflate(R.layout.bottom_label_listview,parent,false))
        val viewHolder = BottomLabelViewHolder(view)
        view.setOnClickListener {
            listner.onRecyclerViewItemClick(view,labelsList[viewHolder.adapterPosition])
        }
        return viewHolder
    }


    override fun getItemCount() = labelsList.size

    override fun onBindViewHolder(holder: BottomLabelViewHolder, position: Int) {
        holder.itemView.bottom_label_textview.text = labelsList[position]
    }
}