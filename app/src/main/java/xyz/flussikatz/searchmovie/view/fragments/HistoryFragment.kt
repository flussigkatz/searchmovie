package xyz.flussikatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.databinding.FragmentHistoryBinding
import xyz.flussikatz.searchmovie.domain.Movie
import java.io.IOException
import java.lang.Exception

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding

    init {
        val gson = Gson()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/550?api_key=0e2890e9ecce0e067130f88a04963bfa")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBodyString = response.body()?.string()
                    binding.movie = gson.fromJson(responseBodyString, Movie::class.java)
                } catch (e: Exception) {
                    println(response)
                    e.printStackTrace()
                }
            }
        })
    }

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