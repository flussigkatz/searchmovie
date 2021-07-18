package xyz.flussikatz.searchmovie.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.*
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.databinding.FragmentHistoryBinding
import xyz.flussikatz.searchmovie.domain.Movie
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.Executors

private const val API_KEY = "0e2890e9ecce0e067130f88a04963bfa"

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val movie_id = 550
//    var moviePoster = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/66RvLrRJTm4J8l3uHXWF09AICol.jpg"

    init {
        val gson = Gson()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/$movie_id?api_key=$API_KEY&language=ru-RU")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBodyString = response.body()?.string()
                    val movie = gson.fromJson(responseBodyString, Movie::class.java)
                    binding.movie = movie
//                    moviePoster = movie.posterPath
                    println("!!! ${movie.posterPath}")
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

        Glide.with(this)
            .load("https://api.themoviedb.org/3/movie/$movie_id/images?api_key=$API_KEY&language=ru-RU")
            .centerCrop()
            .into(binding.hystoryPoster)

        /*Executors.newSingleThreadExecutor().execute {
            while (moviePoster == "") {
                if (moviePoster != "") {
                    println("!!! $moviePoster")
                    Glide.with(this)
                        .load(moviePoster)
                        .centerCrop()
                        .into(binding.hystoryPoster)
                }
            }
            return@execute
        }*/

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