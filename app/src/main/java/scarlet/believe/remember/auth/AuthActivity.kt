package scarlet.believe.remember.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import scarlet.believe.remember.R
import scarlet.believe.remember.home.HomeActivity
import scarlet.believe.remember.utils.Constants.Companion.RC_SIGN_IN
import scarlet.believe.remember.utils.Constants.Companion.TAG

class AuthActivity : AppCompatActivity() {

    private lateinit var gBtn : SignInButton
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_main)

        initSignInButton()
        initAuthViewModel()
        initGoogleSignInClient()

    }

    private fun initSignInButton(){
        gBtn = findViewById(R.id.gBtn)
        gBtn.setOnClickListener { signIn() }
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

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val googleSignInAccount = task.getResult(ApiException::class.java)!!
                getGoogleAuthCredentials(googleSignInAccount)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
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
                    toastMessage(it.name!!)
                }
                goToMainActivity(it)
        })
    }

    private fun goToMainActivity(user : User){
        val intent = Intent(this,HomeActivity::class.java)
        intent.putExtra("user",user)
        startActivity(intent)
        finish()

    }

    private fun toastMessage(name : String) = Toast.makeText(this, "Welcome $name",Toast.LENGTH_SHORT).show()


}
