package xyz.flussikatz.searchmovie


import android.view.View
import android.widget.CheckBox
import androidx.appcompat.widget.SearchView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

//    @Test
    fun recyclerViewIsAttached() {
        onView(withId(R.id.home_recycler)).check(matches(isDisplayed()))
        onView(withId(R.id.home_recycler))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<FilmListRecyclerAdapter.FilmViewHolder>(
                    0,
                    click()))
    }

//    @Test
    fun inputTestInSearchView() {
        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
        onView(withId(R.id.search_view)).perform(typeTextSearchView("Some text"))
    }

//    @Test
    fun listingBottomMenu() {
        onView(withId(R.id.home_page)).perform(click())
        onView(withId(R.id.root_fragment_home)).check(matches(isDisplayed()))

        onView(withId(R.id.history)).perform(click())
        onView(withId(R.id.root_fragment_history)).check(matches(isDisplayed()))

        onView(withId(R.id.marked)).perform(click())
        onView(withId(R.id.root_fragment_marked)).check(matches(isDisplayed()))

    }

    @Test
    fun openedDetailFragment() {

        onView(withId(R.id.home_recycler))
            .perform(RecyclerViewActions
                .scrollToPosition<FilmListRecyclerAdapter.FilmViewHolder>(5),
                RecyclerViewActions.actionOnItemAtPosition<FilmListRecyclerAdapter.FilmViewHolder>(
                    4,
                    click()))
        onView(withId(R.id.root_fragment_details)).check(matches(isDisplayed()))
    }

//    @Test
    fun clickToFavorite() {
        onView(withId(R.id.home_recycler))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<FilmListRecyclerAdapter.FilmViewHolder>(
                    0,
                    clickToFavCheckBox(R.id.favorite_check_box)), )
    }

    private fun clickToFavCheckBox(id: Int) : ViewAction{
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(isDisplayed(), isAssignableFrom(CheckBox::class.java))
            }

            override fun getDescription(): String {
                return "Click to favorite checkbox."
            }

            override fun perform(uiController: UiController?, view: View?) {
                val v = view?.findViewById<CheckBox>(R.id.favorite_check_box)
                v?.performClick()
                v?.performClick()
            }

        }
    }


    private fun typeTextSearchView(text: String?) : ViewAction?{
        return object : ViewAction{
            override fun getConstraints(): Matcher<View> {
                return allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))
            }

            override fun getDescription(): String {
                return "Change text in SearchView"
            }

            override fun perform(uiController: UiController?, view: View?) {
                (view as SearchView).setQuery(text, false)
            }

        }

    }

}