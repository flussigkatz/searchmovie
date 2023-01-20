package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SPACING_ITEM_DECORATION_IN_DP
import xyz.flussigkatz.searchmovie.databinding.FragmentUpcomingFilmsBinding
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmPagingAdapter
import xyz.flussigkatz.searchmovie.view.rv_adapters.OnCheckboxClickListener
import xyz.flussigkatz.searchmovie.view.rv_adapters.OnItemClickListener
import xyz.flussigkatz.searchmovie.view.rv_adapters.SpacingItemDecoration
import xyz.flussigkatz.searchmovie.viewmodel.UpcomingFilmsFragmentViewModel

class UpcomingFilmsFragment : Fragment() {
    private lateinit var binding: FragmentUpcomingFilmsBinding
    private lateinit var filmsAdapter: FilmPagingAdapter
    private val viewModel: UpcomingFilmsFragmentViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpcomingFilmsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        binding.upcomingRecycler.apply {
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
        }
        lifecycleScope.launch {
            viewModel.filmFlow.collectLatest { filmsAdapter.submitData(it) }
        }
    }
}