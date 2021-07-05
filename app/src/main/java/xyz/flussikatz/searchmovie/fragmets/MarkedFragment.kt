package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.flussikatz.searchmovie.*
import xyz.flussikatz.searchmovie.databinding.FragmentMarkedBinding

class MarkedFragment : Fragment() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentMarkedBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMarkedBinding.inflate(inflater, container, false)
        return binding.rootFragmentMarked
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var markedList = App.instance.filmDataBase.filter { it.fav_state }

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
                        markedList[position].fav_state = state
                        markedList = markedList.filter { it.fav_state }
                        filmsAdapter.updateData(markedList as ArrayList<Film>)
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            val decorator = TopSpasingItemDecoration(5)
            addItemDecoration(decorator)

        }

        filmsAdapter.addItems(markedList)
    }

}