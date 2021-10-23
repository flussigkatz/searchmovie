package xyz.flussikatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import xyz.flussikatz.searchmovie.*
import xyz.flussikatz.searchmovie.databinding.FragmentHomeBinding
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussikatz.searchmovie.view.rv_adapters.TopSpasingItemDecoration
import xyz.flussikatz.searchmovie.viewmodel.HomeFragmentViewModel
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentHomeBinding
    private lateinit var scope: CoroutineScope
    private val viewModel: HomeFragmentViewModel by activityViewModels()
    private var filmDataBase = listOf<Film>()
        set(value) {
            if (field == value) return
            field = value
            filmsAdapter.addItems(field)
        }


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

        scope = CoroutineScope(Dispatchers.IO).also {
            it.launch {
                viewModel.filmListData.collect {
                    withContext(Dispatchers.Main) {
                        filmsAdapter.addItems(it)
                        filmDataBase = it
                    }
                }
            }
        }

        AnimationHelper.revealAnimation(binding.rootFragmentHome)

        initPullToRefresh()

        initEventMessage()


        binding.homeSearchView.setOnClickListener { binding.homeSearchView.isIconified = false }
        //TODO некорректно работает при нажатии на крест

        binding.homeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null) {
                    return true
                } else {
                    if (newText.isEmpty()) {
                        filmsAdapter.updateData(filmDataBase as ArrayList<Film>)
                    }
                    val result = filmDataBase.filter {
                        it.title.lowercase(Locale.getDefault())
                            .contains(newText.lowercase(Locale.getDefault()))
                    }
                    filmsAdapter.updateData(result as ArrayList<Film>)
                    return false
                }
            }

        })

        binding.homeRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable("film", film)
                        AnimationHelper.coverAnimation(
                            binding.rootFragmentHome,
                            requireActivity(),
                            R.id.action_global_detailsFragment,
                            bundle
                        )
                    }
                }, object : FilmListRecyclerAdapter.OnCheckedChangeListener {
                    override fun checkedChange(position: Int, state: Boolean) {
                        filmsAdapter.items[position].fav_state = state
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            val decorator = TopSpasingItemDecoration(5)
            addItemDecoration(decorator)
        }

        //TODO разобраться с устаревшим методом setOnNavigationItemSelectedListener
        binding.homeBottomToolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    Toast.makeText(context, "Already", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.history -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentHome,
                        requireActivity(),
                        R.id.action_global_historyFragment
                    )
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentHome,
                        requireActivity(),
                        R.id.action_global_markedFragment
                    )
                    true
                }
                R.id.settings -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentHome,
                        requireActivity(),
                        R.id.action_global_settingsFragment
                    )
                    true
                }
                else -> false
            }
        }

    }

    private fun initPullToRefresh() {
        binding.homeRefresh.setOnRefreshListener {
            viewModel.getFilms()
            scope.launch {
                for (element in viewModel.channelRefreshState) {
                    withContext(Dispatchers.Main) {
                        binding.homeRefresh.isRefreshing = element
                    }
                }
            }
        }
    }

    private fun initEventMessage() {
        scope.launch {
            for (element in viewModel.channelEventMessage) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, element, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

}