package info.erulinman.litetimetracker.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        val categories = mutableListOf<Category>()
        val presets = mutableListOf<Preset>()
        database.query("SELECT * FROM categories ORDER BY id").use { row ->
            row.moveToFirst()
            var position = 0
            while (!row.isAfterLast) {
                val category = Category(
                    row.getLong(0),
                    position,
                    row.getString(1)
                )
                categories.add(category)
                position++
                row.moveToNext()
            }
        }
        database.execSQL("DROP TABLE categories")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS categories ('id' INTEGER NOT NULL, " +
                    "'position' INTEGER NOT NULL, 'name' TEXT NOT NULL, PRIMARY KEY(`id`))"
        )
        categories.forEach { category ->
            database.execSQL("INSERT INTO categories VALUES(${category.id}, ${category.position}, '${category.name}')")
        }
        database.query("SELECT * FROM presets ORDER BY id").use { row ->
            row.moveToFirst()
            var position = 0
            var categoryId = row.getLong(1)
            while (!row.isAfterLast) {
                if (categoryId != row.getLong(1)) position = 0
                categoryId = row.getLong(1)
                val preset = Preset(
                    row.getLong(0),
                    row.getLong(1),
                    position,
                    row.getString(2),
                    row.getLong(3)
                )
                presets.add(preset)
                position++
                row.moveToNext()
            }
        }
        database.execSQL("DROP TABLE presets")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS presets ('id' INTEGER NOT NULL, " +
                    "'categoryId' INTEGER NOT NULL, 'position' INTEGER NOT NULL, " +
                    "'name' TEXT NOT NULL, 'time' INTEGER NOT NULL, PRIMARY KEY('id'))"
        )
        presets.forEach { preset ->
            database.execSQL(
                "INSERT INTO presets VALUES(${preset.id}, ${preset.categoryId}," +
                        " ${preset.position}, '${preset.name}', ${preset.time})"
            )
        }
    }
}
