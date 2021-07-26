package info.erulinman.lifetimetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "ways")
@TypeConverters(Converters::class)
data class Way(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String? = null,
    val scenario: List<Preset>? = null
)