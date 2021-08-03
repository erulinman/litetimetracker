package info.erulinman.lifetimetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ways")
data class Way(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String? = null,
)