package scarlet.believe.remember.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SecretNoteDao {

    @Insert
    fun addSecretNote(secretNote: SecretNote)

    @Query("SELECT * FROM SecretNote ORDER BY id DESC")
    fun getAllSecretNotes() : LiveData<List<SecretNote>>

    @Update
    fun updateSecretNote(secretNote: SecretNote)

    @Delete
    fun deleteSecretNote(secretNote: SecretNote)

}