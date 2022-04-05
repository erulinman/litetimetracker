package info.erulinman.litetimetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import info.erulinman.litetimetracker.base.BaseFragment
import info.erulinman.litetimetracker.databinding.ActivityMainBinding
import info.erulinman.litetimetracker.features.categories.CategoryListFragment

class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) navigateTo(CategoryListFragment(), false)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager
            .findFragmentById(R.id.mainFragmentContainer) as BaseFragment<*>
        if (fragment.onBackPressed()) super.onBackPressed()
    }

    override fun navigateTo(fragment: Fragment, addToBackStack: Boolean) {
        supportFragmentManager.commit {
            if (addToBackStack) addToBackStack(null)
            replace(R.id.mainFragmentContainer, fragment)
        }
    }

    override fun showDialog(fragment: DialogFragment, tag: String) {
        fragment.show(supportFragmentManager, tag)
    }
}