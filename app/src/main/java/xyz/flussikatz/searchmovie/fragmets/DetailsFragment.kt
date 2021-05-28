package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_details.*
import xyz.flussikatz.searchmovie.Film
import xyz.flussikatz.searchmovie.MainActivity
import xyz.flussikatz.searchmovie.R

class DetailsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val film = arguments?.get("film") as Film
        details_toolbar.title = film.title
        details_poster.setImageResource(film.poster)
        details_description.text = film.description
        details_favorite.isChecked = film.fav_state

        details_favorite.setOnCheckedChangeListener { _, isChecked ->  film.fav_state = isChecked}

        step_back.setOnClickListener {
            (requireActivity() as MainActivity).onBackPressed()
        }
        details_fab.setOnClickListener {
        }
    }


}