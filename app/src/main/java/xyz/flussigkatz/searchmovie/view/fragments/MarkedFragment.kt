package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.AbstractFilmEntity
import xyz.flussigkatz.searchmovie.*
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SEARCH_DEBOUNCE_TIME_MILLISECONDS
import xyz.flussigkatz.searchmovie.databinding.FragmentMarkedBinding
import xyz.flussigkatz.searchmovie.util.AutoDisposable
import xyz.flussigkatz.searchmovie.util.Converter
import xyz.flussigkatz.searchmovie.util.addTo
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussigkatz.searchmovie.view.rv_adapters.SpacingItemDecoration
import xyz.flussigkatz.searchmovie.viewmodel.MarkedFragmentViewModel
import java.util.concurrent.TimeUnit

class MarkedFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentMarkedBinding
    private val viewModel: MarkedFragmentViewModel by activityViewModels()
    private val autoDisposable = AutoDisposable()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMarkedBinding.inflate(inflater, container, false)
        autoDisposable.bindTo(lifecycle)
        return binding.rootFragmentMarked
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initPullToRefresh()
        initSearchView()
    }

    private fun initRecycler() {
        viewModel.markedFilmListData
            .observeOn(AndroidSchedulers.mainThread())
            .filter { !it.isNullOrEmpty() }
            .map { Converter.convertToFilmUiModel(it) }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = { filmsAdapter.updateData(it) }
            ).addTo(autoDisposable)

        binding.markedRecycler.apply {
            val onItemClickListener = object : FilmListRecyclerAdapter.OnItemClickListener {
                override fun click(film: AbstractFilmEntity) {
                    val bundle = Bundle()
                    bundle.putParcelable(DETAILS_FILM_KEY, film)
                    (requireActivity() as MainActivity).navController.navigate(
                        R.id.action_markedFragment_to_detailsFragment, bundle
                    )
                }
            }
            val onCheckboxClickListener = object : FilmListRecyclerAdapter.OnCheckboxClickListener {
                override fun click(film: AbstractFilmEntity, view: CheckBox) {
                    if (view.isChecked) viewModel.addFavoriteFilmToList(film.id)
                    else viewModel.removeFavoriteFilmFromList(film.id)
                }
            }
            filmsAdapter = FilmListRecyclerAdapter(onItemClickListener, onCheckboxClickListener)
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            val decorator = SpacingItemDecoration(5)
            addItemDecoration(decorator)
        }
    }

    private fun initSearchView() {
        binding.markedSearchView.setOnCloseListener {
            binding.markedSearchView.clearFocus()
            true
        }
        Observable.create(ObservableOnSubscribe<String> { sub ->
            binding.markedSearchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query.isNullOrBlank()) sub.onNext("") else sub.onNext(query)
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText.isNullOrBlank()) sub.onNext("") else sub.onNext(newText)
                        return false
                    }

                })
        }).observeOn(AndroidSchedulers.mainThread())
            .debounce(SEARCH_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
            .map { it.lowercase().trim() }
            .flatMap {
                if (it.isNullOrBlank()) viewModel.getMarkedFilmsFromDB()
                else viewModel.getSearchedMarkedFilms(it)
            }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = { if (!binding.markedSearchView.isIconified) filmsAdapter.updateData(it) }
            ).addTo(autoDisposable)
    }

    private fun initPullToRefresh() {
        binding.markedRefresh.setOnRefreshListener {
            binding.markedSearchView.setQuery("", false)
            binding.markedSearchView.clearFocus()
            viewModel.getMarkedFilms()
            viewModel.refreshState.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onError = { Timber.d(it) },
                    onNext = { binding.markedRefresh.isRefreshing = it }
                ).addTo(autoDisposable)
        }
    }
}