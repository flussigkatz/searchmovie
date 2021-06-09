package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_marked.*
import xyz.flussikatz.searchmovie.*
import java.util.function.Predicate

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
        marked_recycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable("film", film)
                        (requireActivity() as MainActivity).navController.navigate(
                            R.id.action_markedFragment_to_detailsFragment,
                            bundle
                        )
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(context)
            val decorator = TopSpasingItemDecoration(5)
            addItemDecoration(decorator)

        }
        val markedList = App.instance.filmDataBase.filter { it.fav_state }
        filmsAdapter.addItems(markedList)
    }

}