package scarlet.believe.remember.home

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import scarlet.believe.remember.BuildConfig
import scarlet.believe.remember.R
import scarlet.believe.remember.db.Label
import scarlet.believe.remember.db.Note
import scarlet.believe.remember.utils.LabelRecyclerViewClickListner
import scarlet.believe.remember.utils.NavigationDrawerInterface
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

class NoteAddFragment(private var updateNote: Note?) : Fragment(), LabelRecyclerViewClickListner {

    private lateinit var homeViewModel : HomeViewModel


    private var imagePath : String? = null

    private var labelsList = mutableListOf<Label>()
    private var labelNames = mutableListOf<String>()

    private val PICK_IMAGE : Int = 100

    private lateinit var back_Btn : ImageButton
    private lateinit var addImage : ImageButton
    private lateinit var noteImage : ImageView
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var bottomSheetView : View
    private lateinit var scrollView: NestedScrollView
    private lateinit var title_editTxt : EditText
    private lateinit var label_txtView : TextView
    private lateinit var msg_editTxt : EditText
    private lateinit var label_recyclerView : RecyclerView
    private lateinit var label_adapter : BottomLabelAdapter
    private lateinit var clipboard : ClipboardManager
    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_add, container, false)
        initView(view)
        updateNote()
        getAllLabels()
        onBackPressed()
        return view
    }

    private fun initView(view: View) {

        (activity as NavigationDrawerInterface).lockDrawer()
        //activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        bottomSheetDialog = BottomSheetDialog(this.context!!,R.style.BottomSheetDialogTheme)
        bottomSheetView = LayoutInflater.from(this.context).inflate(R.layout.add_bottom_sheet, view.findViewById(R.id.bottom_sheet_container))

        back_Btn = view.findViewById(R.id.back_Btn)
        back_Btn.setOnClickListener {
            addNoteToDatabase()
        }
        addImage = view.findViewById(R.id.add_image)
        addImage.setOnClickListener {
            //openDialogBox()
            openBottomSheet()
        }
        label_recyclerView = bottomSheetView.findViewById(R.id.bottom_labels_recyclerview)
        scrollView = view.findViewById(R.id.scrollView_note_add)
        noteImage = view.findViewById(R.id.noteImage)
        title_editTxt = view.findViewById(R.id.title_editTxt)
        label_txtView = view.findViewById(R.id.label_txtview)
        msg_editTxt = view.findViewById(R.id.msg_editTxt)
        constraintLayout = view.findViewById(R.id.constraint_layout_noteadd)

        label_txtView.setOnClickListener {
            label_txtView.text = null
            label_txtView.visibility = View.GONE
        }

        clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    }


    private fun initViewModel(){
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private fun getAllLabels(){
        homeViewModel.getAllLabels()?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            labelsList = it.toMutableList()
            val size = it.size
            if(size!=0){
                for(i in 0 until size)
                    labelNames.add(it[i].labelName!!)
            }
            addLabelsToRecyclerview()
        })
    }

    private fun addLabelsToRecyclerview(){
        val options = mutableListOf<String>()
        options.add("Personal")
        options.add("Work")
        for(i in labelsList)
            options.add(i.labelName!!)
        label_recyclerView.setItemViewCacheSize(20)
        label_recyclerView.layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.HORIZONTAL,false)
        label_adapter = BottomLabelAdapter(options,this)
        label_recyclerView.adapter = label_adapter
    }

    private fun updateNote(){
        if(updateNote!=null){
            title_editTxt.setText(updateNote!!.title)
            msg_editTxt.setText(updateNote!!.note)
            if(updateNote!!.label!=null){
                label_txtView.visibility = View.VISIBLE
                label_txtView.text = updateNote!!.label
            }
            if(updateNote!!.imageString!=null){
                val imgFile = File(updateNote!!.imageString)
                imagePath = imgFile.absolutePath
                if (imgFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    noteImage.visibility = View.VISIBLE
                    noteImage.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun addNoteToDatabase(){
        if(!title_editTxt.text.isNullOrEmpty() || !msg_editTxt.text.isNullOrEmpty() || imagePath!=null){
            var title : String? = null
            var msg : String? = null
            var label : String? = null
            if(!title_editTxt.text.isNullOrEmpty()) title = title_editTxt.text.toString()
            if(!msg_editTxt.text.isNullOrEmpty()) msg = msg_editTxt.text.toString()
            if(!label_txtView.text.isNullOrEmpty()) label = label_txtView.text.toString()
            val note = Note(title = title,note = msg,imageString = imagePath,label = label)
            if(updateNote!=null){
                //Toast.makeText(activity,"updated",Toast.LENGTH_SHORT).show()
                note.id = updateNote!!.id
                homeViewModel.updateNote(note)
            }else{
                homeViewModel.addNote(note)
            }
        }
        activity?.supportFragmentManager?.popBackStackImmediate()
    }


    private fun openImage(){
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data!=null){
            val imageSelected = data.data!!
            noteImage.visibility = View.VISIBLE
            noteImage.setImageURI(imageSelected)
            scrollView.fullScroll(View.FOCUS_DOWN)
            if(imagePath!=null){
                val file = File(imagePath)
                file.delete()
            }
            saveImageToDir()
        }
    }

    private fun saveImageToDir(){

        val cw = ContextWrapper(activity?.application?.applicationContext)
        val directory: File = cw.getDir("RememberImageDir", Context.MODE_PRIVATE)
        val time = Calendar.getInstance().time.toString()
        val file = File(directory,time+".jpg")
        val drawable : BitmapDrawable = noteImage.drawable as BitmapDrawable
        val bitmap : Bitmap = drawable.bitmap
        //val scaledBitmap = ImageResizer().reduceBitmapSize(bitmap,230400)
        if (!file.exists()) {
            var  fos : FileOutputStream? = null
            try {
                fos = FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                imagePath = file.absolutePath
                fos.close()
            }catch (e : FileNotFoundException){

            }catch (e : IOException){

            }
        }
    }

    private fun openBottomSheet(){
        val labelEnter = bottomSheetView.findViewById<MaterialCardView>(R.id.enterlabel_option_bs)
        val editTextLabel = bottomSheetView.findViewById<EditText>(R.id.editText_labelenter)
        bottomSheetView.findViewById<MaterialCardView>(R.id.camera_option_bs).setOnClickListener {
                openImage()
                bottomSheetDialog.dismiss()
        }
        bottomSheetView.findViewById<MaterialCardView>(R.id.copy_option_bs).setOnClickListener {

                bottomSheetDialog.dismiss()
                val clipboardText = title_editTxt.text.toString() +"\n" +msg_editTxt.text.toString()
                val clipData = ClipData.newPlainText("CopyNote",clipboardText)
                clipboard.setPrimaryClip(clipData)

        }
        bottomSheetView.findViewById<MaterialCardView>(R.id.delete_option_bs).setOnClickListener {
                bottomSheetDialog.dismiss()
                if(updateNote!=null){
                    homeViewModel.deleteNote(updateNote!!)
                }
                activity?.supportFragmentManager?.popBackStackImmediate()
        }
        bottomSheetView.findViewById<MaterialCardView>(R.id.newlabel_option_bs).setOnClickListener {
            if(labelEnter.visibility==View.VISIBLE) labelEnter.visibility = View.GONE else labelEnter.visibility = View.VISIBLE
        }
        editTextLabel.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                if((p2?.action==KeyEvent.ACTION_DOWN) || (p2?.action==KeyEvent.KEYCODE_ENTER)){
                    hideKeyboard()
                    if(!editTextLabel.text.isNullOrEmpty()){
                        enterLabel(editTextLabel.text.toString())
                    }else{
                        bottomSheetDialog.dismiss()
                        showSnackbar("Label Name Cannot Be Empty")
                    }
                    editTextLabel.text.clear()
                    labelEnter.visibility = View.GONE
                    return true
                }
                return false
            }
        })
        bottomSheetView.findViewById<MaterialCardView>(R.id.share_option_bs).setOnClickListener {
            if(!title_editTxt.text.isNullOrEmpty() || !msg_editTxt.text.isNullOrEmpty() ){
                val text = title_editTxt.text.toString() +"\n\n" +msg_editTxt.text.toString()
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT,text)
                intent.type = "text/plain"
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                bottomSheetDialog.dismiss()
                startActivity(Intent.createChooser(intent,"send"))
            }
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun enterLabel(label: String){
        if(labelNames.contains(label)){
            bottomSheetDialog.dismiss()
            showSnackbar("Label Already Exists")
        }else{
            val Label = Label(labelName = label)
            homeViewModel.addLabel(Label)
            label_adapter.notifyDataSetChanged()
            setLabel(label)
            bottomSheetDialog.dismiss()
        }
    }

    private fun hideKeyboard(){
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currectFocusedView = activity?.currentFocus
        currectFocusedView.let {
            inputMethodManager.hideSoftInputFromWindow(currectFocusedView?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun showSnackbar(text: String){
        val snackbar = Snackbar.make(constraintLayout,text, Snackbar.LENGTH_SHORT)
        snackbar!!.setActionTextColor(ContextCompat.getColor(activity!!,R.color.white))
        snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        val view = snackbar!!.view
        view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
        snackbar.show()
    }

    override fun onRecyclerViewItemClick(view: View, label: String) {
        setLabel(label)
        bottomSheetDialog.dismiss()
    }

    private fun setLabel(label: String){
        label_txtView.visibility = View.VISIBLE
        label_txtView.text = label
    }

    private fun onBackPressed(){
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                addNoteToDatabase()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as NavigationDrawerInterface).unlockDrawer()
    }


}