package scarlet.believe.remember.home.secured

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import scarlet.believe.remember.R
import scarlet.believe.remember.db.SecretNote
import scarlet.believe.remember.home.HomeFragment
import scarlet.believe.remember.utils.BiometricCancelListner
import scarlet.believe.remember.utils.NavigationDrawerInterface
import scarlet.believe.remember.utils.RecyclerViewClickSec
import scarlet.believe.remember.utils.RecyclerViewLongClickListener
import java.security.Key
import java.security.KeyStore
import java.security.Signature
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec


class SecuredFragment : Fragment() , RecyclerViewLongClickListener, RecyclerViewClickSec,BiometricCancelListner {

    private lateinit var biometricAuth : BiometricAuth
    private val AndroidKeyStore = "AndroidKeyStore"
    private val KEY_ALIAS = "SecuredKeys"
    private lateinit var keyStore: KeyStore
    private var snackbar : Snackbar? = null
    private var notesList = mutableListOf<SecretNote>()
    private var isUpdate = false
    private var updateid : Int? = null
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var bottomSheetView : View
    private lateinit var SecViewModel: SecuredViewModel
    private lateinit var sec_recyclerview : RecyclerView
    private lateinit var sec_adapter : SecuredAdapter
    private lateinit var addnote_Btn : ImageButton
    private lateinit var nav_btn : ImageButton
    private lateinit var deleteBtn : ImageButton
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var bg_constraintlayout : ConstraintLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_secured, container, false)
        initView(view)
        initViewModel()
        initGenerateKey()
        onBackPressed()
        return view
    }

    private fun initView(view : View){

        biometricAuth = BiometricAuth(this.context,this)
        if(biometricAuth.canAuthenticateWithBiometrics()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val signature = biometricAuth.tryAuth()
                if(signature!=null){
                    showBiometricPromt(signature)
                }else{
                    Log.i("T","Signature Null")
                }
            } else {
                Log.i("T","Android < M")
            }
        }else{
            Toast.makeText(this.context,"No Biometric",Toast.LENGTH_SHORT).show()
        }
        bottomSheetDialog = BottomSheetDialog(this.context!!,R.style.BottomSheetDialogTheme)
        bottomSheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        bottomSheetView = LayoutInflater.from(this.context).inflate(R.layout.secnote_bottom_sheet,view.findViewById(R.id.bottom_sheet_container_sec),false)
        //bottomSheetView = view.findViewById(R.id.bottom_sheet_include)
        bottomSheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.setOnDismissListener(dismissal)

        deleteBtn = bottomSheetView.findViewById(R.id.sec_note_delete)

        sec_recyclerview = view.findViewById(R.id.sec_recyclerview)
        addnote_Btn = view.findViewById(R.id.addnote_Btn_sec)
        nav_btn =  view.findViewById(R.id.navdrawer_secBtn)
        coordinatorLayout = view.findViewById(R.id.coordinator_layout_sec)
        bg_constraintlayout = view.findViewById(R.id.bg_sec)

        addnote_Btn.setOnClickListener {
            addNote()
        }

        nav_btn.setOnClickListener {
            (activity as NavigationDrawerInterface).opencloseDrawer()
        }

    }

    private fun showBiometricPromt(signature: Signature){

        Log.i("T","Promt")
        val authcall = biometricAuth.authenticationCallback
        val biometricPrompt = BiometricPrompt(this,biometricAuth.mainThreadExecutor,authcall)

        // Set prompt info
        val promptInfo = PromptInfo.Builder()
            .setTitle("Hidden Notes")
            .setSubtitle("Authention Required")
            .setConfirmationRequired(false)
            .setNegativeButtonText("Cancel")
            .build()

        // Show biometric prompt

        // Show biometric prompt
        if (signature != null) {
            biometricPrompt.authenticate(
                promptInfo,
                BiometricPrompt.CryptoObject(signature)
            )
        }


    }

    private fun initViewModel(){
        SecViewModel = ViewModelProvider(this).get(SecuredViewModel::class.java)
        getSecNotes()
    }

    private fun initGenerateKey(){
        keyStore = KeyStore.getInstance(AndroidKeyStore)
        keyStore.load(null)

        if(!keyStore.containsAlias(KEY_ALIAS)){
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build())
            keyGenerator.generateKey()
        }
    }

    private fun getSecNotes(){
        SecViewModel.getAllSecretNotes()?.observe(viewLifecycleOwner, Observer {
            notesList = it.toMutableList()
            addToRecyclerView()
        })
    }


    private fun getSecretKey(context: Context): SecretKey {
        val secretKey = keyStore.getEntry(KEY_ALIAS,null) as KeyStore.SecretKeyEntry
        //keyStore.getKey(KEY_ALIAS, null)
        return secretKey.secretKey
    }

    private fun addToRecyclerView(){
        sec_recyclerview.setItemViewCacheSize(20)
        sec_recyclerview.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        sec_adapter = SecuredAdapter(notesList,this,this)
        sec_adapter.setHasStableIds(true)
        sec_recyclerview.adapter = sec_adapter
        if(sec_adapter.itemCount>0) bg_constraintlayout.visibility = View.GONE
        else bg_constraintlayout.visibility = View.VISIBLE
    }

    private fun addNoteToDatabase(){
        val msg = bottomSheetView.findViewById<EditText>(R.id.edit_note_secnote)
        val title = bottomSheetView.findViewById<EditText>(R.id.edit_title_secnote)
        if(!title.text.isNullOrEmpty()){
            val pair = if(!msg.text.isNullOrEmpty()) encrypt(msg.text.toString()) else null
            val note = SecretNote(title = title.text.toString(),note = pair?.first,ivBytes = pair?.second)
            if (isUpdate){
                note.id = updateid!!
                SecViewModel.updateSecretNote(note)
            }else{
                SecViewModel.addSecretNote(note)
            }
            msg.text.clear()
            title.text.clear()
        }else{
            snackbar = Snackbar.make(coordinatorLayout,"Empty Note Deleted", Snackbar.LENGTH_SHORT)
            snackbar!!.setActionTextColor(ContextCompat.getColor(activity!!,R.color.white))
            snackbar!!.anchorView = addnote_Btn
            snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
            val view = snackbar!!.view
            view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
            snackbar!!.show()
        }
    }

    private fun addNote(){
        bottomSheetDialog.show()
    }

    override fun onItemLongClicked(view: View, position: Int): Boolean {
        return true
    }

    override fun onRecyclerViewItemClick(view: View, secretNote: SecretNote, position: Int) {

        bottomSheetView.findViewById<EditText>(R.id.edit_title_secnote).setText(secretNote.title)
        if(secretNote.note!=null)
            bottomSheetView.findViewById<EditText>(R.id.edit_note_secnote).setText(decrypt(secretNote))
        isUpdate = true
        updateid = secretNote.id
        deleteBtn.visibility = View.VISIBLE
        bottomSheetDialog.show()

        deleteBtn.setOnClickListener {
            sec_adapter.notifyItemRemoved(position)
            SecViewModel.deleteSecretNote(secretNote)
            bottomSheetDialog.dismiss()
        }

    }

    private var dismissal = DialogInterface.OnDismissListener {
        addNoteToDatabase()
        deleteBtn.visibility = View.GONE
    }

    override fun onBiometricCancel(int: Int) {
        if(int == 13){
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container_frag, HomeFragment())
                ?.commitNow()
        }else if(int == 95){
            sec_recyclerview.visibility = View.VISIBLE
        }
        else{
            snackbar = Snackbar.make(coordinatorLayout,"Authentication Failed", Snackbar.LENGTH_SHORT)
            snackbar!!.setActionTextColor(ContextCompat.getColor(activity!!,R.color.white))
            snackbar!!.anchorView = addnote_Btn
            snackbar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
            val view = snackbar!!.view
            view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.black))
            snackbar!!.show()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container_frag, HomeFragment())
                ?.commitNow()
        }
    }

    private fun encrypt(
        plaintext: String
    ): Pair<String,String> {

        val cipher: Cipher = Cipher.getInstance("AES/CBC/NoPadding")
        var temp = plaintext
        while(temp.toByteArray().size%16!=0){
            temp += "\u0020"
        }
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(this.context!!))
        val ivBytes = cipher.iv
        val encryptedBytes = cipher.doFinal(temp.toByteArray(Charsets.UTF_8))
        return Pair(Base64.encodeToString(encryptedBytes,Base64.DEFAULT),Base64.encodeToString(ivBytes,Base64.DEFAULT))
    }

    private fun decrypt(secretNote: SecretNote): String {
        val cipher: Cipher = Cipher.getInstance("AES/CBC/NoPadding")
        val decrptText = Base64.decode(secretNote.note,Base64.DEFAULT)
        val spec = IvParameterSpec(Base64.decode(secretNote.ivBytes,Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE,getSecretKey(this.context!!),spec)
        return String(cipher.doFinal(decrptText),Charsets.UTF_8).trim()
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