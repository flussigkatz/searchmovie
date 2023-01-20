package xyz.flussigkatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.fragment.app.activityViewModels
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.databinding.FragmentSettingsBinding
import xyz.flussigkatz.searchmovie.viewmodel.SettingsFragmentViewModel
import java.lang.IllegalArgumentException

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
        viewModel.themePropertyLifeData.observe(viewLifecycleOwner) {
            when (it) {
                MODE_NIGHT_NO -> binding.settingsRadioGroupTheme.check(R.id.radio_light)
                MODE_NIGHT_YES -> binding.settingsRadioGroupTheme.check(R.id.radio_dark)
                MODE_NIGHT_FOLLOW_SYSTEM -> binding.settingsRadioGroupTheme.check(R.id.radio_system)
                MODE_NIGHT_AUTO_BATTERY -> binding.settingsRadioGroupTheme.check(R.id.radio_battery)
            }
        }
        viewModel.splashScreenPropertyLifeData.observe(viewLifecycleOwner) {
            binding.settingsSplashScreen.isChecked = it
        }
        binding.settingsRadioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.radio_light -> MODE_NIGHT_NO
                R.id.radio_dark -> MODE_NIGHT_YES
                R.id.radio_system -> MODE_NIGHT_FOLLOW_SYSTEM
                R.id.radio_battery -> MODE_NIGHT_AUTO_BATTERY
                else -> throw IllegalArgumentException("Wrong argument for night mode")
            }
            viewModel.setNightMode(mode, requireActivity())
        }
        binding.settingsSplashScreen.setOnCheckedChangeListener { _, checkedId ->
            viewModel.putSplashScreenProperty(checkedId)
        }
    }
}