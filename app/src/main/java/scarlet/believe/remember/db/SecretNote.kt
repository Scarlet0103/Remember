package scarlet.believe.remember.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import javax.crypto.SecretKey

@Entity
data class SecretNote(@PrimaryKey(autoGenerate = true)
                var id : Int = 0,
                var title : String? = null,
                var note : String? = null,
                var ivBytes : String? = null) : Serializable
