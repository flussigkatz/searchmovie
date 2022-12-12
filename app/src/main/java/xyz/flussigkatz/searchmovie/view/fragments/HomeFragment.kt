package xyz.flussigkatz.searchmovie.view.fragments

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import androidx.appcompat.widget.SearchView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.AbstractFilmEntity
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.R.dimen.home_recycler_view_start_height
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.FIRST_PAGE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.HIDE_KEYBOARD_FLAG
import xyz.flussigkatz.searchmovie.data.ConstantsApp.IS_SCROLL_FLAG
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NOW_PLAYING_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NOW_PLAYING_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NOW_PLAYING_CATEGORY_TAB_TITLE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY_TAB_TITLE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.REMAINDER_OF_ELEMENTS
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SEARCH_DEBOUNCE_TIME_MILLISECONDS
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SPACING_ITEM_DECORATION_IN_DP
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY_TAB_TITLE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.UPCOMING_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.UPCOMING_CATEGORY_TAB_NUMBER
import xyz.flussigkatz.searchmovie.data.ConstantsApp.UPCOMING_CATEGORY_TAB_TITLE
import xyz.flussigkatz.searchmovie.databinding.FragmentHomeBinding
import xyz.flussigkatz.searchmovie.util.AutoDisposable
import xyz.flussigkatz.searchmovie.util.addTo
import xyz.flussigkatz.searchmovie.view.HomeFragmentViewPagerAdapter
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussigkatz.searchmovie.view.rv_adapters.SpacingItemDecoration
import xyz.flussigkatz.searchmovie.viewmodel.HomeFragmentViewModel
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeFragmentViewModel by activityViewModels()
    private val autoDisposable = AutoDisposable()
    private var isLoadingFromApi = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        autoDisposable.bindTo(lifecycle)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initPullToRefresh()
        initSearchView()
        initViewPager()
    }

    private fun initRecycler() {
        viewModel.filmListData.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = { filmsAdapter.updateData(it) }
            ).addTo(autoDisposable)

        binding.homeRecycler.apply {
            filmsAdapter = FilmListRecyclerAdapter(
                object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: AbstractFilmEntity) {
                        Bundle().apply {
                            putParcelable(DETAILS_FILM_KEY, film)
                            (requireActivity() as MainActivity).navController.navigate(
                                R.id.action_homeFragment_to_detailsFragment, this
                            )
                        }
                    }
                }, object : FilmListRecyclerAdapter.OnCheckboxClickListener {
                    override fun click(film: AbstractFilmEntity, view: CheckBox) {
                        view.isChecked.let {
                            film.fav_state = it
                            if (it) viewModel.addFavoriteFilmToList(film.id)
                            else viewModel.removeFavoriteFilmFromList(film.id)
                        }
                    }
                }
            )
            adapter = filmsAdapter
            val mLayoutManager = LinearLayoutManager(context)
            layoutManager = mLayoutManager
            addItemDecoration(SpacingItemDecoration(SPACING_ITEM_DECORATION_IN_DP))
            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy != IS_SCROLL_FLAG) {
                        hideSoftKeyboard(recyclerView)
                        binding.homeSearchView.clearFocus()
                    }
                    if (dy > IS_SCROLL_FLAG && !isLoadingFromApi) paginationCheck(
                        mLayoutManager.childCount,
                        mLayoutManager.itemCount,
                        mLayoutManager.findFirstVisibleItemPosition()
                    )
                }
            }
            addOnScrollListener(scrollListener)
        }
    }

    private fun initPullToRefresh() {
        binding.homeRefresh.setOnRefreshListener {
            binding.homeSearchView.clearFocus()
            if (binding.homeSearchView.query.isNullOrBlank()) {
                when (binding.homeViewpager.currentItem) {
                    POPULAR_CATEGORY_TAB_NUMBER -> viewModel.getFilms(POPULAR_CATEGORY)
                    TOP_RATED_CATEGORY_TAB_NUMBER -> viewModel.getFilms(TOP_RATED_CATEGORY)
                    UPCOMING_CATEGORY_TAB_NUMBER -> viewModel.getFilms(UPCOMING_CATEGORY)
                    NOW_PLAYING_CATEGORY_TAB_NUMBER -> viewModel.getFilms(NOW_PLAYING_CATEGORY)
                }
            } else {
                viewModel.nextPage = FIRST_PAGE
                viewModel.getSearchedFilms(binding.homeSearchView.query.toString())
            }
            viewModel.refreshState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onError = { Timber.d(it) },
                    onNext = { binding.homeRefresh.isRefreshing = it }
                ).addTo(autoDisposable)
        }
    }

    private fun initSearchView() {
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayHeight = windowManager.currentWindowMetrics.bounds.run { top + bottom }
        val recyclerHeightDimension =
            requireContext().resources.getDimension(home_recycler_view_start_height).toInt()
        var homeRecyclerCollapsedState = binding.homeRecycler.height <= recyclerHeightDimension
        val anim = ValueAnimator.ofInt(recyclerHeightDimension, displayHeight).apply {
            addUpdateListener {
                val layoutParams = binding.homeRecycler.layoutParams
                layoutParams.height = it.animatedValue as Int
                binding.homeRecycler.layoutParams = layoutParams
            }
            doOnEnd {
                homeRecyclerCollapsedState =
                    binding.homeRecycler.height <= displayHeight / HALF_RATIO
            }
            duration = HOME_RECYCLER_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
        }
        binding.homeSearchView.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) hideSoftKeyboard(v)
        }
        binding.homeSearchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus && homeRecyclerCollapsedState) anim.start()
            else if (binding.homeSearchView.query.isNullOrBlank() &&
                !homeRecyclerCollapsedState && !hasFocus
            ) {
                anim.reverse()
            }
        }
        Observable.create(ObservableOnSubscribe<String> { sub ->
            binding.homeSearchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (binding.homeSearchView.hasFocus()) sub.onNext(query.orEmpty())
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (binding.homeSearchView.hasFocus()) sub.onNext(newText.orEmpty())
                        return true
                    }
                })
        }).debounce(SEARCH_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
            .map { it.lowercase().trim() }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = {
                    if (it.isBlank()) viewModel.clearSearchedFilmDB()
                    else viewModel.getSearchedFilms(it)
                }
            ).addTo(autoDisposable)
    }

    private fun initViewPager() {
        binding.homeViewpager.adapter = HomeFragmentViewPagerAdapter(this)
        initTabs()
    }

    private fun initTabs() {
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

    private fun paginationCheck(visibleItemCount: Int, totalItemCount: Int, pastVisibleItems: Int) {
        if (totalItemCount - (visibleItemCount + pastVisibleItems) <= REMAINDER_OF_ELEMENTS) {
            isLoadingFromApi = true
            viewModel.getSearchedFilms(binding.homeSearchView.query.toString())
        }
    }

    companion object {
        private const val HALF_RATIO = 2
        private const val HOME_RECYCLER_ANIMATION_DURATION = 500L
    }
}