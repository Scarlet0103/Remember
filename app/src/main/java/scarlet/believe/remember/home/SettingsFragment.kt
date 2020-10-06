package scarlet.believe.remember.home

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import de.cketti.mailto.EmailIntentBuilder
import scarlet.believe.remember.R
import scarlet.believe.remember.splash.SplashActivity
import scarlet.believe.remember.utils.NavigationDrawerInterface
import scarlet.believe.remember.utils.SelectMenuItemNav
import java.util.*


class SettingsFragment : Fragment() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val policyUrl = Uri.parse("https://docs.google.com/document/d/16L_iDJZvRv3L-feyFzs5XfBY0N3VD-aJakrbXD9clwU/edit?usp=sharing")
    private val termsUrl = Uri.parse("https://docs.google.com/document/d/1Ins1LCXjy19qrQkn9V09PvBWkeRCOG97Qhuj7ZIlxxY/edit?usp=sharing")
    private val subjectEmail = "Remember FeedBack - ${firebaseAuth.currentUser?.uid}"
    private val emailDev = "scarlet.erza.0103@gmail.com"
    private var SHARED_PREFS : String = "sharedPrefs"
    private var MY_THEME : String = "theme"
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var bottomSheetView : View
    private lateinit var nav_btn : ImageButton
//    private lateinit var logout_Btn : MaterialButton
    private lateinit var themeTxt : TextView
    private lateinit var policyTxt : TextView
    private lateinit var termsTxt : TextView
    private lateinit var feedbackTxt : TextView
    private lateinit var sharedPref : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        initView(view)
        onBackPressed()
        return view
    }

    private fun initView(view: View){
        (activity as SelectMenuItemNav).selectMenuItem(95)

        bottomSheetDialog = BottomSheetDialog(this.context!!,R.style.BottomSheetDialogTheme)
        bottomSheetView = LayoutInflater.from(this.context).inflate(R.layout.bottomsheet_settings, view.findViewById(R.id.bottom_sheet_container_setting))
        bottomSheetDialog.setContentView(bottomSheetView)

        nav_btn = view.findViewById(R.id.navdrawer_settingBtn)
//        logout_Btn = view.findViewById(R.id.logOutBtn)
        themeTxt = view.findViewById(R.id.theme_setting)
        policyTxt = view.findViewById(R.id.policy_setting)
        termsTxt = view.findViewById(R.id.terms_setting)
        feedbackTxt = view.findViewById(R.id.feedback_setting)
        sharedPref = activity!!.getSharedPreferences(SHARED_PREFS,AppCompatActivity.MODE_PRIVATE)
        editor = sharedPref.edit()

        themeTxt.setOnClickListener {
            bottomSheetDialog.show()
            selectTheme()
        }

        policyTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,policyUrl)
            startActivity(intent)
        }

        termsTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,termsUrl)
            startActivity(intent)
        }

        feedbackTxt.setOnClickListener {
            val intent = EmailIntentBuilder.from(this.context!!)
                .to(emailDev)
                .subject(subjectEmail)
                .build()
            startActivity(intent)
        }

//        logout_Btn.setOnClickListener {
//            val gso = GoogleSignInOptions
//                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//
//            val googleSignInClient = GoogleSignIn.getClient(activity!!,gso)
//            googleSignInClient.signOut()
//            firebaseAuth.signOut()
//        }

        nav_btn.setOnClickListener {
            (activity as NavigationDrawerInterface).opencloseDrawer()
        }
    }

    private fun selectTheme(){

        bottomSheetDialog.findViewById<TextView>(R.id.lighttheme_setting)?.setOnClickListener {
            bottomSheetDialog.dismiss()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putInt(MY_THEME,1).apply()
        }

        bottomSheetDialog.findViewById<TextView>(R.id.darktheme_setting)?.setOnClickListener {
            bottomSheetDialog.dismiss()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putInt(MY_THEME,2).apply()
        }

        bottomSheetDialog.findViewById<TextView>(R.id.autotheme_setting)?.setOnClickListener {
            bottomSheetDialog.dismiss()
            val ctime  = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            editor.putInt(MY_THEME,3).apply()
            if(ctime>=18 || ctime<6)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

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