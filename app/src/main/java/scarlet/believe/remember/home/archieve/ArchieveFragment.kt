package scarlet.believe.remember.home.archieve

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

import scarlet.believe.remember.R
import scarlet.believe.remember.db.Note
import scarlet.believe.remember.home.HomeFragment

import scarlet.believe.remember.home.HomeViewModel
import scarlet.believe.remember.utils.NavigationDrawerInterface
import scarlet.believe.remember.utils.RecyclerViewLongClickListener

/**
 * A simple [Fragment] subclass.
 */
class ArchieveFragment : Fragment(),RecyclerViewLongClickListener {

    private lateinit var notesList : MutableList<Note>
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var bottomSheetView : View
    private var snackbar : Snackbar? = null
    private var isstaggeredLayout = true
    private lateinit var arch_recyclerview : RecyclerView
    private lateinit var arch_adapter : ArchieveAdapter
    private lateinit var nav_btn : ImageButton
    private lateinit var archLayoutChangeBtn : ImageButton
    private lateinit var arch_constraintlayout : CoordinatorLayout
    private lateinit var bg_constraintlayout : ConstraintLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_archieve, container, false)
        initView(view)
        initViewModel()
        onBackPressed()
        return view
    }

    private fun initView(view : View){

        bottomSheetDialog = BottomSheetDialog(this.context!!,R.style.BottomSheetDialogTheme)
        bottomSheetView = LayoutInflater.from(this.context).inflate(R.layout.archieve_bottom_sheet, view.findViewById(R.id.bottom_sheet_archieve))

        notesList = mutableListOf()
        nav_btn =  view.findViewById(R.id.navdrawer_archBtn)
        arch_recyclerview = view.findViewById(R.id.arch_recyclerview)
        arch_constraintlayout = view.findViewById(R.id.arch_constraintlayout)
        bg_constraintlayout = view.findViewById(R.id.bg_archive)
        archLayoutChangeBtn = view.findViewById(R.id.arc_layoutchange)

        nav_btn.setOnClickListener {
            (activity as NavigationDrawerInterface).opencloseDrawer()
        }

        archLayoutChangeBtn.setOnClickListener {
            if(isstaggeredLayout){
                arch_recyclerview.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
                arch_adapter.notifyItemRangeChanged(0,arch_adapter.itemCount)
                archLayoutChangeBtn.background = ContextCompat.getDrawable(this.context!!,R.drawable.ic_square2)
                isstaggeredLayout = false
            }else{
                arch_recyclerview.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
                arch_adapter.notifyItemRangeChanged(0,arch_adapter.itemCount)
                archLayoutChangeBtn.background = ContextCompat.getDrawable(this.context!!,R.drawable.ic_square1)
                isstaggeredLayout = true
            }
        }

    }

    private fun initViewModel(){
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        getArchieveNotes()
    }

    private fun getArchieveNotes() {
        homeViewModel.getArchieveNotes()?.observe(viewLifecycleOwner, Observer {
            notesList = it.toMutableList()
            addToRecyclerView()
        })
    }

    private fun addToRecyclerView() {
        arch_recyclerview.setItemViewCacheSize(20)
        arch_recyclerview.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(arch_recyclerview)
        arch_adapter = ArchieveAdapter(notesList,this)
        arch_adapter.setHasStableIds(true)
        arch_recyclerview.adapter = arch_adapter
        if(arch_adapter.itemCount>0) bg_constraintlayout.visibility = View.GONE
        else bg_constraintlayout.visibility = View.VISIBLE
    }

    val itemTouchHelperCallback :ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onSwiped(viewHolder.adapterPosition)
            }

        }

    private fun onSwiped(position: Int){
        var undoClicked = false
        val note = notesList[position]
        notesList.removeAt(position)
        arch_adapter.notifyItemRemoved(position)
        snackbar = Snackbar.make(arch_constraintlayout,"Note Unarchived", Snackbar.LENGTH_SHORT)
        snackbar!!.setActionTextColor(ContextCompat.getColor(activity!!,R.color.white))
        snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        val view = snackbar!!.view
        view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
        snackbar!!.setAction("Undo",View.OnClickListener {
                undoClicked = true
                notesList.add(position,note)
                arch_adapter.notifyItemInserted(position)
            })
            .addCallback( object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if(!undoClicked){
                        unArchieveNote(note)
                    }
                    super.onDismissed(transientBottomBar, event)
                }
            })
            .show()
    }

    private fun unArchieveNote(note : Note){
        note.isArchieved = 0
        homeViewModel.updateNote(note)
    }

    private fun onDelete(position: Int){
        var undoClicked = false
        val note = notesList[position]
        notesList.removeAt(position)
        arch_adapter.notifyItemRemoved(position)
        snackbar = Snackbar.make(arch_constraintlayout,"Note Deleted", Snackbar.LENGTH_LONG)
        snackbar!!.setActionTextColor(ContextCompat.getColor(activity!!,R.color.white))
        snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        val view = snackbar!!.view
        view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
        snackbar!!.setAction("Undo",View.OnClickListener {
                undoClicked = true
                notesList.add(position,note)
                arch_adapter.notifyItemInserted(position)
            })
            .addCallback( object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if(!undoClicked){
                        deleteNote(note)
                    }
                    super.onDismissed(transientBottomBar, event)
                }
            })
            .show()
    }

    private fun deleteNote(note: Note){
        homeViewModel.deleteNote(note)
    }

    override fun onItemLongClicked(view: View, position: Int): Boolean {

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        bottomSheetView.findViewById<LinearLayout>(R.id.unarchieve_note).setOnClickListener {
            onSwiped(position)
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.delete_note).setOnClickListener {
            onDelete(position)
            bottomSheetDialog.dismiss()
        }
        return true
    }

    private fun onBackPressed(){
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container_frag, HomeFragment())
                    ?.commit()
            }
        })
    }



}
