package xyz.flussikatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.flussikatz.searchmovie.*
import xyz.flussikatz.searchmovie.databinding.FragmentMarkedBinding
import xyz.flussikatz.searchmovie.domain.Film
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.view.rv_adapters.FilmListRecyclerAdapter
import xyz.flussikatz.searchmovie.view.rv_adapters.TopSpasingItemDecoration
import xyz.flussikatz.searchmovie.viewmodel.MarkedFragmentViewModel

class MarkedFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentMarkedBinding
    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(MarkedFragmentViewModel::class.java)
    }
    private var filmsDataBase = listOf<Film>()
        set(value) {
            if (field == value) return
            field = value
            filmsAdapter.addItems(field)
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMarkedBinding.inflate(inflater, container, false)
        return binding.rootFragmentMarked
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.filmListLiveData.observe(viewLifecycleOwner, Observer<List<Film>> {
            filmsDataBase = it
        })

        binding.markedRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable("film", film)
                        AnimationHelper.coverAnimation(
                            binding.rootFragmentMarked,
                            requireActivity(),
                            R.id.action_markedFragment_to_detailsFragment,
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

        AnimationHelper.revealAnimation(binding.rootFragmentMarked, requireActivity())

        binding.markedBottomToolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentMarked, requireActivity(),
                        R.id.action_markedFragment_to_homeFragment
                    )
                    true
                }
                R.id.history -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentMarked, requireActivity(),
                        R.id.action_markedFragment_to_historyFragment
                    )
                    true
                }
                R.id.marked -> {
                    Toast.makeText(context, "Already", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}