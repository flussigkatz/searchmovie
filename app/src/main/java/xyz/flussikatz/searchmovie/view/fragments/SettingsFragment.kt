package xyz.flussikatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.databinding.FragmentSettingsBinding
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.viewmodel.SettingsFragmentViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(SettingsFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationHelper.revealAnimation(binding.rootFragmentSettings, requireActivity())

        viewModel.categoryPropertyLifeData.observe(viewLifecycleOwner, Observer<String> {
            when (it) {
                POPULAR_CATEGORY -> binding.settingsRadioGroup.check(R.id.radio_popular)
                TOP_RATED_CATEGORY -> binding.settingsRadioGroup.check(R.id.radio_top_rated)
                UPCOMING_CATEGORY -> binding.settingsRadioGroup.check(R.id.radio_upcoming)
                IN_CINEMAS_CATEGORY -> binding.settingsRadioGroup.check(R.id.radio_in_cinemas)
            }
        })

        binding.settingsRadioGroup.setOnCheckedChangeListener{group, chekedId ->
            when(chekedId) {
                R.id.radio_popular -> viewModel.putCategoryProperty(POPULAR_CATEGORY)
                R.id.radio_top_rated -> viewModel.putCategoryProperty(TOP_RATED_CATEGORY)
                R.id.radio_upcoming -> viewModel.putCategoryProperty(UPCOMING_CATEGORY)
                R.id.radio_in_cinemas -> viewModel.putCategoryProperty(IN_CINEMAS_CATEGORY)
            }
        }
    }

    companion object {
        private const val POPULAR_CATEGORY = "popular"
        private const val TOP_RATED_CATEGORY = "popular"
        private const val UPCOMING_CATEGORY = "popular"
        private const val IN_CINEMAS_CATEGORY = "popular"
    }

}