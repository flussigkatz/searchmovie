package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import xyz.flussikatz.searchmovie.AnimationHelper
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.rootFragmentHistory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.revealAnimation(binding.rootFragmentHistory, requireActivity())


        binding.historyBottomToolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    AnimationHelper.coverAnimation(
                        binding.root,
                        requireActivity(),
                        R.id.action_historyFragment_to_homeFragment
                    )
                    true
                }
                R.id.history -> {
                    Toast.makeText(context, "Already", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentHistory,
                        requireActivity(),
                        R.id.action_historyFragment_to_markedFragment
                    )
                    true
                }
                else -> false
            }
        }
    }

}