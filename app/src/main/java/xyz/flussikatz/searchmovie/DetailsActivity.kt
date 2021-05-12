package xyz.flussikatz.searchmovie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val film = intent.extras?.get("film") as Film
        details_toolbar.title = film.title
        details_poster.setImageResource(film.poster)
//        details_description.text = film.description
        details_description.text = film.fav_state.toString()

       step_back.setOnClickListener {
           details_description.text = film.fav_state.toString()
       }
        details_fab.setOnClickListener {
            film.fav_state = !film.fav_state
        }
    }
}