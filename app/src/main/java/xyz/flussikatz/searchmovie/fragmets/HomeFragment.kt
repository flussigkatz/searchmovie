package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import android.transition.Fade
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import xyz.flussikatz.searchmovie.*
import java.time.chrono.MinguoChronology
import xyz.flussikatz.searchmovie.fragmets.MarkedFragment as MarkedFragment


class HomeFragment : Fragment() {
    lateinit var filmsAdapter: FilmListRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.reveaAnimationAppere(root_fragment_home, requireActivity())

        val frag = this


        home_toolbar.setNavigationOnClickListener {
        }

        home_toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        home_bottom_toolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    Toast.makeText(context, "Already", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.history -> {
                    Toast.makeText(context, "History", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.marked -> {
                    AnimationHelper.reveaAnimationDisappere(root_fragment_home, requireActivity(), R.id.action_homeFragment_to_markedFragment)
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
                        AnimationHelper.reveaAnimationDisappere(root_fragment_home, requireActivity(), R.id.action_homeFragment_to_detailsFragment, bundle)
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
        filmsAdapter.addItems(App.instance.filmDataBase)
    }

}