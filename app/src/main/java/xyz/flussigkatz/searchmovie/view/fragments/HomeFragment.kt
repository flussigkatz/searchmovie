package xyz.flussigkatz.searchmovie.view.fragments

import android.animation.ValueAnimator
import android.content.Context.WINDOW_SERVICE
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import xyz.flussigkatz.searchmovie.R.dimen.home_recycler_view_start_height
import xyz.flussigkatz.searchmovie.data.ConstantsApp.EMPTY_QUERY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.HALF_RATIO
import xyz.flussigkatz.searchmovie.data.ConstantsApp.HIDE_KEYBOARD_FLAG
import xyz.flussigkatz.searchmovie.data.ConstantsApp.LOAD_STATE_DEBOUNCE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NOW_PLAYING_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NOW_PLAYING_CATEGORY_TAB_TITLE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY_TAB_TITLE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SPACING_ITEM_DECORATION_IN_DP
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY_TAB_TITLE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.UPCOMING_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.UPCOMING_CATEGORY_TAB_TITLE
import xyz.flussigkatz.searchmovie.databinding.FragmentHomeBinding
import xyz.flussigkatz.searchmovie.util.OnQueryTextListener
import xyz.flussigkatz.searchmovie.view.rv_adapters.*
import xyz.flussigkatz.searchmovie.viewmodel.HomeFragmentViewModel

class HomeFragment : Fragment() {
    private lateinit var filmsAdapter: FilmPagingAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initPullToRefresh()
        initSearchView()
        initViewPager()
    }

    override fun onStop() {
        super.onStop()
        viewModel.setSearchQuery(EMPTY_QUERY)
    }

    private fun initRecycler() {
        binding.homeRecycler.apply {
            val onItemClickListener = OnItemClickListener { requireContext().sendBroadcast(it) }
            val onCheckboxClickListener = OnCheckboxClickListener { film, view ->
                lifecycleScope.launch {
                    view.isChecked = viewModel.changeFavoriteMark(film.id, view.isChecked)
                }
            }
            filmsAdapter = FilmPagingAdapter(onItemClickListener, onCheckboxClickListener)
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpacingItemDecoration(SPACING_ITEM_DECORATION_IN_DP))
            addOnScrollListener(OnScrollListener {
                hideSoftKeyboard(it)
                binding.root.clearFocus()
            })
        }
        lifecycleScope.launch {
            viewModel.filmFlow.collectLatest { filmsAdapter.submitData(it) }
        }
    }

    @OptIn(FlowPreview::class)
    private fun initPullToRefresh() {
        binding.homeRefresh.setOnRefreshListener {
            binding.homeSearchView.setQuery(EMPTY_QUERY, true)
            binding.homeSearchView.clearFocus()
            filmsAdapter.refresh()
        }
        filmsAdapter.loadStateFlow.debounce(LOAD_STATE_DEBOUNCE).onEach {
            binding.homeRefresh.isRefreshing = it.refresh is LoadState.Loading
        }.launchIn(lifecycleScope)
    }

    private fun initSearchView() {
        val windowManager = requireContext().getSystemService(WINDOW_SERVICE) as WindowManager
        val displayHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.run { top + bottom }
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.height
        }
        val recyclerHeightDimension =
            requireContext().resources.getDimension(home_recycler_view_start_height).toInt()
        var recyclerCollapsed = binding.homeRecycler.height <= recyclerHeightDimension
        val anim = ValueAnimator.ofInt(recyclerHeightDimension, displayHeight).apply {
            addUpdateListener {
                val layoutParams = binding.homeRecycler.layoutParams
                layoutParams.height = it.animatedValue as Int
                binding.homeRecycler.layoutParams = layoutParams
            }
            doOnEnd {
                recyclerCollapsed = binding.homeRecycler.height <= displayHeight / HALF_RATIO
            }
            duration = HOME_RECYCLER_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
        }
        with(binding.homeSearchView) {
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) hideSoftKeyboard(v)
            }
            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus && recyclerCollapsed) anim.start()
                else if (
                    query.isNullOrBlank() &&
                    !recyclerCollapsed && !hasFocus
                ) {
                    anim.reverse()
                }
            }
            setOnQueryTextListener(OnQueryTextListener { query -> viewModel.setSearchQuery(query) })
        }
    }

    private fun initViewPager() {
        binding.homeViewpager.adapter = HomeFragmentViewPagerAdapter(this)
        TabLayoutMediator(
            binding.homeTabLayout,
            binding.homeViewpager
        ) { tab, position ->
            when (position) {
                POPULAR_CATEGORY_TAB_NUMBER -> tab.text = POPULAR_CATEGORY_TAB_TITLE
                TOP_RATED_CATEGORY_TAB_NUMBER -> tab.text = TOP_RATED_CATEGORY_TAB_TITLE
                UPCOMING_CATEGORY_TAB_NUMBER -> tab.text = UPCOMING_CATEGORY_TAB_TITLE
                NOW_PLAYING_CATEGORY_TAB_NUMBER -> tab.text = NOW_PLAYING_CATEGORY_TAB_TITLE
            }
        }.attach()
    }

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(requireContext(), InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, HIDE_KEYBOARD_FLAG)
    }

    companion object {
        private const val HOME_RECYCLER_ANIMATION_DURATION = 500L
    }
}