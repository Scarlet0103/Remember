package scarlet.believe.remember.auth

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import scarlet.believe.remember.utils.Constants.Companion.USERS

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseFirestore.getInstance()
    private val userRef = rootRef.collection(USERS)

    fun firebaseSignInGoogle(googleAuthCredential : AuthCredential) : MutableLiveData<User> {
        val authenticatedUserMutableLiveData : MutableLiveData<User> = MutableLiveData()
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener {
            if(it.isSuccessful){
                val  isnewUser : Boolean = it.getResult()!!.additionalUserInfo!!.isNewUser
                val firebaseUser = firebaseAuth.currentUser
                if(firebaseUser!=null){
                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val user = User(uid,name!!,email!!)
                    user.isNew = isnewUser
                    authenticatedUserMutableLiveData.value = user
                }
            }else{
                //logErrorMessage(it.getException().getMessage());
            }
        }
        return authenticatedUserMutableLiveData
    }

    fun createUserInFirestoreIfNotExists(authenticatedUser : User) : MutableLiveData<User>{
        val newUserMutableLiveData : MutableLiveData<User> = MutableLiveData()
        val uidRef = userRef.document(authenticatedUser.uid!!)
        uidRef.get().addOnCompleteListener{
            if(it.isSuccessful){
                val document = it.getResult()
                if(!document!!.exists()){
                    uidRef.set(authenticatedUser).addOnCompleteListener {
                        if(it.isSuccessful){
                            authenticatedUser.isCreated = true
                            newUserMutableLiveData.value = authenticatedUser
                        }else{
                            //logErrorMessage(userCreationTask.getException().getMessage());
                        }
                    }
                }else{
                    newUserMutableLiveData.value = authenticatedUser
                }
            }else{
                    //logErrorMessage(uidTask.getException().getMessage());
            }
        }
        return newUserMutableLiveData
    }

}