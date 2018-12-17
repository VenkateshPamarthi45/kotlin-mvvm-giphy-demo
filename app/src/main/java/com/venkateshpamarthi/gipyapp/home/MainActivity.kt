package com.venkateshpamarthi.gipyapp.home

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.venkateshpamarthi.gipyapp.R
import com.venkateshpamarthi.gipyapp.home.favourites.FavouriteListingFragment
import com.venkateshpamarthi.gipyapp.home.trending.TrendingListingFragment
import android.support.test.espresso.idling.CountingIdlingResource




class MainActivity : AppCompatActivity(){

    private lateinit var toolbar:Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var isSearchCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        viewPager = findViewById(R.id.viewpager)
        setupViewPager()

        tabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
    }


    /**
     * Viewpager adapter is set in this method
     * adding trending fragment and favourite fragment to adapter
     * and setting view pager adapter with added fragments
     *
     */
    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(TrendingListingFragment.newInstance(),getString(R.string.tab_title_trending))
        viewPagerAdapter.addFragment(FavouriteListingFragment.newInstance(),getString(R.string.tab_title_favourites))
        viewPager.adapter = viewPagerAdapter
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.fragment_menu, menu)
        val searchItem = menu?.findItem(R.id.search)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchMenuItemCloseActionHandling(searchItem!!)
        searchMenuItemQueryListener(searchView)
        return true
    }

    /**
     * This method helps to set search query listener
     * set view pager current item to zero index if user is on favourites fragment
     * getting instance of trending fragment from adapter and calling method
     *
     *  @param searchView Search view object to set listener
     */
    private fun searchMenuItemQueryListener(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewPager.setCurrentItem(0,true)
                val trendingListingFragment : TrendingListingFragment = viewPagerAdapter.getItem(0) as TrendingListingFragment
                trendingListingFragment.onSearchQuery(query)
                isSearchCalled = true
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    /**
     * This method helps to detect of search close
     * using this we show trending giphs back to user replacing searched items
     * getting instance of trending fragment from adapter and calling particular method
     *
     *  @param searchItem Search item object to set listener
     */
    private fun searchMenuItemCloseActionHandling(searchItem: MenuItem){
        MenuItemCompat.setOnActionExpandListener(searchItem, object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                if(isSearchCalled){
                    viewPager.setCurrentItem(0,true)
                    val trendingListingFragment : TrendingListingFragment = viewPagerAdapter.getItem(0) as TrendingListingFragment
                    trendingListingFragment.onSearchCanceled()
                    isSearchCalled = false
                }
                return true
            }
        })
    }

    /**
     * This method helps to get [CountingIdlingResource]
     * from [TrendingListingFragment] for testing
     */
    fun getCountingIdlingResource(): CountingIdlingResource {
        return TrendingListingFragment.getCountingIdlingResource()
    }

    /**
     * This class extends [FragmentPagerAdapter]
     * adding trending and favourite fragments to [getSupportFragmentManager]
     *
     *  @param fm Fragment Manager
     */
    class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        private val mFragmentList = mutableListOf<Fragment>()
        private val mFragmentTitleList = mutableListOf<String>()

        fun addFragment(fragment: Fragment, title:String){
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }


        override fun getItem(p0: Int): Fragment {
            return mFragmentList[p0]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

    }
}
