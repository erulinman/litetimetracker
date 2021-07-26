package info.erulinman.lifetimetracker.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun toListOfPresets(jsonData: String?): List<Preset>? {
        return jsonData?.let { jsonData ->
            val typeToken = object : TypeToken<List<Preset>>() {}.type
            val listOfPreset: List<Preset> = Gson().fromJson(jsonData, typeToken)
            listOfPreset.toList()
        }
    }

    @TypeConverter
    fun toJson(presets: List<Preset>?): String? {
        return presets?.let { listOfPreset ->
            val jsonData: String? = Gson().toJson(listOfPreset)
            jsonData
        }
    }
}