package info.erulinman.lifetimetracker.data

import android.content.Context

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.WorkManager
import androidx.work.workDataOf

import info.erulinman.lifetimetracker.utilities.DATABASE_NAME
import info.erulinman.lifetimetracker.utilities.WAT_DATA_FILENAME
import info.erulinman.lifetimetracker.workers.SeedDatabaseWorker
import info.erulinman.lifetimetracker.workers.SeedDatabaseWorker.Companion.KEY_FILENAME

@Database(entities = [Way::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wayDao(): WayDao

    companion object {

        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
                                .setInputData(workDataOf(KEY_FILENAME to WAT_DATA_FILENAME))
                                .build()
                            WorkManager.getInstance(context).enqueue(request)
                        }
                    }
                )
                .build()
    }
}