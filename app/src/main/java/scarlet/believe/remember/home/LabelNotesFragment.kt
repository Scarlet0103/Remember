package scarlet.believe.remember.home

import android.graphics.Color
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
import scarlet.believe.remember.utils.NavigationDrawerInterface
import scarlet.believe.remember.utils.RecyclerViewClickListner
import scarlet.believe.remember.utils.RecyclerViewLongClickListener

class LabelNotesFragment(val labelName : String) : Fragment() , RecyclerViewClickListner, RecyclerViewLongClickListener {

    private var BACK_STACK = "root_fragment"
    private var isSwipeEnabled =  true
    private var snackbar : Snackbar? = null
    private var isstaggeredLayout = true
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var bottomSheetView : View
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var label_recyclerview : RecyclerView
    private lateinit var label_adapter : HomeAdapter
    private lateinit var nav_btn : ImageButton
    private lateinit var labelLayoutChangeBtn : ImageButton
    private lateinit var coordinatorLayout : CoordinatorLayout
    private lateinit var bg_constraintlayout : ConstraintLayout
    private var notesList = mutableListOf<Note>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_label_notes, container, false)
        initView(view)
        initViewModel()
        onBackPressed()
        return view
    }

    private fun initView(view: View) {

        bottomSheetDialog = BottomSheetDialog(this.context!!,R.style.BottomSheetDialogTheme)
        bottomSheetView = LayoutInflater.from(this.context).inflate(R.layout.all_bottom_sheet, view.findViewById(R.id.bottom_sheet_all))

        label_recyclerview = view.findViewById(R.id.label_notes_recyclerview)
        nav_btn =  view.findViewById(R.id.navdrawer_labelBtn)
        coordinatorLayout = view.findViewById(R.id.label_coordinator_layout)
        bg_constraintlayout = view.findViewById(R.id.bg_labelnotes)
        labelLayoutChangeBtn = view.findViewById(R.id.label_layoutchange)

        nav_btn.setOnClickListener {
            (activity as NavigationDrawerInterface).opencloseDrawer()
        }

        labelLayoutChangeBtn.setOnClickListener {
            if(isstaggeredLayout){
                label_recyclerview.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
                label_adapter.notifyItemRangeChanged(0,label_adapter.itemCount)
                labelLayoutChangeBtn.background = ContextCompat.getDrawable(this.context!!,R.drawable.ic_square2)
                isstaggeredLayout = false
            }else{
                label_recyclerview.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
                label_adapter.notifyItemRangeChanged(0,label_adapter.itemCount)
                labelLayoutChangeBtn.background = ContextCompat.getDrawable(this.context!!,R.drawable.ic_square1)
                isstaggeredLayout = true
            }
        }

    }

    private fun initViewModel() {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        getLabeledNotes()
    }

    private fun getLabeledNotes() {
        homeViewModel.getLabledNotes(labelName)?.observe(viewLifecycleOwner, Observer {
            notesList = it.toMutableList()
            addToRecyclerView()
        })
    }

    private fun addToRecyclerView() {
        label_recyclerview.setItemViewCacheSize(20)
        label_recyclerview.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(label_recyclerview)
        label_adapter = HomeAdapter(notesList,this,this)
        label_adapter.setHasStableIds(true)
        label_recyclerview.adapter = label_adapter
        if(label_adapter.itemCount>0) bg_constraintlayout.visibility = View.GONE
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
                return isSwipeEnabled
            }

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return super.getSwipeEscapeVelocity(defaultValue*10)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //onSwiped(viewHolder.adapterPosition)
                val position = viewHolder.adapterPosition
                val note = notesList[position]
                isSwipeEnabled = false
                isItemViewSwipeEnabled
                notesList.removeAt(position)
                label_adapter.notifyItemRemoved(position)
                var undoClicked = false
                snackbar = Snackbar.make(coordinatorLayout,"Label Removed", Snackbar.LENGTH_SHORT)
                snackbar!!.setActionTextColor(Color.BLACK)
                snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
                val view = snackbar!!.view
                view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
                snackbar!!.setAction("Undo",View.OnClickListener {
                        undoClicked = true
                        notesList.add(position,note)
                        label_adapter.notifyItemInserted(position)
                    })
                    .addCallback( object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if(!undoClicked){
                                removeLabel(note)
                            }
                            isSwipeEnabled = true
                            isItemViewSwipeEnabled
                            super.onDismissed(transientBottomBar, event)
                        }
                    })
                    .show()
            }

        }

    private fun removeLabel(note: Note){
        note.label = null
        homeViewModel.updateNote(note)
    }

    override fun onRecyclerViewItemClick(view: View, note: Note) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container_frag,NoteAddFragment(note))
            ?.addToBackStack(BACK_STACK)
            ?.commit()
    }

    override fun onItemLongClicked(view: View, position: Int): Boolean {
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        bottomSheetView.findViewById<LinearLayout>(R.id.archieve_note_all).setOnClickListener {
            onSwiped(position)
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.delete_note_all).setOnClickListener {
            onDelete(position)
            bottomSheetDialog.dismiss()
        }
        return true
    }

    private fun onSwiped(position: Int){
        var undoClicked = false
        val note = notesList[position]
        notesList.removeAt(position)
        label_adapter.notifyItemRemoved(position)
        snackbar = Snackbar.make(coordinatorLayout,"Note Archieved", Snackbar.LENGTH_SHORT)
        snackbar!!.setActionTextColor(ContextCompat.getColor(activity!!,R.color.white))
        snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        val view = snackbar!!.view
        view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
        snackbar!!.setAction("Undo",View.OnClickListener {
            undoClicked = true
            notesList.add(position,note)
            label_adapter.notifyItemInserted(position)
        }).addCallback( object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if(!undoClicked){
                        archieveNote(note)
                    }
                    super.onDismissed(transientBottomBar, event)
                }
            })
            .show()
    }

    private fun archieveNote(note : Note){
        note.isArchieved = 1
        homeViewModel.updateNote(note)
    }

    private fun onDelete(position: Int){
        var undoClicked = false
        val note = notesList[position]
        notesList.removeAt(position)
        label_adapter.notifyItemRemoved(position)
        snackbar = Snackbar.make(coordinatorLayout,"Note Deleted", Snackbar.LENGTH_LONG)
        snackbar!!.setActionTextColor(ContextCompat.getColor(activity!!,R.color.white))
        snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        val view = snackbar!!.view
        view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
        snackbar!!.setAction("Undo",View.OnClickListener {
            undoClicked = true
            notesList.add(position,note)
            label_adapter.notifyItemInserted(position)
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

    private fun onBackPressed(){
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container_frag,HomeFragment())
                    ?.commit()
            }
        })
    }



}