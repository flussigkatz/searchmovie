package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.AbstractFilmEntity
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.databinding.FragmentHistoryBinding
import xyz.flussigkatz.searchmovie.util.AutoDisposable
import xyz.flussigkatz.searchmovie.util.addTo
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussigkatz.searchmovie.view.rv_adapters.SpacingItemDecoration
import xyz.flussigkatz.searchmovie.viewmodel.HistoryFragmentViewModel

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val viewModel: HistoryFragmentViewModel by activityViewModels()
    private val autoDisposable = AutoDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        autoDisposable.bindTo(lifecycle)
        return binding.rootFragmentHistory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        viewModel.browsingFilmListData
            .observeOn(AndroidSchedulers.mainThread())
            .filter { !it.isNullOrEmpty() }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = { filmsAdapter.updateData(it) }
            ).addTo(autoDisposable)
        binding.historyRecycler.apply {
            val onItemClickListener = object : FilmListRecyclerAdapter.OnItemClickListener {
                override fun click(film: AbstractFilmEntity) {
                    val bundle = Bundle()
                    bundle.putParcelable(DETAILS_FILM_KEY, film)
                    (requireActivity() as MainActivity).navController.navigate(
                        R.id.action_historyFragment_to_detailsFragment, bundle
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
            addItemDecoration(SpacingItemDecoration(5))
        }
    }
}