package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.doOnAttach
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.checkbox.MaterialCheckBox
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import xyz.flussigkatz.searchmovie.*
import xyz.flussigkatz.searchmovie.databinding.FragmentHomeBinding
import xyz.flussigkatz.searchmovie.data.entity.Film
import xyz.flussigkatz.searchmovie.util.AnimationHelper
import xyz.flussigkatz.searchmovie.util.AutoDisposable
import xyz.flussigkatz.searchmovie.util.addTo
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussigkatz.searchmovie.view.rv_adapters.TopSpasingItemDecoration
import xyz.flussigkatz.searchmovie.viewmodel.HomeFragmentViewModel
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeFragmentViewModel by activityViewModels()
    private val autoDisposable = AutoDisposable()
    private var filmDataBase = mutableListOf<Film>()


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

        view.doOnAttach { AnimationHelper.revealAnimation(view) }

        autoDisposable.bindTo(lifecycle)

        viewModel.filmListData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                filmDataBase.addAll(it)
                filmsAdapter.updateData(it)
            }.addTo(autoDisposable)


        initPullToRefresh()

        initEventMessage()

        initSearchView()


        binding.homeRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable(DetailsFragment.DETAILS_FILM_KEY, film)
                        AnimationHelper.coverAnimation(
                            binding.rootFragmentHome,
                            requireActivity(),
                            R.id.action_global_detailsFragment,
                            bundle
                        )
                    }
                }, object : FilmListRecyclerAdapter.OnCheckboxClickListener {
                    override fun click(film: Film, view: View) {
//                        filmsAdapter.items[position].fav_state = state
                        film.fav_state = (view as MaterialCheckBox).isChecked
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            val decorator = TopSpasingItemDecoration(5)
            addItemDecoration(decorator)
        }

        binding.homeBottomToolbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    Toast.makeText(context, "Already", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.history -> {
                    AnimationHelper.coverAnimation(
                        view,
                        requireActivity(),
                        R.id.action_global_historyFragment
                    )
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(
                        view,
                        requireActivity(),
                        R.id.action_global_markedFragment
                    )
                    true
                }
                R.id.settings -> {
                    AnimationHelper.coverAnimation(
                        view,
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

    private fun initSearchView() {
//        binding.homeSearchView.isIconifiedByDefault = false
//        binding.homeSearchView.setOnClickListener { binding.homeSearchView.isIconified = false
//            println("!!!cache")
//        }
//        binding.homeSearchView.setOnCloseListener {
//            binding.homeSearchView.clearFocus()
//            true
//        }
        //TODO: Deal with isIconified and filmDataBase
        Observable.create(ObservableOnSubscribe<String> { sub ->
            binding.homeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    sub.onNext(newText!!)
                    return false
                }

            })
        }).observeOn(Schedulers.io())
            .map { it.lowercase().trim() }
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .filter {
                it.isNotEmpty()
            }.observeOn(Schedulers.io())
            .flatMap {
                viewModel.getSearchedFilms(it)
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    println(it.localizedMessage)
                },
                onNext = {
                    filmsAdapter.updateData(it)
                }
            ).addTo(autoDisposable)
    }
}