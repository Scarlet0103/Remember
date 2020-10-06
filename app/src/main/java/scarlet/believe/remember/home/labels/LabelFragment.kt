package scarlet.believe.remember.home.labels

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

import scarlet.believe.remember.R
import scarlet.believe.remember.db.Label
import scarlet.believe.remember.home.HomeFragment
import scarlet.believe.remember.home.HomeViewModel
import scarlet.believe.remember.utils.ListViewClickListner
import scarlet.believe.remember.utils.NavigationDrawerInterface
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 */
class LabelFragment : Fragment(), ListViewClickListner {

    private var snackbar : Snackbar? = null
    private lateinit var labelListview : RecyclerView
    private lateinit var labelAdapter : LabelAdapter
    private lateinit var labelNames : MutableList<String>
    private lateinit var labelName : EditText
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var bottomSheetView : View
    private lateinit var labelBtn : ImageButton
    private lateinit var drawerBtn : ImageButton
    private lateinit var layout : ConstraintLayout
    private lateinit var bg_constraintlayout : ConstraintLayout
    private lateinit var homeViewModel : HomeViewModel
    private lateinit var labelList: MutableList<Label>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_label, container, false)
        initView(view)
        initViewModel()
        onBackPressed()
        return view
    }

    private fun initView(view : View){

        bottomSheetDialog = BottomSheetDialog(this.context!!,R.style.BottomSheetDialogTheme)
        bottomSheetView = LayoutInflater.from(this.context).inflate(R.layout.bottomsheet_edit_label, view.findViewById(R.id.bottom_sheet_container_label))

        labelList = mutableListOf()
        labelNames = mutableListOf()
        drawerBtn = view.findViewById(R.id.navdrawer_alLabelsBtn)
        labelListview = view.findViewById(R.id.label_listview)
        bg_constraintlayout = view.findViewById(R.id.bg_labeladd)
        layout = view.findViewById(R.id.main_constraintLayout)
        labelName = view.findViewById(R.id.editText_label_name)
        labelBtn = view.findViewById(R.id.labelBtn)
        labelBtn.setOnClickListener {
            if(labelName.text.isNullOrEmpty()){
                snackbar = Snackbar.make(layout,"Label Name cannot be empty",Snackbar.LENGTH_SHORT)
                snackbar!!.setActionTextColor(ContextCompat.getColor(activity!!,R.color.white))
                snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
                val view = snackbar!!.view
                view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
                snackbar!!.show()
                return@setOnClickListener
            }else if(labelNames.contains(labelName.text.toString())){
                snackbar = Snackbar.make(layout,"Label Name already exists",Snackbar.LENGTH_SHORT)
                snackbar!!.setActionTextColor(ContextCompat.getColor(activity!!,R.color.white))
                snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
                val view = snackbar!!.view
                view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
                snackbar!!.show()
                return@setOnClickListener
            }
            else {
                val label : Label = Label(labelName = labelName.text.toString())
                addLabelToDatabase(label)
            }
        }

        drawerBtn.setOnClickListener {
            (activity as NavigationDrawerInterface).opencloseDrawer()
        }

    }

    private fun initViewModel(){

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.getAllLabels()?.observe(viewLifecycleOwner, Observer {
            labelList = it.toMutableList()
            var size = it.size
            if(size!=0){
                for(i in 0 until size)
                    labelNames.add(it[i].labelName!!)
            }
            addToListView()
        })

    }

    private fun addToListView() {

        labelListview.hasFixedSize()
        labelListview.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
        labelAdapter = LabelAdapter(labelList,labelNames,this)
        labelListview.adapter = labelAdapter
        if(labelAdapter.itemCount>0) bg_constraintlayout.visibility = View.GONE
        else bg_constraintlayout.visibility = View.VISIBLE
    }

    private fun addLabelToDatabase(label: Label){
        homeViewModel.addLabel(label)
        labelName.text.clear()
        labelName.clearFocus()
        labelAdapter.notifyDataSetChanged()
    }

    override fun listviewClickListner(view: View, label: Label, position: Int) {


        val editText = bottomSheetView.findViewById<EditText>(R.id.editText_listview)
        editText.setText(label.labelName)
        val temp = labelNames
        temp.remove(label.labelName)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        bottomSheetView.findViewById<Button>(R.id.delete_btn).setOnClickListener {
                labelList.removeAt(position)
                addtoLabelNames()
                homeViewModel.deleteLabel(label)
                homeViewModel.deleteLabelNote(label.labelName!!)
                labelAdapter.notifyItemRemoved(position)
                editText.text.clear()
                bottomSheetDialog.dismiss()
                hideKeyboard()
        }

        bottomSheetView.findViewById<Button>(R.id.update_button).setOnClickListener{
            if(label.labelName == editText.text.toString()){
                bottomSheetDialog.dismiss()
            }else if(label.labelName.isNullOrEmpty()){
                Toast.makeText(this.context,"Label Name Cannot Be Empty",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(temp.contains(editText.text.toString())){
                Toast.makeText(this.context,"Label Already Exists",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                val updatelabel = Label(id = label.id,labelName = editText.text.toString())
                editText.text.clear()
                for(i in labelNames)
                    Log.i("T",i)
                Log.i("T",position.toString())
                labelList[position] = updatelabel
                addtoLabelNames()
                homeViewModel.updateLabel(updatelabel)
                homeViewModel.updateLabelNote(label.labelName!!,updatelabel.labelName!!)
                labelAdapter.notifyItemChanged(position)
                bottomSheetDialog.dismiss()
                hideKeyboard()
            }
        }
    }

    private fun addtoLabelNames(){
        labelNames.clear()
        for (i in labelList){
            labelNames.add(i.labelName!!)
        }
    }

    private fun hideKeyboard(){
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currectFocusedView = activity?.currentFocus
        currectFocusedView.let {
            inputMethodManager.hideSoftInputFromWindow(currectFocusedView?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
        }
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
