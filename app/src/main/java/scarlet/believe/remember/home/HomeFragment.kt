package scarlet.believe.remember.home

import android.app.UiModeManager
import android.content.Context
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.Explode
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

import scarlet.believe.remember.R
import scarlet.believe.remember.db.Note
import scarlet.believe.remember.utils.NavigationDrawerInterface
import scarlet.believe.remember.utils.RecyclerViewClickListner
import scarlet.believe.remember.utils.RecyclerViewLongClickListener
import scarlet.believe.remember.utils.SelectMenuItemNav


class HomeFragment : Fragment() , RecyclerViewClickListner , RecyclerViewLongClickListener{


    private var notesList = mutableListOf<Note>()
    private var BACK_STACK = "root_fragment"
    private var isSwipeEnabled =  true
    private var isstaggeredLayout = true
    private var snackbar : Snackbar? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var home_recyclerview : RecyclerView
    private lateinit var home_adapter : HomeAdapter
    private lateinit var addnote_Btn : ImageButton
    private lateinit var nav_btn : ImageButton
    private lateinit var homeLayoutChangeBtn : ImageButton
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var bg_home : ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        //hideKeyboard()
        initView(view)
        initViewModel()
        return view
    }

    private fun initView(view : View){

        (activity as SelectMenuItemNav).selectMenuItem(R.id.notes_menu)

        home_recyclerview = view.findViewById(R.id.home_recyclerview)
        addnote_Btn = view.findViewById(R.id.addnote_Btn)
        homeLayoutChangeBtn = view.findViewById(R.id.home_layoutchange)
        nav_btn =  view.findViewById(R.id.navdrawer_homeBtn)
        coordinatorLayout = view.findViewById(R.id.constraint_layout_main)
        bg_home = view.findViewById(R.id.bg_home)

        addnote_Btn.setOnClickListener {
            //invokeHomeLayout()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container_frag,NoteAddFragment(null))
                ?.addToBackStack(BACK_STACK)
                ?.commit()
        }

        homeLayoutChangeBtn.setOnClickListener {

            if(isstaggeredLayout){
                home_recyclerview.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
                home_adapter.notifyItemRangeChanged(0,home_adapter.itemCount)
                homeLayoutChangeBtn.background = ContextCompat.getDrawable(this.context!!,R.drawable.ic_square2)
                isstaggeredLayout = false
            }else{
                home_recyclerview.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
                home_adapter.notifyItemRangeChanged(0,home_adapter.itemCount)
                homeLayoutChangeBtn.background = ContextCompat.getDrawable(this.context!!,R.drawable.ic_square1)
                isstaggeredLayout = true
            }

        }

        nav_btn.setOnClickListener {
            (activity as NavigationDrawerInterface).opencloseDrawer()
        }

    }

    private fun initViewModel(){
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        getNotes()
    }

    private fun getNotes(){
        homeViewModel.getNotes()?.observe(viewLifecycleOwner, Observer {
            notesList = it.toMutableList()
            addToRecyclerView()
        })
    }

    private fun addToRecyclerView(){
        home_recyclerview.setItemViewCacheSize(20)
        home_recyclerview.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(home_recyclerview)
        home_adapter = HomeAdapter(notesList,this,this)
        home_adapter.setHasStableIds(true)
        home_recyclerview.adapter = home_adapter
        if(home_adapter.itemCount > 0) bg_home.visibility = View.GONE
        else bg_home.visibility = View.VISIBLE
    }

    override fun onRecyclerViewItemClick(view: View, note: Note) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container_frag,NoteAddFragment(note))
            ?.addToBackStack(BACK_STACK)
            ?.commit()
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
                var position = viewHolder.adapterPosition
                val note = notesList[position]
                isSwipeEnabled = false
                isItemViewSwipeEnabled
                notesList.removeAt(position)
                home_adapter.notifyItemRemoved(position)
                var undoClicked = false
                snackbar = Snackbar.make(coordinatorLayout,"Note Archived",Snackbar.LENGTH_SHORT)
                snackbar!!.setActionTextColor(ContextCompat.getColor(activity!!,R.color.white))
                snackbar!!.anchorView = addnote_Btn
                snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
                val view = snackbar!!.view
                view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
                snackbar!!.setAction("Undo",View.OnClickListener {
                        undoClicked = true
                        notesList.add(position,note)
                        home_adapter.notifyItemInserted(position)
                    })
                    .addCallback( object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if(!undoClicked){
                                archieveNote(note)
                            }
                            isSwipeEnabled = true
                            isItemViewSwipeEnabled
                            super.onDismissed(transientBottomBar, event)
                        }
                    })
                    .show()
            }

        }

    private fun archieveNote(note : Note){
        note.isArchieved = 1
        homeViewModel.updateNote(note)
    }

    private fun hideKeyboard(){
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currectFocusedView = activity?.currentFocus
        currectFocusedView.let {
            inputMethodManager.hideSoftInputFromWindow(currectFocusedView?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onItemLongClicked(view: View, position: Int): Boolean {
        return true
    }

}
