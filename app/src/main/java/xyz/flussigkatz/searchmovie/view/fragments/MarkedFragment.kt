package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.EMPTY_QUERY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.HIDE_KEYBOARD_FLAG
import xyz.flussigkatz.searchmovie.data.ConstantsApp.LOAD_STATE_DEBOUNCE
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.databinding.FragmentMarkedBinding
import xyz.flussigkatz.searchmovie.util.OnQueryTextListener
import xyz.flussigkatz.searchmovie.util.QueryAction
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmPagingAdapter
import xyz.flussigkatz.searchmovie.view.rv_adapters.SpacingItemDecoration
import xyz.flussigkatz.searchmovie.viewmodel.MarkedFragmentViewModel

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
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
            val onItemClickListener = object : FilmPagingAdapter.OnItemClickListener {
                override fun click(film: FilmUiModel) {
                    val bundle = Bundle()
                    bundle.putParcelable(DETAILS_FILM_KEY, film)
                    (requireActivity() as MainActivity).navController.navigate(
                        R.id.action_markedFragment_to_detailsFragment, bundle
                    )
                }
            }
            val onCheckboxClickListener = object : FilmPagingAdapter.OnCheckboxClickListener {
                override fun click(film: FilmUiModel, view: CheckBox) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        view.isChecked.run { viewModel.changeFavoriteMark(film.id, this) }
                    }
                }
            }
            filmsAdapter = FilmPagingAdapter(onItemClickListener, onCheckboxClickListener)
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            val decorator = SpacingItemDecoration(5)
            addItemDecoration(decorator)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.filmFlow.collectLatest {
                withContext(Dispatchers.Main) {
                    filmsAdapter.submitData(it)
                }
            }
        }
    }

    private fun initSearchView() {
        with(binding.markedSearchView) {
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) hideSoftKeyboard(v)
            }
            val queryAction: QueryAction = { query -> viewModel.setSearchQuery(query) }
            setOnQueryTextListener(OnQueryTextListener(queryAction))
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

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(requireContext(), InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, HIDE_KEYBOARD_FLAG)
    }
}