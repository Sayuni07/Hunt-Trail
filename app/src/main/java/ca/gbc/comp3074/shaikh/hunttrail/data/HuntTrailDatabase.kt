package ca.gbc.comp3074.shaikh.hunttrail.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PointOfInterest::class, Tag::class, PointTagCrossRef::class, TeamContact::class],
    version = 1,
    exportSchema = false
)
abstract class HuntTrailDatabase : RoomDatabase() {

    abstract fun pointDao(): PointDao
    abstract fun tagDao(): TagDao
    abstract fun contactDao(): ContactDao

    companion object {
        // Singleton: only one database object for the whole app
        @Volatile
        private var INSTANCE: HuntTrailDatabase? = null

        fun getInstance(context: Context): HuntTrailDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HuntTrailDatabase::class.java,
                    "hunttrail.db"
                ).build().also { INSTANCE = it }
            }
    }
}
