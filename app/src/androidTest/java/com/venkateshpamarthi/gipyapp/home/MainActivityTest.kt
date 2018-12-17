package com.venkateshpamarthi.gipyapp.home


import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import com.venkateshpamarthi.gipyapp.R
import com.venkateshpamarthi.gipyapp.home.trending.TrendingViewHolder
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun clickFavouriteIcon() {
        IdlingRegistry.getInstance().register(mActivityTestRule.getActivity().getCountingIdlingResource())
        val card = onView(childAtPosition(childAtPosition(childAtPosition(childAtPosition(childAtPosition(childAtPosition(ViewMatchers.withId(R.id.viewpager),0), 1), 1),0),0),2))
        card.perform(click())
    }

    @Test
    fun scrollToParticularPosition() {
        IdlingRegistry.getInstance().register(mActivityTestRule.getActivity().getCountingIdlingResource())
        val recyclerView = onView(childAtPosition(childAtPosition(childAtPosition(ViewMatchers.withId(R.id.viewpager),0), 1), 1))
        recyclerView.perform(RecyclerViewActions.scrollToPosition<TrendingViewHolder>(4))
    }
    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
