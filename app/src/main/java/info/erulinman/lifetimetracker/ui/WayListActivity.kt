package info.erulinman.lifetimetracker.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy

import info.erulinman.lifetimetracker.adapters.WayAdapter
import info.erulinman.lifetimetracker.data.entity.Way
import info.erulinman.lifetimetracker.databinding.ActivityWayListBinding
import info.erulinman.lifetimetracker.MainApplication
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.selection.WayItemDetailsLookup
import info.erulinman.lifetimetracker.selection.WayItemKeyProvider
import info.erulinman.lifetimetracker.utilities.NEW_WAY_DESCRIPTION
import info.erulinman.lifetimetracker.utilities.NEW_WAY_NAME
import info.erulinman.lifetimetracker.utilities.WAY_ID
import info.erulinman.lifetimetracker.utilities.WAY_NAME
import info.erulinman.lifetimetracker.viewmodels.WayListViewModel
import info.erulinman.lifetimetracker.viewmodels.WayListViewModelFactory

class WayListActivity : AppCompatActivity() {
    private val newWayActivityRequestCode = 1
    private val wayListViewModel by viewModels<WayListViewModel> {
        WayListViewModelFactory((application as MainApplication).databaseRepository)
    }
    private var tracker: SelectionTracker<Long>? = null
    private lateinit var binding: ActivityWayListBinding
    private lateinit var fabOnClick: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWayListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val wayAdapter = WayAdapter { way -> adapterOnClick(way) }

        binding.recyclerView.adapter = wayAdapter
        submitUi(wayAdapter)


        binding.bottomAppBarLayout.fab.apply {
            fabOnClick = ::addNewWay
            setOnClickListener { fabOnClick() }
            setImageResource(R.drawable.baseline_add_24)
        }

        tracker = SelectionTracker.Builder(
            "WayListActivity selection tracker",
            binding.recyclerView,
            WayItemKeyProvider(wayAdapter),
            WayItemDetailsLookup(binding.recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        wayAdapter.setTracker(tracker)
        setTrackerObserver()


        /*
        * Disable deselecting on touching recyclerview`s empty area
        *
        * I think this is a bad decision
        * TODO: searching SelectionTracker.SelectionPredicate
        }*/
    }

    private fun setTrackerObserver() {
        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    showTheNumberOfSelectedItems()
                }

                override fun onSelectionRestored() {
                    showTheNumberOfSelectedItems()
                }
            }
        )
    }

    private fun submitUi(adapter: WayAdapter) {
        wayListViewModel.liveDataWays.observe(this, {
            it?.let { adapter.submitList(it) }
        })
    }

    private fun showTheNumberOfSelectedItems() {
        val nItems: Int? = tracker?.selection?.size()
        val counterText = "Selected: "
        if (nItems != null && nItems > 0) {
            binding.bottomAppBarLayout.appBarTitle.text = counterText + nItems
            binding.bottomAppBarLayout.fab.setImageResource(R.drawable.ic_delete_24)
            fabOnClick = ::deleteSelectedWays
        } else {
            binding.bottomAppBarLayout.appBarTitle.setText(R.string.app_name)
            binding.bottomAppBarLayout.fab.setImageResource(R.drawable.baseline_add_24)
            fabOnClick = ::addNewWay
        }
    }

    private fun addNewWay() {
        val intent = Intent(this, AddWayActivity::class.java)
        startActivityForResult(intent, newWayActivityRequestCode)
    }

    private fun deleteSelectedWays() {
        tracker?.selection?.let {
            wayListViewModel.deleteSelectedWays(it.toList())
        }
    }

    override fun onBackPressed() {
        if (tracker?.hasSelection() == true) {
            tracker?.clearSelection()
        } else {
            super.onBackPressed()
        }
    }

    private fun adapterOnClick(way: Way) {
        val intent = Intent(this, PresetActivity::class.java)
        intent.putExtra(WAY_ID, way.id)
        intent.putExtra(WAY_NAME, way.name)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWayActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let {
                val name: String = data.getStringExtra(NEW_WAY_NAME) ?: return
                    val description = data.getStringExtra(NEW_WAY_DESCRIPTION)
                    wayListViewModel.addNewWay(name, description)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        tracker?.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        tracker?.onSaveInstanceState(outState)
    }
}