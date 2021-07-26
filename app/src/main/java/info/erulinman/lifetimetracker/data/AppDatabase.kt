package info.erulinman.lifetimetracker.data

import android.content.Context
import android.util.Log

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader

import info.erulinman.lifetimetracker.utilities.DATABASE_NAME
import info.erulinman.lifetimetracker.utilities.DEBUG_TAG
import info.erulinman.lifetimetracker.utilities.POMODORO_PRESET_FILENAME
import info.erulinman.lifetimetracker.utilities.WAY_DATA_FILENAME

import kotlinx.coroutines.*

@Database(entities = [Way::class, Preset::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wayDao(): WayDao

    abstract fun presetDao(): PresetDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase =
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(Callback(context, scope))
                .build()
                .also { INSTANCE = it }

        private class Callback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                context.applicationContext.assets.open(WAY_DATA_FILENAME).use {
                    JsonReader(it.reader()).use { jsonReader ->
                        val wayType = object : TypeToken<List<Way>>() {}.type
                        val wayList: List<Way> = Gson().fromJson(jsonReader, wayType)

                        scope.launch(Dispatchers.IO) {
                            INSTANCE?.let { database ->
                                Log.d(DEBUG_TAG, "way list: $wayList")
                                prepopulateWays(wayList, database.wayDao())
                            }
                        }
                    }
                }
            }

            private suspend fun prepopulateWays(
                wayList: List<Way>,
                watDao: WayDao
            ) {
                watDao.insertAll(wayList)
            }
        }
    }
}