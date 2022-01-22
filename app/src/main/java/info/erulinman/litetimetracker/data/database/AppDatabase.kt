package info.erulinman.litetimetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import info.erulinman.litetimetracker.data.dao.CategoryDao
import info.erulinman.litetimetracker.data.dao.PresetDao
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Category::class, Preset::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao

    abstract fun presetDao(): PresetDao

    companion object {
        private const val DATABASE_NAME = "application.database.name"
        private const val CATEGORIES = "categories.json"
        private const val PRESETS = "presets.json"

        private var INSTANCE: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase =
            INSTANCE ?: Room
                .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(Callback(context, scope))
                .build()
                .also { INSTANCE = it }

        private class Callback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                prepopulate(CATEGORIES)
                prepopulate(PRESETS)
            }

            private fun prepopulate(fileName: String) {
                context.assets.open(fileName).use {
                    JsonReader(it.reader()).use { jsonReader ->
                        when (fileName) {
                            CATEGORIES -> {
                                val type = object : TypeToken<List<Category>>() {}.type
                                val list: List<Category> = Gson().fromJson(jsonReader, type)
                                INSTANCE?.let { database ->
                                    scope.launch(Dispatchers.IO) {
                                        database.categoryDao().insertAll(list)
                                    }
                                }
                            }
                            PRESETS -> {
                                val type = object : TypeToken<List<Preset>>() {}.type
                                val list: List<Preset> = Gson().fromJson(jsonReader, type)
                                INSTANCE?.let { database ->
                                    scope.launch(Dispatchers.IO) {
                                        database.presetDao().insertAll(list)
                                    }
                                }
                            }
                            else -> return
                        }
                    }
                }
            }
        }
    }
}