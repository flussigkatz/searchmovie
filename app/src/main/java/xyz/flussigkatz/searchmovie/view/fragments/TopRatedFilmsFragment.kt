package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ConstantsApp
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SPACING_ITEM_DECORATION_IN_DP
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.databinding.FragmentTopRatedFilmsBinding
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmPagingAdapter
import xyz.flussigkatz.searchmovie.view.rv_adapters.SpacingItemDecoration
import xyz.flussigkatz.searchmovie.viewmodel.TopRatedFilmsFragmentViewModel

@ExperimentalPagingApi

class TopRatedFilmsFragment : Fragment() {
    private lateinit var binding: FragmentTopRatedFilmsBinding
    private lateinit var filmsAdapter: FilmPagingAdapter
    private val viewModel: TopRatedFilmsFragmentViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopRatedFilmsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        binding.topRatedRecycler.apply {
            filmsAdapter = FilmPagingAdapter(
                object : FilmPagingAdapter.OnItemClickListener {
                    override fun click(film: FilmUiModel) {
                        Bundle().apply {
                            putParcelable(ConstantsApp.DETAILS_FILM_KEY, film)
                            (requireActivity() as MainActivity).navController.navigate(
                                R.id.action_homeFragment_to_detailsFragment, this
                            )
                        }
                    }
                }, object : FilmPagingAdapter.OnCheckboxClickListener {
                    override fun click(film: FilmUiModel, view: CheckBox) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            view.isChecked.run { viewModel.changeFavoriteMark(film.id, this) }
                        }
                    }
                }
            )
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpacingItemDecoration(SPACING_ITEM_DECORATION_IN_DP))
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.filmFlow.collectLatest {
                withContext(Dispatchers.Main) {
                    filmsAdapter.submitData(it)
                }
            }
        }
    }
}