package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.doOnAttach
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.databinding.FragmentSettingsBinding
import xyz.flussigkatz.searchmovie.util.AnimationHelper
import xyz.flussigkatz.searchmovie.viewmodel.SettingsFragmentViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnAttach { AnimationHelper.revealAnimation(view) }

        viewModel.categoryPropertyLifeData.observe(viewLifecycleOwner, Observer<String> {
            when (it) {
                POPULAR_CATEGORY ->
                    binding.settingsRadioGroupCategory.check(R.id.radio_popular)
                TOP_RATED_CATEGORY ->
                    binding.settingsRadioGroupCategory.check(R.id.radio_top_rated)
                UPCOMING_CATEGORY ->
                    binding.settingsRadioGroupCategory.check(R.id.radio_upcoming)
                NOW_PLAYING_CATEGORY ->
                    binding.settingsRadioGroupCategory.check(R.id.radio_in_cinemas)
            }
        })

        viewModel.themePropertyLifeData.observe(viewLifecycleOwner, Observer<Int> {
            when (it) {
                AppCompatDelegate.MODE_NIGHT_NO ->
                    binding.settingsRadioGroupTheme.check(R.id.radio_light)
                AppCompatDelegate.MODE_NIGHT_YES ->
                    binding.settingsRadioGroupTheme.check(R.id.radio_dark)
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM ->
                    binding.settingsRadioGroupTheme.check(R.id.radio_system)
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY ->
                    binding.settingsRadioGroupTheme.check(R.id.radio_battery)
            }
        })

        viewModel.splashScreenPropertyLifeData.observe(viewLifecycleOwner, Observer<Boolean> {
            binding.settingsSplashScreen.isChecked = it
        })

        binding.settingsRadioGroupCategory.setOnCheckedChangeListener { _, chekedId ->
            when (chekedId) {
                R.id.radio_popular -> viewModel.putCategoryProperty(POPULAR_CATEGORY)
                R.id.radio_top_rated -> viewModel.putCategoryProperty(TOP_RATED_CATEGORY)
                R.id.radio_upcoming -> viewModel.putCategoryProperty(UPCOMING_CATEGORY)
                R.id.radio_in_cinemas -> viewModel.putCategoryProperty(NOW_PLAYING_CATEGORY)
            }
        }

        binding.settingsRadioGroupTheme.setOnCheckedChangeListener { _, chekedId ->
            when (chekedId) {
                R.id.radio_light ->
                    viewModel.putThemeProperty(AppCompatDelegate.MODE_NIGHT_NO)
                R.id.radio_dark ->
                    viewModel.putThemeProperty(AppCompatDelegate.MODE_NIGHT_YES)
                R.id.radio_system ->
                    viewModel.putThemeProperty(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                R.id.radio_battery ->
                    viewModel.putThemeProperty(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
        }

        binding.settingsBottomToolbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    AnimationHelper.coverAnimation(
                        view,
                        requireActivity(),
                        R.id.action_global_homeFragment
                    )
                    true
                }
                R.id.history -> {
                    AnimationHelper.coverAnimation(
                        view,
                        requireActivity(),
                        R.id.action_global_historyFragment
                    )
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(
                        view,
                        requireActivity(),
                        R.id.action_global_markedFragment
                    )
                    true
                }
                R.id.settings -> {
                    Toast.makeText(context, "Already", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        binding.settingsSplashScreen.setOnCheckedChangeListener { _, chekedId ->
            viewModel.putSplashScreenProperty(chekedId)
        }
    }

    companion object {
        private const val POPULAR_CATEGORY = "popular"
        private const val TOP_RATED_CATEGORY = "top_rated"
        private const val UPCOMING_CATEGORY = "upcoming"
        private const val NOW_PLAYING_CATEGORY = "now_playing"
    }

}