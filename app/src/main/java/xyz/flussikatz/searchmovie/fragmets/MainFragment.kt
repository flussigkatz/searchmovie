package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import xyz.flussikatz.searchmovie.*

class MainFragment : Fragment() {
    lateinit var filmsAdapter: FilmListRecyclerAdapter

    /*init {
        exitTransition = Slide(Gravity.START).apply { duration = 800;mode = Slide.MODE_OUT }
        reenterTransition = Slide(Gravity.START).apply { duration = 800; }
    }*/


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        TransitionManager.beginDelayedTransition(root_fragment_main, Slide().apply { duration = 3000 })
//        root_fragment_main.children.forEach { it.translationX += 10 }


        main_toolbar.setNavigationOnClickListener {
        }

        main_toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        bottom_toolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
//                    Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show()
                    (activity as MainActivity).navController.navigate(R.id.action_mainFragment_to_searchFragment)
                    true
                }
                R.id.history -> {
                    Toast.makeText(context, "History", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.marked -> {
                    (activity as MainActivity).navController.navigate(R.id.action_mainFragment_to_markedFragment)
//                    Toast.makeText(context, "Marked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }



        main_recycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable("film", film)
                        (activity as MainActivity).navController.navigate(R.id.action_mainFragment_to_detailsFragment, bundle)
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            val decorator = TopSpasingItemDecoration(5)
            addItemDecoration(decorator)

        }
        filmsAdapter.addItems(App.instance.filmDataBase)
    }


}