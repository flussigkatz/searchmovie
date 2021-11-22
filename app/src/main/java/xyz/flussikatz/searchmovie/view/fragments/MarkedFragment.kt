package xyz.flussikatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.doOnAttach
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import xyz.flussikatz.searchmovie.*
import xyz.flussikatz.searchmovie.databinding.FragmentMarkedBinding
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.util.AutoDisposable
import xyz.flussikatz.searchmovie.util.addTo
import xyz.flussikatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussikatz.searchmovie.view.rv_adapters.TopSpasingItemDecoration
import xyz.flussikatz.searchmovie.viewmodel.MarkedFragmentViewModel
import java.util.concurrent.TimeUnit

class MarkedFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentMarkedBinding
    private val viewModel:MarkedFragmentViewModel by activityViewModels()
    private val autoDisposable = AutoDisposable()
    private var filmDataBase = listOf<Film>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMarkedBinding.inflate(inflater, container, false)
        return binding.rootFragmentMarked
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnAttach { AnimationHelper.revealAnimation(view) }

        autoDisposable.bindTo(lifecycle)

        viewModel.favoriteFilmListData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                filmDataBase = it
                filmsAdapter.addItems(it)
            }.addTo(autoDisposable)

        binding.markedRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable("film", film)
                        AnimationHelper.coverAnimation(
                            binding.rootFragmentMarked,
                            requireActivity(),
                            R.id.action_global_detailsFragment,
                            bundle
                        )
                    }
                }, object : FilmListRecyclerAdapter.OnCheckedChangeListener {
                    override fun checkedChange(position: Int, state: Boolean) {
                        filmsAdapter.items[position].fav_state = state
                        val list = filmsAdapter.items.filter { it.fav_state } as ArrayList<Film>
                        filmsAdapter.updateData(list)
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            val decorator = TopSpasingItemDecoration(5)
            addItemDecoration(decorator)

        }

        binding.markedBottomToolbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    AnimationHelper.coverAnimation(
                        view,
                        requireActivity(),
                        R.id.action_global_homeFragment
                    )
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
                    Toast.makeText(context, "Already", Toast.LENGTH_SHORT).show()
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

    /*private fun initSearchView() {
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
            binding.markedSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                if (it.isNullOrBlank()) filmsAdapter.addItems(filmDataBase)
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
                    filmsAdapter.addItems(it)
                }
            ).addTo(autoDisposable)
    }*/
}