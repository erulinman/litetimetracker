package info.erulinman.lifetimetracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import info.erulinman.lifetimetracker.databinding.ActivityAddWayBinding

const val NEW_WAY_NAME = "new wat name"
const val NEW_WAY_DESCRIPTION = "new wat description"

class AddWayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddWayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomAppBarLayout.appBarTitle.text = "Choose your way..."
        binding.bottomAppBarLayout.fab.apply {
            setOnClickListener { addWay() }
            setImageResource(R.drawable.ic_save_24)
        }
    }

    private fun addWay() {
        val resultIntent = Intent()

        if (binding.newWayName.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val name = binding.newWayName.text.toString()
            val description: String? = binding.newWayDescription.text?.toString()
            resultIntent.putExtra(NEW_WAY_NAME, name)
            resultIntent.putExtra(NEW_WAY_DESCRIPTION, description)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}