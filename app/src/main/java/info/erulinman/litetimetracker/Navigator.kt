package info.erulinman.litetimetracker

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

interface Navigator {

    fun navigateTo(fragment: Fragment, addToBackStack: Boolean)

    fun showDialog(fragment: DialogFragment)
}