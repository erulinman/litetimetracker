package info.erulinman.lifetimetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "pomodoro_presets")
data class Preset(
    @PrimaryKey val id: Long,
    val wayId: Long,
    val name: String,
    val time: Long = 90000
) {
    // temporary data TODO: implement name/time data checker in input view
    companion object {
        const val DEFAULT_NAME = "Work"
        const val DEFAULT_TIME: Long = 1500000
    }
}
