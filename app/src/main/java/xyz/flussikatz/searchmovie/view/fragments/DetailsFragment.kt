package xyz.flussikatz.searchmovie.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import xyz.flussikatz.searchmovie.domain.Film
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.ApiConstants
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.databinding.FragmentDetailsBinding
import xyz.flussikatz.searchmovie.view.MainActivity


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
        binding.film = film

        AnimationHelper.revealAnimation(binding.rootFragmentDetails, requireActivity())

        binding.stepBack.setOnClickListener {
            (requireActivity() as MainActivity).onBackPressed()
            //TODO некоректно возвращает на маркерованный фрагмент, пустой recycler
        }

        Picasso.get()
            .load(ApiConstants.IMAGES_URL + "w500" + film.posterId)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.upload_wait)
            .error(R.drawable.upload_fail)
            .into(binding.detailsPoster)

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
                        R.id.action_global_homeFragment
                    )
                    true
                }
                R.id.history -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentDetails,
                        requireActivity(),
                        R.id.action_global_historyFragment
                    )
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentDetails,
                        requireActivity(),
                        R.id.action_global_markedFragment
                    )
                    true
                }
                else -> false
            }
        }
    }
}