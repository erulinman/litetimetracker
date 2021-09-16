package info.erulinman.lifetimetracker.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import info.erulinman.lifetimetracker.data.entity.Preset

/**
* Not used because data storage schema changed
*/
class Converters {
    @TypeConverter
    fun toListOfPresets(jsonData: String?): List<Preset>? {
        return jsonData?.let {
            val typeToken = object : TypeToken<List<Preset>>() {}.type
            val listOfPreset: List<Preset> = Gson().fromJson(it, typeToken)
            listOfPreset.toList()
        }
    }

    @TypeConverter
    fun toJson(presets: List<Preset>?): String? {
        return presets?.let {
            val jsonData: String? = Gson().toJson(it)
            jsonData
        }
    }
}