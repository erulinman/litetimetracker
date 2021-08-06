package info.erulinman.lifetimetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_presets")
data class Preset(
    @PrimaryKey val id: Long,
    val wayId: Long,
    val name: String,
    val time: Long = 90000
) {
    companion object {
        const val DEFAULT_NAME = "Work"
        const val DEFAULT_TIME = "1500000"
    }
}
