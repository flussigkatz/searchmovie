package xyz.flussikatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import xyz.flussikatz.searchmovie.*
import xyz.flussikatz.searchmovie.databinding.FragmentHomeBinding
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.util.AutoDisposable
import xyz.flussikatz.searchmovie.util.addTo
import xyz.flussikatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussikatz.searchmovie.view.rv_adapters.TopSpasingItemDecoration
import xyz.flussikatz.searchmovie.viewmodel.HomeFragmentViewModel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeFragmentViewModel by activityViewModels()
    private val autoDisposable = AutoDisposable()
    private var filmDataBase = listOf<Film>()


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

        viewModel.filmListData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                filmDataBase = it
                filmsAdapter.addItems(it)
            }.addTo(autoDisposable)


        AnimationHelper.revealAnimation(binding.rootFragmentHome)

        initPullToRefresh()

        initEventMessage()

        initSearchView()


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
            binding.homeSearchView.clearFocus()
            viewModel.getFilms()
            viewModel.refreshState
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { binding.homeRefresh.isRefreshing = it }
                .addTo(autoDisposable)
        }
    }

    private fun initEventMessage() {
        viewModel.eventMessage
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            .addTo(autoDisposable)
    }

    fun initSearchView() {
//        binding.homeSearchView.isIconifiedByDefault = false
//        binding.homeSearchView.setOnClickListener { binding.homeSearchView.isIconified = false
//            println("!!!cache")
//        }
//        binding.homeSearchView.setOnCloseListener {
//            binding.homeSearchView.clearFocus()
//            true
//        }
        //TODO: Deal with isIconified
        Observable.create(ObservableOnSubscribe<String> { sub ->
            binding.homeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    sub.onNext(newText)
                    return false
                }

            })
        }).observeOn(Schedulers.io())
            .map { it.lowercase().trim() }
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .filter {
                if (it.isBlank()) filmsAdapter.addItems(filmDataBase)
                it.isNotEmpty()
            }.observeOn(Schedulers.io())
            .flatMap {
                viewModel.getSearchedFilms(it)
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                    println(it.localizedMessage)
                },
                onNext = {
                    filmsAdapter.addItems(it)
                }
            ).addTo(autoDisposable)
    }
}