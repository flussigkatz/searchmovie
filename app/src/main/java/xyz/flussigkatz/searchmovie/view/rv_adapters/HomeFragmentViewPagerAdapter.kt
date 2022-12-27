package xyz.flussigkatz.searchmovie.view.rv_adapters

import androidx.fragment.app.Fragment
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.flussigkatz.searchmovie.data.ConstantsApp.HOME_VIEW_PAGER_TAB_COUNT
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NOW_PLAYING_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.UPCOMING_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.view.fragments.NowPlayingFilmsFragment
import xyz.flussigkatz.searchmovie.view.fragments.PopularFilmsFragment
import xyz.flussigkatz.searchmovie.view.fragments.TopRatedFilmsFragment
import xyz.flussigkatz.searchmovie.view.fragments.UpcomingFilmsFragment

@ExperimentalPagingApi
class HomeFragmentViewPagerAdapter(parent: Fragment) : FragmentStateAdapter(parent) {

    override fun getItemCount() = HOME_VIEW_PAGER_TAB_COUNT

    override fun createFragment(position: Int) = when (position) {
        POPULAR_CATEGORY_TAB_NUMBER -> PopularFilmsFragment()
        TOP_RATED_CATEGORY_TAB_NUMBER -> TopRatedFilmsFragment()
        UPCOMING_CATEGORY_TAB_NUMBER -> UpcomingFilmsFragment()
        NOW_PLAYING_CATEGORY_TAB_NUMBER -> NowPlayingFilmsFragment()
        else -> throw IllegalArgumentException(EXCEPTION_MESSAGE)
    }

    companion object {
        private const val EXCEPTION_MESSAGE = "Wrong position"
    }
}