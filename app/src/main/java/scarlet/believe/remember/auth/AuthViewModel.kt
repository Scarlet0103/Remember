package scarlet.believe.remember.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.AuthCredential


class AuthViewModel(application: Application?) : AndroidViewModel(application!!) {

    private var authRespository : AuthRepository = AuthRepository()
    lateinit var authenticatedUserLiveData : LiveData<User>
    lateinit var createdUserLiveData : LiveData<User>

    fun createUser(authenticatedUser : User){
        createdUserLiveData = authRespository.createUserInFirestoreIfNotExists(authenticatedUser)
    }

    fun signInWithGoogle(googleAuthCredential : AuthCredential){
        authenticatedUserLiveData = authRespository.firebaseSignInGoogle(googleAuthCredential)
    }

}