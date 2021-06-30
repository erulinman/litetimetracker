package info.erulinman.lifetimetracker.wayList


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.addNewWay.AddWayActivity
import info.erulinman.lifetimetracker.addNewWay.WAY_NAME
import info.erulinman.lifetimetracker.data.Way
import info.erulinman.lifetimetracker.wayDetail.WayDetailActivity

const val TAG = "CHECKING"
const val WAY_ID = "way id"
class WayListActivity : AppCompatActivity() {
    private val newWayActivityRequestCode = 1
    private val wayListViewModel by viewModels<WayListViewModel> {
        WayListViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wayAdapter = WayAdapter { way -> adapterOnClick(way) }
        val addWayAdapter = AddAdapter { addNewWayOnClick() }
        val concatAdapter = ConcatAdapter(wayAdapter, addWayAdapter)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = concatAdapter

        wayListViewModel.wayLiveData.observe(this, {
            it?.let {
                wayAdapter.submitList(it as MutableList<Way>)
            }
        })
    }

    private fun adapterOnClick(way: Way) {
        val intent = Intent(this, WayDetailActivity()::class.java)
        intent.putExtra(WAY_ID, way.id)
        startActivity(intent)
    }

    private fun addNewWayOnClick() {
        val intent = Intent(this, AddWayActivity()::class.java)
        startActivityForResult(intent, newWayActivityRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWayActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let {
                val name = data.getStringExtra(WAY_NAME)
                wayListViewModel.insertWay(name)
            }
        }
    }
}