package scarlet.believe.remember.auth

import com.google.firebase.firestore.Exclude
import java.io.Serializable


//class User(val uid : String,val name : String,val email : String) : Serializable {
//    @Exclude
//    var isAuthenticated = false
//    @Exclude
//    var isNew = false
//    @Exclude
//    var isCreated = false
//
//
//}

class User : Serializable {
    var uid: String? = null
    var name: String? = null
    var email: String? = null
    @Exclude
    var isAuthenticated = false
    @Exclude
    var isNew = false
    @Exclude
    var isCreated = false

    constructor() {}
    internal constructor(uid: String?, name: String?, email: String?) {
        this.uid = uid
        this.name = name
        this.email = email
    }
}