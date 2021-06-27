package xyz.flussikatz.searchmovie.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_history.*
import xyz.flussikatz.searchmovie.AnimationHelper
import xyz.flussikatz.searchmovie.R

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.revealAnimation(root_fragment_history, requireActivity())

        history_bottom_toolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    AnimationHelper.coverAnimation(root_fragment_history, requireActivity(), R.id.action_historyFragment_to_homeFragment)
                    true
                }
                R.id.history -> {
                    Toast.makeText(context, "Already", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(root_fragment_history, requireActivity(), R.id.action_historyFragment_to_markedFragment)
                    true
                }
                else -> false
            }
        }
    }

}