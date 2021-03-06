package info.erulinman.litetimetracker.utils

import android.content.Context
import android.content.Intent

class ActionIntent(context: Context, clazz: Class<*>, _action: String) : Intent(context, clazz) {
    init {
        this.action = _action
    }
}