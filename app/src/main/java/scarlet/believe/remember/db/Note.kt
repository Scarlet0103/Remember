package scarlet.believe.remember.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note(@PrimaryKey(autoGenerate = true)
                var id : Int = 0,
                var title : String? = null,
                var note : String? = null,
                var label : String? = null,
                var imageString : String? = null,
                var isArchieved : Int = 0) : Serializable

