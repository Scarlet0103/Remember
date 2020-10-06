package scarlet.believe.remember.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import scarlet.believe.remember.R
import scarlet.believe.remember.auth.AuthViewModel
import scarlet.believe.remember.auth.User
import scarlet.believe.remember.home.HomeActivity
import scarlet.believe.remember.utils.Constants
import java.util.*

class SplashActivity : AppCompatActivity() {

    private var SHARED_PREFS : String = "sharedPrefs"
    private var MY_THEME : String = "theme"

    private lateinit var splashViewModel: SplashViewModel
    private lateinit var gBtn : SignInButton
    private lateinit var logInBtn : MaterialButton
    private lateinit var progressBar : ProgressBar
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initView()
        initSplashViewModel()
        initAuthViewModel()
        initGoogleSignInClient()
        checkIfUserIsAuthenticated()

    }


    private fun initView(){
        gBtn = findViewById(R.id.gBtn)
        logInBtn = findViewById(R.id.logInBtn)
        progressBar = findViewById(R.id.splash_progress)
        sharedPref = getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE)
        initSetTheme(sharedPref.getInt(MY_THEME,3))
    }

    private fun initSetTheme(i : Int){
        when(i){
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            3 -> {
                val ctime  = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                if(ctime>=18 || ctime<6)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun initSplashViewModel(){
        splashViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
    }

    private fun initAuthViewModel(){
        authViewModel = ViewModelProvider(this).get<AuthViewModel>(AuthViewModel::class.java)
    }

    fun initGoogleSignInClient() {

        // Configure Google Sign In
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }

    private fun checkIfUserIsAuthenticated(){
        splashViewModel.checkIsUserIsAuthenticated()
        splashViewModel.isUserAuthenticated.observe(this,
            Observer {
                if(!it.isAuthenticated){
                    showSignInBtn()
                }else{
                    getUserFromDatabase(it.uid!!)
                }
            })
    }

    private fun showSignInBtn() {
        logInBtn.visibility = View.VISIBLE
        val fade_in = AnimationUtils.loadAnimation(this,R.anim.fade_in)
        logInBtn.startAnimation(fade_in)
        logInBtn.setOnClickListener {
            signIn()
            progressBar.visibility = View.VISIBLE
        }
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val googleSignInAccount = task.getResult(ApiException::class.java)!!
                getGoogleAuthCredentials(googleSignInAccount)
            } catch (e: ApiException) {
                Log.w(Constants.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun getGoogleAuthCredentials(googleSignInAccount: GoogleSignInAccount) {
        val googleTokenId = googleSignInAccount.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId,null)
        signInwithGoogleAuthCredentials(googleAuthCredential)
    }

    private fun signInwithGoogleAuthCredentials(googleAuthCredential : AuthCredential){
        authViewModel.signInWithGoogle(googleAuthCredential)
        authViewModel.authenticatedUserLiveData.observe(this,
            Observer { authenticatedUser: User ->
                if (authenticatedUser.isNew) {
                    createNewUser(authenticatedUser)
                } else {
                    goToMainActivity(authenticatedUser)
                }
            }
        )

    }

    private fun createNewUser(authenticatedUser : User){
        authViewModel.createUser(authenticatedUser)
        authViewModel.createdUserLiveData.observe(this,
            Observer {
                if(it.isCreated){
                    goToMainActivity(it)
                }
            })
    }

    private fun goToMainActivity(user : User){
        val intent = Intent(this,HomeActivity::class.java)
        intent.putExtra("user",user)
        startActivity(intent)
        finish()

    }

    private fun getUserFromDatabase(uid : String){
        splashViewModel.getUserFromDatabase(uid)
        splashViewModel.userData.observe(this,
            Observer {
                val intent = Intent(this,HomeActivity::class.java)
                intent.putExtra("user",it)
                startActivity(intent)
                finish()
        })
    }

}
