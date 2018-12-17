package com.venkateshpamarthi.gipyapp.home

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.view.View
import android.view.ViewGroup
import com.venkateshpamarthi.gipyapp.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

fun sasds(){
    val appCompatImageView2 = Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.favouriteImageView),
            childAtPosition(
                childAtPosition(
                    ViewMatchers.withClassName(Matchers.`is`("android.support.design.card.MaterialCardView")),
                    0
                ),
                2
            ),
            ViewMatchers.isDisplayed()
        )
    )
    appCompatImageView2.perform(ViewActions.click())

    val tabView = Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withContentDescription("Favourites"),
            childAtPosition(
                childAtPosition(
                    ViewMatchers.withId(R.id.tabs),
                    0
                ),
                1
            ),
            ViewMatchers.isDisplayed()
        )
    )
    tabView.perform(ViewActions.click())

    val viewPager = Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.viewpager),
            childAtPosition(
                childAtPosition(
                    ViewMatchers.withId(android.R.id.content),
                    0
                ),
                1
            ),
            ViewMatchers.isDisplayed()
        )
    )
    viewPager.perform(ViewActions.swipeLeft())
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