package xyz.flussikatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.flussikatz.searchmovie.*
import xyz.flussikatz.searchmovie.databinding.FragmentHomeBinding
import xyz.flussikatz.searchmovie.domain.Film
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussikatz.searchmovie.view.rv_adapters.TopSpasingItemDecoration
import xyz.flussikatz.searchmovie.viewmodel.HomeFragmentViewModel
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(HomeFragmentViewModel::class.java)
    }
    private var filmDataBase = listOf<Film>()
        set(value) {
            if (field == value) return
            field = value
            filmsAdapter.addItems(field)
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.rootFragmentHome
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.filmListLiveData.observe(viewLifecycleOwner, Observer<List<Film>> {
            filmDataBase = it
            filmsAdapter.addItems(it)
        })

        AnimationHelper.revealAnimation(binding.rootFragmentHome, requireActivity())

        initPullToRefresh()

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
            filmsAdapter.items.clear()
            viewModel.getFilms()
            binding.homeRefresh.isRefreshing = false
        }
    }
}