package scarlet.believe.remember.splash

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import scarlet.believe.remember.auth.User
import scarlet.believe.remember.utils.Constants.Companion.USERS


class SplashRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var user : User = User()
    private val rootRef = FirebaseFirestore.getInstance()
    private val userRef =  rootRef.collection(USERS)

    fun isUserAuthenticatedInFirebase() : MutableLiveData<User>{
        val isUserAuthenticatedInFirebaseLiveData : MutableLiveData<User> = MutableLiveData()
        val firebaseuser = firebaseAuth.currentUser
        if(firebaseuser==null){
            user.isAuthenticated = false
            isUserAuthenticatedInFirebaseLiveData.value = user
        }else{
            user.uid = firebaseuser.uid
            user.isAuthenticated = true
            isUserAuthenticatedInFirebaseLiveData.value = user
        }
        return isUserAuthenticatedInFirebaseLiveData
    }

    fun getUserFromDatabase(uid : String) : MutableLiveData<User>{
        val getUserFromDatabaseLiveData : MutableLiveData<User> = MutableLiveData()
        userRef.document(uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                val document = it.result
                if(document!!.exists()){
                    val user = document.toObject(User::class.java)
                    getUserFromDatabaseLiveData.value = user
                }
            }else{
                //logErrorMessage(userTask.getException().getMessage());
            }
        }
        return getUserFromDatabaseLiveData
    }

}