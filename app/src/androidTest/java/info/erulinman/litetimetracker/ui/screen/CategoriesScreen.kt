package info.erulinman.litetimetracker.ui.screen

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.features.categories.CategoryListFragment
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

class CategoriesScreen : KScreen<CategoriesScreen>() {

    override val layoutId: Int = R.layout.fragment_category_list
    override val viewClass: Class<*> = CategoryListFragment::class.java

    val categories = KRecyclerView(
        builder = { withId(R.id.rvCategories) },
        itemTypeBuilder = { itemType(::CategoryItem) }
    )

    internal class CategoryItem(parent: Matcher<View>) : KRecyclerItem<CategoryItem>(parent) {
        val categoryName = KTextView(parent) { withId(R.id.categoryName) }
    }
}