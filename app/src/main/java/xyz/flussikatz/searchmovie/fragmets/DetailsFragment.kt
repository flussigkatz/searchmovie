package xyz.flussikatz.searchmovie.fragmets

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.flussikatz.searchmovie.AnimationHelper
import xyz.flussikatz.searchmovie.Film
import xyz.flussikatz.searchmovie.MainActivity
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.databinding.FragmentDetailsBinding


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.rootFragmentDetails

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val film = arguments?.get("film") as Film
        binding.detailsToolbar.title = film.title
        binding.detailsPoster.setImageResource(film.posterId)
        binding.detailsDescription.text = film.description
        binding.detailsFavorite.isChecked = film.fav_state

        AnimationHelper.revealAnimation(binding.rootFragmentDetails, requireActivity())

        binding.detailsFavorite.setOnCheckedChangeListener { _, isChecked ->
            film.fav_state = isChecked
        }

        binding.stepBack.setOnClickListener {
            (requireActivity() as MainActivity).onBackPressed()
        }
        binding.detailsFab.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check this film: ${film.title} \n ${film.description}."
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share to"))
        }


        binding.detailsBottomToolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentDetails,
                        requireActivity(),
                        R.id.action_detailsFragment_to_homeFragment
                    )
                    true
                }
                R.id.history -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentDetails,
                        requireActivity(),
                        R.id.action_detailsFragment_to_historyFragment
                    )
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentDetails,
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