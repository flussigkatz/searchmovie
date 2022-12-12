package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.AbstractFilmEntity
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.FIRST_PAGE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.IS_SCROLL_FLAG
import xyz.flussigkatz.searchmovie.data.ConstantsApp.REMAINDER_OF_ELEMENTS
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SPACING_ITEM_DECORATION_IN_DP
import xyz.flussigkatz.searchmovie.databinding.FragmentNowPlayingFilmsBinding
import xyz.flussigkatz.searchmovie.util.AutoDisposable
import xyz.flussigkatz.searchmovie.util.addTo
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussigkatz.searchmovie.view.rv_adapters.SpacingItemDecoration
import xyz.flussigkatz.searchmovie.viewmodel.NowPlayingFilmsFragmentViewModel

class NowPlayingFilmsFragment : Fragment() {
    private lateinit var binding: FragmentNowPlayingFilmsBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val viewModel: NowPlayingFilmsFragmentViewModel by activityViewModels()
    private val autoDisposable = AutoDisposable()
    private var isLoadingFromApi = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNowPlayingFilmsBinding.inflate(inflater, container, false)
        autoDisposable.bindTo(lifecycle)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        viewModel.filmListData.observeOn(AndroidSchedulers.mainThread())
            .filter { !it.isNullOrEmpty() }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = { filmsAdapter.updateData(it) }
            ).addTo(autoDisposable)

        binding.nowPlayingRecycler.apply {
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
                    if (dy > IS_SCROLL_FLAG && !isLoadingFromApi) paginationCheck(
                        mLayoutManager.childCount,
                        mLayoutManager.itemCount,
                        mLayoutManager.findFirstVisibleItemPosition()
                    )
                }
            }
            addOnScrollListener(scrollListener)
        }
        viewModel.getFilms(FIRST_PAGE)
    }

    private fun paginationCheck(visibleItemCount: Int, totalItemCount: Int, pastVisibleItems: Int) {
        if (totalItemCount - (visibleItemCount + pastVisibleItems) <= REMAINDER_OF_ELEMENTS) {
            isLoadingFromApi = true
            viewModel.getFilms()
        }
    }
}