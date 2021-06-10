package xyz.flussikatz.searchmovie.fragmets

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.fragment_details.*
import xyz.flussikatz.searchmovie.Film
import xyz.flussikatz.searchmovie.MainActivity
import xyz.flussikatz.searchmovie.R

class DetailsFragment : Fragment() {
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
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "Check this film: ${film.title} \n ${film.description}.")
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share to"))
        }
    }


}