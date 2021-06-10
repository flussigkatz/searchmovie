package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import android.transition.Scene
import android.transition.Slide
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.merge_search_fragment_content.*
import xyz.flussikatz.searchmovie.*
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {
    lateinit var filmsAdapter: FilmListRecyclerAdapter
    val filmDataBase = App.instance.filmDataBase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val scene = Scene.getSceneForLayout(root_fragment_search, R.layout.merge_search_fragment_content, requireContext())
        val searchSlide = Slide(Gravity.TOP).addTarget(R.id.search_view)
        val recyclerSlide = Slide(Gravity.BOTTOM).addTarget(R.id.search_recycler)
        val customTransition = TransitionSet().apply {
            duration = 400
            addTransition(recyclerSlide)
            addTransition(searchSlide)
        }
        TransitionManager.go(scene, customTransition)



        search_view.setOnClickListener { search_view.isIconified = false }

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null) {return true}
                else{
                if (newText!!.isEmpty()) {
                    filmsAdapter.updateData(filmDataBase as ArrayList<Film>)
                }
                val result = filmDataBase.filter { it.title.toLowerCase(Locale.getDefault()).contains(newText!!.toLowerCase(Locale.getDefault())) }
                    filmsAdapter.updateData(result as ArrayList<Film>)
                return false}
            }

        })

        search_recycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        val bundle = Bundle()
                        bundle.putParcelable("film", film)
                        (activity as MainActivity).navController.navigate(R.id.action_searchFragment_to_detailsFragment, bundle)
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