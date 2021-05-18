package xyz.flussikatz.searchmovie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*materialToolbar.setNavigationOnClickListener {
        }

        materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        bottomToolBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
                    Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.history -> {
                    Toast.makeText(context, "History", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.marked -> {
                    Toast.makeText(context, "Marked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

}