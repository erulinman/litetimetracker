package info.erulinman.lifetimetracker.addNewWay

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import info.erulinman.lifetimetracker.databinding.ActivityAddWayBinding


const val WAY_NAME = "name"

class AddWayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddWayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWayBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.saveButton.setOnClickListener {
            addWay()
        }
    }

    private fun addWay() {
        val resultIntent = Intent()

        if (binding.editTextWayName.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val name = binding.editTextWayName.text.toString()
            resultIntent.putExtra(WAY_NAME, name)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}