package info.erulinman.lifetimetracker.addNewWay

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import info.erulinman.lifetimetracker.R

const val WAY_NAME = "name"
const val TAG = "CHECKING"

class AddWayActivity : AppCompatActivity() {
    private lateinit var addWayName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_way)

        findViewById<Button>(R.id.save_button).setOnClickListener {
            Log.d(TAG, "setOnClickListener init")
            addWay()
        }
        addWayName = findViewById(R.id.editTextWayName)
    }

    private fun addWay() {
        val resultIntent = Intent()

        if (addWayName.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val name = addWayName.text.toString()
            resultIntent.putExtra(WAY_NAME, name)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}