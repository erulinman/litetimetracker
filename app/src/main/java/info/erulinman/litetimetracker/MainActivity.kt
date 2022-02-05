package info.erulinman.litetimetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import info.erulinman.litetimetracker.databinding.ActivityMainBinding
import info.erulinman.litetimetracker.features.categories.CategoryListFragment

class MainActivity : AppCompatActivity(), Toolbar, Navigator {

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

    override fun updateToolbar(title: String, actionIconRes: Int, action: () -> Unit) {
        binding.title.text = title
        binding.btnAction.setImageResource(actionIconRes)
        binding.btnAction.setOnClickListener { action() }
    }

    override fun setActionVisibility(visibility: Boolean) {
        binding.btnAction.isVisible = visibility
    }

    override fun updateTitle(title: String) {
        binding.title.text = title
    }

    override fun updateTitle(stringResId: Int) {
        binding.title.setText(stringResId)
    }

    override fun updateTitle(visibility: Boolean) {
        binding.title.isVisible = visibility
    }

    override fun navigateTo(fragment: Fragment, addToBackStack: Boolean) {
        supportFragmentManager.commit {
            if (addToBackStack) addToBackStack(null)
            replace(R.id.mainFragmentContainer, fragment)
        }
    }

    override fun showDialog(fragment: DialogFragment) {
        fragment.show(supportFragmentManager, fragment.hashCode().toString())
    }
}