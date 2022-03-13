package info.erulinman.litetimetracker.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey override val id: Long,
    val position: Int,
    val name: String
) : Identifiable, Parcelable