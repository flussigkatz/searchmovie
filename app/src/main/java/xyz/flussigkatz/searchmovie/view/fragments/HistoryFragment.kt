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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.HIDE_KEYBOARD_FLAG
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SPACING_ITEM_DECORATION_IN_DP
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.databinding.FragmentHistoryBinding
import xyz.flussigkatz.searchmovie.util.OnQueryTextListener
import xyz.flussigkatz.searchmovie.util.QueryAction
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmPagingAdapter
import xyz.flussigkatz.searchmovie.view.rv_adapters.SpacingItemDecoration
import xyz.flussigkatz.searchmovie.viewmodel.HistoryFragmentViewModel

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var filmsAdapter: FilmPagingAdapter
    private val viewModel: HistoryFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initSearchView()
    }

    private fun initRecycler() {
        binding.historyRecycler.apply {
            filmsAdapter = FilmPagingAdapter(
                object : FilmPagingAdapter.OnItemClickListener {
                    override fun click(film: FilmUiModel) {
                        val bundle = Bundle()
                        bundle.putParcelable(DETAILS_FILM_KEY, film)
                        (requireActivity() as MainActivity).navController.navigate(
                            R.id.action_historyFragment_to_detailsFragment, bundle
                        )
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

    private fun initSearchView() {
        with(binding.historySearchView) {
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) hideSoftKeyboard(v)
            }
            val queryAction: QueryAction = { query -> viewModel.setSearchQuery(query) }
            setOnQueryTextListener(OnQueryTextListener(queryAction))
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(requireContext(), InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, HIDE_KEYBOARD_FLAG)
    }
}