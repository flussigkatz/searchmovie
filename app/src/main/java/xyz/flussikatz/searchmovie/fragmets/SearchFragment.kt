package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.whenResumed
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_search.*
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

        seach_view.setOnClickListener { seach_view.isIconified = false }

        seach_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
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