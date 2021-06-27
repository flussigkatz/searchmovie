package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_marked.*
import kotlinx.android.synthetic.main.fragment_marked.marked_recycler
import xyz.flussikatz.searchmovie.*

class MarkedFragment : Fragment() {
    lateinit var filmsAdapter: FilmListRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_marked, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var markedList = App.instance.filmDataBase.filter { it.fav_state }

        AnimationHelper.revealAnimation(root_fragment_marked, requireActivity())


        marked_bottom_toolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    AnimationHelper.coverAnimation(
                        root_fragment_marked, requireActivity(),
                        R.id.action_markedFragment_to_homeFragment
                    )
                    true
                }
                R.id.history -> {
                    AnimationHelper.coverAnimation(
                        root_fragment_marked, requireActivity(),
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

        marked_recycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable("film", film)
                        AnimationHelper.coverAnimation(
                            root_fragment_marked,
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