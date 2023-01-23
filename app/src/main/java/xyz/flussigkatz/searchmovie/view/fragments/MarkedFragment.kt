package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import xyz.flussigkatz.searchmovie.data.ConstantsApp.EMPTY_QUERY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.LOAD_STATE_DEBOUNCE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SPACING_ITEM_DECORATION_IN_DP
import xyz.flussigkatz.searchmovie.databinding.FragmentMarkedBinding
import xyz.flussigkatz.searchmovie.util.OnQueryTextListener
import xyz.flussigkatz.searchmovie.view.rv_adapters.*
import xyz.flussigkatz.searchmovie.viewmodel.MarkedFragmentViewModel

class MarkedFragment : Fragment() {
    private lateinit var filmsAdapter: FilmPagingAdapter
    private lateinit var binding: FragmentMarkedBinding
    private val viewModel: MarkedFragmentViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMarkedBinding.inflate(inflater, container, false)
        return binding.rootFragmentMarked
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initPullToRefresh()
        initSearchView()
    }

    private fun initRecycler() {
        binding.markedRecycler.apply {
            val onItemClickListener = OnItemClickListener { requireContext().sendBroadcast(it) }
            val onCheckboxClickListener = OnCheckboxClickListener{ film, view ->
                lifecycleScope.launch {
                    view.isChecked = viewModel.changeFavoriteMark(film.id, view.isChecked)
                }
            }
            filmsAdapter = FilmPagingAdapter(onItemClickListener, onCheckboxClickListener)
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpacingItemDecoration(SPACING_ITEM_DECORATION_IN_DP))
            addOnScrollListener(OnScrollListener {
                binding.markedSearchView.clearFocus()
            })
        }
        lifecycleScope.launch {
            viewModel.filmFlow.collectLatest { filmsAdapter.submitData(it) }
        }
    }

    private fun initSearchView() {
        with(binding.markedSearchView) {
            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus) clearFocus()
                setOnQueryTextFocusChangeListener(null)
            }
            setOnQueryTextListener(OnQueryTextListener { query -> viewModel.setSearchQuery(query) })
        }
    }

    @OptIn(FlowPreview::class)
    private fun initPullToRefresh() {
        with(binding) {
            markedRefresh.setOnRefreshListener {
                markedSearchView.setQuery(EMPTY_QUERY, false)
                markedSearchView.clearFocus()
                viewModel.getMarkedFilmsFromApi()
                filmsAdapter.loadStateFlow.debounce(LOAD_STATE_DEBOUNCE).onEach {
                    binding.markedRefresh.isRefreshing = it.refresh is LoadState.Loading
                }.launchIn(lifecycleScope)
            }
        }
    }
}