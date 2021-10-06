package info.erulinman.lifetimetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import info.erulinman.lifetimetracker.data.dao.CategoryDao
import info.erulinman.lifetimetracker.data.dao.PresetDao
import info.erulinman.lifetimetracker.data.entity.Category
import info.erulinman.lifetimetracker.data.entity.Preset
import kotlinx.coroutines.*

@Database(entities = [Category::class, Preset::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao

    abstract fun presetDao(): PresetDao

    companion object {
        private const val DATABASE_NAME = "application.database.name"
        private const val CATEGORY_DATA_FILENAME = "categories.json"

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
                context.applicationContext.assets.open(CATEGORY_DATA_FILENAME).use {
                    JsonReader(it.reader()).use { jsonReader ->
                        val categoryType = object : TypeToken<List<Category>>() {}.type
                        val categoryList: List<Category> = Gson().fromJson(jsonReader, categoryType)

                        scope.launch(Dispatchers.IO) {
                            INSTANCE?.let { database ->
                                prepopulateCategories(categoryList, database.categoryDao())
                            }
                        }
                    }
                }
            }

            private suspend fun prepopulateCategories(
                categoryList: List<Category>,
                categoryDao: CategoryDao
            ) {
                categoryDao.insertAll(categoryList)
            }
        }
    }
}