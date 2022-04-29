package info.erulinman.litetimetracker.ui.test

import androidx.test.core.app.ActivityScenario
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import info.erulinman.litetimetracker.MainActivity
import info.erulinman.litetimetracker.ui.screen.CategoriesScreen
import io.github.kakaocup.kakao.screen.Screen.Companion.onScreen
import org.junit.Test

class CategoriesScreenTest : TestCase() {

    protected lateinit var activityScenario: ActivityScenario<MainActivity>

    @Test
    fun checkPresetCategory() =
        before {
            activityScenario = ActivityScenario.launch(MainActivity::class.java)
        }.after {
            activityScenario.close()
        }.run {
            step("first step") {
                onScreen<CategoriesScreen>
                {
                    categories {
                        firstChild<CategoriesScreen.CategoryItem> {
                            isVisible()
                            categoryName { hasText("Work") }
                        }
                    }
                }
            }
        }
}