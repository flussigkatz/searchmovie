package xyz.flussigkatz.searchmovie.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.flussigkatz.searchmovie.view.fragments.NowPlayingFilmsFragment
import xyz.flussigkatz.searchmovie.view.fragments.PopularFilmsFragment
import xyz.flussigkatz.searchmovie.view.fragments.TopRatedFilmsFragment
import xyz.flussigkatz.searchmovie.view.fragments.UpcomingFilmsFragment

class HomeFragmentViewPagerAdapter(parent: Fragment) : FragmentStateAdapter(parent) {

    override fun getItemCount() = 4

    override fun createFragment(position: Int) =  when (position) {
            0 -> PopularFilmsFragment()
            1 -> TopRatedFilmsFragment()
            2 -> UpcomingFilmsFragment()
            3 -> NowPlayingFilmsFragment()
            else -> throw IllegalArgumentException("Wrong position")
    }
}