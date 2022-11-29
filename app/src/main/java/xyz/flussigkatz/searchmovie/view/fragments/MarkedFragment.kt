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
import xyz.flussigkatz.core_api.entity.AbstractFilmEntity
import xyz.flussigkatz.searchmovie.*
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SEARCH_DEBOUNCE_TIME_MILLISECONDS
import xyz.flussigkatz.searchmovie.databinding.FragmentMarkedBinding
import xyz.flussigkatz.searchmovie.util.AutoDisposable
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
        return binding.rootFragmentMarked
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        autoDisposable.bindTo(lifecycle)
        initRecycler()
        initPullToRefresh()
        initSearchView()
    }

    private fun initRecycler() {
        viewModel.markedFilmListData
            .subscribeOn(Schedulers.io())
            .filter { !it.isNullOrEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ filmsAdapter.updateData(it) },
                { println("$TAG viewModel.markedFilmListData onError: ${it.localizedMessage}") })
            .addTo(autoDisposable)
        binding.markedRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: AbstractFilmEntity) {
                        val bundle = Bundle()
                        bundle.putParcelable(DetailsFragment.DETAILS_FILM_KEY, film)
                        (requireActivity() as MainActivity).navController.navigate(
                            R.id.action_markedFragment_to_detailsFragment, bundle
                        )
                    }
                }, object : FilmListRecyclerAdapter.OnCheckboxClickListener {
                    override fun click(film: AbstractFilmEntity, view: CheckBox) {
                        if (view.isChecked) viewModel.addFavoriteFilmToList(film.id)
                        else viewModel.removeFavoriteFilmFromList(film.id)
                    }
                })
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
        }).subscribeOn(Schedulers.io())
            .debounce(SEARCH_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
            .map { it.lowercase().trim() }
            .flatMap {
                if (it.isNullOrBlank()) viewModel.getMarkedFilmsFromDB()
                else viewModel.getSearchedMarkedFilms(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { println("$TAG initSearchView onError: ${it.localizedMessage}") },
                onNext = { if (!binding.markedSearchView.isIconified) filmsAdapter.updateData(it) }
            ).addTo(autoDisposable)
    }

    private fun initPullToRefresh() {
        binding.markedRefresh.setOnRefreshListener {
            binding.markedSearchView.setQuery("", false)
            binding.markedSearchView.clearFocus()
            viewModel.getMarkedFilms()
            viewModel.refreshState
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ binding.markedRefresh.isRefreshing = it },
                    { println("$TAG initPullToRefresh onError: ${it.localizedMessage}") })
                .addTo(autoDisposable)
        }
    }

    companion object {
        private const val TAG = "MarkedFragment"
    }
}