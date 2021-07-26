package info.erulinman.lifetimetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presets")
data class Preset(
    @PrimaryKey val id: Long,
    val name: String,
    val time: Long = 0
)
