package info.erulinman.lifetimetracker.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "presets")
data class Preset(
    @PrimaryKey val id: Long,
    val categoryId: Long,
    val name: String,
    val time: Long = 90000
) : Parcelable
