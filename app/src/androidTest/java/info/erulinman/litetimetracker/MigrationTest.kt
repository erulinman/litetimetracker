package info.erulinman.litetimetracker

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import info.erulinman.litetimetracker.data.database.AppDatabase
import info.erulinman.litetimetracker.data.database.MIGRATION_1_2
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        val categories = listOf(
            Category(1, 0, "C1"),
            Category(2, 1, "C2"),
            Category(3, 2, "C3")
        )
        val presets = listOf(
            Preset(1, 1, 0, "P1", 1),
            Preset(3, 1, 1, "P2", 1),
            Preset(5, 2, 1, "P3", 1),
            Preset(6, 2, 0, "P4", 1),
            Preset(7, 3, 0, "P5", 1)
        )
        helper.createDatabase(TEST_DB, 1).use { db ->
            categories.forEach {
                db.execSQL("INSERT INTO categories VALUES(${it.id}, '${it.name}')")
            }
            presets.forEach {
                db.execSQL("INSERT INTO presets VALUES(${it.id}, ${it.categoryId}, '${it.name}', ${it.time})")
            }
        }
        helper.runMigrationsAndValidate(
            TEST_DB,
            2,
            true,
            MIGRATION_1_2
        ).use { db ->
            db.query("SELECT * FROM categories").apply { moveToFirst() }.use {
                assertEquals(categories.lastIndex + 1, it.columnCount)
                assertEquals(categories.size, it.count)
            }
            db.query("SELECT * FROM presets").apply { moveToFirst() }.use {
                assertEquals(presets.lastIndex + 1, it.columnCount)
                assertEquals(presets.size, it.count)
            }
            db.execSQL("INSERT INTO categories VALUES(4, 3, 'C4')")
            db.execSQL("INSERT INTO presets VALUES(8, 4, 0, 'P6', 90000)")
        }
    }
}
