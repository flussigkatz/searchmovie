package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.flussikatz.searchmovie.*
import xyz.flussikatz.searchmovie.databinding.FragmentHomeBinding
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val filmDataBase = App.instance.filmDataBase
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.rootFragmentHome
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.revealAnimation(binding.rootFragmentHome, requireActivity())

        binding.searchView.setOnClickListener { binding.searchView.isIconified = false }
        //некорректно работает при нажатии на крест

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null) {
                    return true
                } else {
                    if (newText!!.isEmpty()) {
                        filmsAdapter.updateData(filmDataBase as ArrayList<Film>)
                    }
                    val result = filmDataBase.filter {
                        it.title.toLowerCase(Locale.getDefault())
                            .contains(newText!!.toLowerCase(Locale.getDefault()))
                    }
                    filmsAdapter.updateData(result as ArrayList<Film>)
                    return false
                }
            }

        })


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
                        R.id.action_homeFragment_to_historyFragment
                    )
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentHome,
                        requireActivity(),
                        R.id.action_homeFragment_to_markedFragment
                    )
                    true
                }
                else -> false
            }
        }



        binding.homeRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable("film", film)
                        AnimationHelper.coverAnimation(
                            binding.rootFragmentHome,
                            requireActivity(),
                            R.id.action_homeFragment_to_detailsFragment,
                            bundle
                        )
                    }
                }, object : FilmListRecyclerAdapter.OnCheckedChangeListener {
                    override fun checkedChange(position: Int, state: Boolean) {
                        val list = filmsAdapter.items
                        list[position].fav_state = state
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            val decorator = TopSpasingItemDecoration(5)
            addItemDecoration(decorator)
        }
        filmsAdapter.addItems(filmDataBase)
    }

}