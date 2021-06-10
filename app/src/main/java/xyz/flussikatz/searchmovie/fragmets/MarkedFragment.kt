package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_marked.*
import xyz.flussikatz.searchmovie.*
import java.util.function.Predicate

class MarkedFragment : Fragment() {
    lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val animDuration = 100L

    init {
        enterTransition = Fade().apply {
            mode = Fade.MODE_IN
            duration = animDuration
            interpolator = LinearInterpolator()

        }

        returnTransition = Fade().apply {
            mode = Fade.MODE_OUT
            duration = animDuration
            interpolator = LinearInterpolator()

        }

        exitTransition = Fade().apply {
            mode = Fade.MODE_OUT
            duration = animDuration
            interpolator = LinearInterpolator()

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_marked, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var markedList = App.instance.filmDataBase.filter { it.fav_state }

        marked_recycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable("film", film)
                        (requireActivity() as MainActivity).navController.navigate(R.id.action_markedFragment_to_detailsFragment,bundle)
                    }
                }, object : FilmListRecyclerAdapter.OnCheckedChangeListener{
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