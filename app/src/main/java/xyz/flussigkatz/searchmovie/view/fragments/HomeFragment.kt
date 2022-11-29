package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import android.widget.Toast
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
import xyz.flussigkatz.searchmovie.databinding.FragmentHomeBinding
import xyz.flussigkatz.searchmovie.util.AutoDisposable
import xyz.flussigkatz.searchmovie.util.addTo
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.rootFragmentHome
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        autoDisposable.bindTo(lifecycle)
        initRecycler()
        initPullToRefresh()
        initEventMessage()
        initSearchView()
    }

    private fun initRecycler() {
        viewModel.filmListData
            .subscribeOn(Schedulers.io())
            .filter { !it.isNullOrEmpty() }
            .doOnError { println("eventMessage: ${it.localizedMessage}") }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ filmsAdapter.updateData(it) },
                { println("$TAG viewModel.filmListData onError: ${it.localizedMessage}") })
            .addTo(autoDisposable)
        binding.homeRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: AbstractFilmEntity) {
                        val bundle = Bundle()
                        bundle.putParcelable(DetailsFragment.DETAILS_FILM_KEY, film)
                        (requireActivity() as MainActivity).navController.navigate(
                            R.id.action_homeFragment_to_detailsFragment, bundle
                        )
                    }
                }, object : FilmListRecyclerAdapter.OnCheckboxClickListener {
                    override fun click(film: AbstractFilmEntity, view: CheckBox) {
                        film.fav_state = view.isChecked
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

    private fun initPullToRefresh() {
        binding.homeRefresh.setOnRefreshListener {
            binding.homeSearchView.setQuery("", false)
            binding.homeSearchView.clearFocus()
            viewModel.getFilms()
            viewModel.refreshState
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ binding.homeRefresh.isRefreshing = it },
                    { println("$TAG initPullToRefresh onError: ${it.localizedMessage}") })
                .addTo(autoDisposable)
        }
    }

    private fun initEventMessage() {
        viewModel.eventMessage
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
                { println("$TAG initEventMessage onError: ${it.localizedMessage}") })
            .addTo(autoDisposable)
    }

    private fun initSearchView() {
        binding.homeSearchView.setOnCloseListener {
            binding.homeSearchView.clearFocus()
            true
        }
        Observable.create(ObservableOnSubscribe<String> { sub ->
            binding.homeSearchView.setOnQueryTextListener(
                object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
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
                if (it.isNullOrBlank()) viewModel.getFilmsFromDB()
                else viewModel.getSearchedFilms(it)

            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { println("$TAG initSearchView onError: ${it.localizedMessage}") },
                onNext = { if (!binding.homeSearchView.isIconified) filmsAdapter.updateData(it) }
            ).addTo(autoDisposable)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}