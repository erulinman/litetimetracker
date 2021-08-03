package info.erulinman.lifetimetracker.model

import info.erulinman.lifetimetracker.data.entity.Preset

class TimeScheduler(private val presetList: List<Preset>) {
    val currentTime: String = ""
    val currentPreset: String = ""

    fun start() {

    }

    fun pause() {

    }

    fun finish() {

    }

    private class Timer(preset: Preset) {

    }

    companion object {
        private val INSTANCE: TimeScheduler? = null
        fun getInstance(presetList: List<Preset>): TimeScheduler {
            return  INSTANCE ?: TimeScheduler(presetList)
        }
    }
}
