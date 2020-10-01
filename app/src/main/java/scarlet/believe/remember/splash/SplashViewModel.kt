package scarlet.believe.remember.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import scarlet.believe.remember.auth.User

class SplashViewModel(application: Application?) : AndroidViewModel(application!!) {

    private var splashRepository = SplashRepository()
    lateinit var isUserAuthenticated : LiveData<User>
    lateinit var userData : LiveData<User>

    fun checkIsUserIsAuthenticated(){
        isUserAuthenticated = splashRepository.isUserAuthenticatedInFirebase()
    }

    fun getUserFromDatabase(uid : String){
        userData = splashRepository.getUserFromDatabase(uid)
    }

}