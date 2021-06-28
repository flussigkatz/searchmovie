package xyz.flussikatz.searchmovie.fragmets

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_details.*
import xyz.flussikatz.searchmovie.*


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

        AnimationHelper.revealAnimation(root_fragment_details, requireActivity())

        details_favorite.setOnCheckedChangeListener { _, isChecked -> film.fav_state = isChecked }

        step_back.setOnClickListener {
            (requireActivity() as MainActivity).onBackPressed()
        }
        details_fab.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check this film: ${film.title} \n ${film.description}."
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share to"))
        }


        details_bottom_toolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    AnimationHelper.coverAnimation(
                        root_fragment_details,
                        requireActivity(),
                        R.id.action_detailsFragment_to_homeFragment
                    )
                    true
                }
                R.id.history -> {
                    AnimationHelper.coverAnimation(
                        root_fragment_details,
                        requireActivity(),
                        R.id.action_detailsFragment_to_historyFragment
                    )
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(
                        root_fragment_details,
                        requireActivity(),
                        R.id.action_detailsFragment_to_markedFragment
                    )
                    true
                }
                else -> false
            }
        }
    }


}