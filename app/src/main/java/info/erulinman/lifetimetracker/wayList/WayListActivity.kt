package info.erulinman.lifetimetracker.wayList


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView
import androidx.core.view.isVisible
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import com.google.android.material.bottomappbar.BottomAppBar
import info.erulinman.lifetimetracker.R

import info.erulinman.lifetimetracker.addNewWay.AddWayActivity
import info.erulinman.lifetimetracker.addNewWay.WAY_NAME
import info.erulinman.lifetimetracker.data.Way
import info.erulinman.lifetimetracker.databinding.ActivityMainBinding
import info.erulinman.lifetimetracker.databinding.SelectionModeBottomAppBarBinding
import info.erulinman.lifetimetracker.wayDetail.WayDetailActivity


const val TAG = "CHECKING"
const val WAY_ID = "way id"

class WayListActivity : AppCompatActivity() {
    private val newWayActivityRequestCode = 1
    private val wayListViewModel by viewModels<WayListViewModel> {
        WayListViewModelFactory()
    }

    private var tracker: SelectionTracker<Long>? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val wayAdapter = WayAdapter { way -> adapterOnClick(way) }

        binding.recyclerView.adapter = wayAdapter

        wayListViewModel.wayLiveData.observe(this, {
            it?.let {
                wayAdapter.submitList(it as MutableList<Way>)
            }
        })

        binding.fab.setOnClickListener {
            fabOnClick()
        }

        tracker = SelectionTracker.Builder(
            "WayListActivity selection tracker",
            binding.recyclerView,
            StableIdKeyProvider(binding.recyclerView),
            ItemDetailsLookup(binding.recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        wayAdapter.setTracker(tracker)

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    showTheNumberOfSelectedItems()
                    setSelectionMode()
                }

                override fun onSelectionRestored() {
                    showTheNumberOfSelectedItems()
                }
            }
        )

        /*
        * Disable deselecting on touching empty recyclerview area
        *
        * I think this is a bad decision
        * TODO: searching SelectionTracker.SelectionPredicate
        */
        binding.recyclerView.setOnTouchListener { v, event ->
                Log.d(TAG, "recyclerView.setOnTouchListener")
                true
            }
    }

    private fun setSelectionMode() {
        val selectionBottomAppBar = findViewById<BottomAppBar>(R.id.selectionBottomAppBar)
        setSupportActionBar(selectionBottomAppBar)
    }
    private fun showTheNumberOfSelectedItems() {
        val nItems: Int? = tracker?.selection?.size()
        val output = "Selected: "
        if (nItems != null && nItems > 0) {
            binding.appNameOnBar.isVisible = false
            binding.counter.isVisible = true
            binding.counter.text = output + nItems
        } else {
            binding.counter.text = output + "0"
            binding.counter.isVisible = false
            binding.appNameOnBar.isVisible = true
        }
    }

    private fun adapterOnClick(way: Way) {
        val intent = Intent(this, WayDetailActivity()::class.java)
        intent.putExtra(WAY_ID, way.name)
        startActivity(intent)
    }

    private val fabOnClick = {
        val intent = Intent(this, AddWayActivity()::class.java)
        startActivityForResult(intent, newWayActivityRequestCode)
    }

    override fun onBackPressed() {
        Log.d(TAG, "Activity.onBackPressed()")
        if (tracker?.hasSelection() == true) {
            tracker?.clearSelection()
        } else {
            super.onBackPressed()
        }
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        if(savedInstanceState != null)
            tracker?.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (outState != null)
            tracker?.onSaveInstanceState(outState)
    }
}