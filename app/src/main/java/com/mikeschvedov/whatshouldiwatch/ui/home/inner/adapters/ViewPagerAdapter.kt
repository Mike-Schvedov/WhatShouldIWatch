package com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.movies.DisplayMoviesFragment
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.tvshow.DisplayTvShowsFragment


class ViewPagerAdapter(fm: FragmentManager, var tabCount: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DisplayMoviesFragment()
            1 -> DisplayTvShowsFragment()
            else -> DisplayMoviesFragment()
        }
    }

    override fun getCount(): Int {
        return tabCount
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Tab " + (position + 1)
    }
}
