package scarlet.believe.remember.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [SecretNote::class],
    version = 5
)
abstract class SecretNoteDatabase : RoomDatabase() {

    abstract fun getSecretNoteDao() : SecretNoteDao

    companion object{

        @Volatile
        private var instance : SecretNoteDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,SecretNoteDatabase::class.java,"secretnotedatabase"
        ).fallbackToDestructiveMigration().build()
    }
}