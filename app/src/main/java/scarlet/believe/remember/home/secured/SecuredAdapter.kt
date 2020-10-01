package scarlet.believe.remember.home.secured

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listview_secured.view.*
import scarlet.believe.remember.R
import scarlet.believe.remember.db.SecretNote
import scarlet.believe.remember.utils.RecyclerViewClickSec
import scarlet.believe.remember.utils.RecyclerViewLongClickListener
import java.security.Key
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class SecuredAdapter(val noteList: MutableList<SecretNote>, val listner: RecyclerViewClickSec, var longlistner : RecyclerViewLongClickListener) : RecyclerView.Adapter<SecuredAdapter.SecuredViewHolder>() {

    class SecuredViewHolder(view : View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecuredViewHolder {
        val view = (LayoutInflater.from(parent.context).inflate(R.layout.listview_secured,parent,false))
        val viewHolder = SecuredViewHolder(view)
        view.setOnClickListener {
            listner.onRecyclerViewItemClick(view,noteList[viewHolder.adapterPosition],viewHolder.adapterPosition)
        }
        view.setOnLongClickListener{
            longlistner.onItemLongClicked(view,viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun getItemCount() = noteList.size

    override fun getItemId(position: Int): Long {
        return noteList[position].id.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: SecuredViewHolder, position: Int) {

        holder.itemView.secnote_title.text =noteList[position].title

    }


}