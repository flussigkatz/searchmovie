package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import xyz.flussigkatz.searchmovie.*
import xyz.flussigkatz.searchmovie.databinding.FragmentMarkedBinding
import xyz.flussigkatz.searchmovie.data.entity.Film
import xyz.flussigkatz.searchmovie.util.AutoDisposable
import xyz.flussigkatz.searchmovie.util.Converter
import xyz.flussigkatz.searchmovie.util.addTo
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussigkatz.searchmovie.view.rv_adapters.TopSpasingItemDecoration
import xyz.flussigkatz.searchmovie.viewmodel.MarkedFragmentViewModel

class MarkedFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentMarkedBinding
    private val viewModel:MarkedFragmentViewModel by activityViewModels()
    private val autoDisposable = AutoDisposable()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var filmDataBase = mutableListOf<Film>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMarkedBinding.inflate(inflater, container, false)
        return binding.rootFragmentMarked
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        autoDisposable.bindTo(lifecycle)

        initPullToRefresh()

        viewModel.markedFilmListData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { Converter.convertToFilm(it) }
            .subscribe {
                filmDataBase.addAll(it)
                filmsAdapter.updateData(it)
            }.addTo(autoDisposable)

        binding.markedRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable(DetailsFragment.DETAILS_FILM_KEY, film)
                        (requireActivity() as MainActivity).navController.navigate(
                            R.id.action_markedFragment_to_detailsFragment, bundle
                        )
                    }
                }, object : FilmListRecyclerAdapter.OnCheckboxClickListener {
                    override fun click(film: Film, view: View) {
                        scope.launch {
                            viewModel.deleteMarkedFilmFromDB(film.id)
                        }
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            val decorator = TopSpasingItemDecoration(5)
            addItemDecoration(decorator)

        }
    }

    private fun initPullToRefresh() {
        binding.markedRefresh.setOnRefreshListener {
            binding.markedSearchView.clearFocus()
            viewModel.getMarkedFilms()
            viewModel.refreshState
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { binding.markedRefresh.isRefreshing = it }
                .addTo(autoDisposable)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}