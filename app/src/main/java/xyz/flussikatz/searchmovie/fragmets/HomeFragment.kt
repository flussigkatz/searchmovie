package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.film_item.*
import kotlinx.android.synthetic.main.film_item.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import xyz.flussikatz.searchmovie.*
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val filmDataBase = App.instance.filmDataBase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.revealAnimation(root_fragment_home, requireActivity())

        search_view.setOnClickListener { search_view.isIconified = false }
        //некорректно работает при нажатии на крест

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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


        home_bottom_toolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    touchCoordinates()
                    Toast.makeText(context, "Already", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.history -> {
                    AnimationHelper.coverAnimation(
                        root_fragment_home,
                        requireActivity(),
                        R.id.action_homeFragment_to_historyFragment
                    )
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(
                        root_fragment_home,
                        requireActivity(),
                        R.id.action_homeFragment_to_markedFragment
                    )
                    true
                }
                else -> false
            }
        }



        home_recycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable("film", film)
                        AnimationHelper.coverAnimation(
                            root_fragment_home,
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

    fun touchCoordinates() {
        object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                var coordinates = mutableListOf(0, 0)
                if (event?.action == MotionEvent.ACTION_UP) {
                    coordinates[0] = event.getX().toInt()
                    println(coordinates[0])
                    println(coordinates[1])
                }
                return false
            }

        }
    }

}