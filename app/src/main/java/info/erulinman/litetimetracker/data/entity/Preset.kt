package info.erulinman.litetimetracker.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "presets")
data class Preset(
    @PrimaryKey override val id: Long,
    val categoryId: Long,
    val position: Int,
    val name: String,
    val time: Long = 90000
) : Identifiable, Parcelable
