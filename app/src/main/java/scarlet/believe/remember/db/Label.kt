package scarlet.believe.remember.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Label(@PrimaryKey(autoGenerate = true)
                 var id : Int = 0,
                 var labelName : String? = null) : Serializable