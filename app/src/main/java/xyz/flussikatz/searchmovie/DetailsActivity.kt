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
//        details_toolbar.title = film.title
//        details_poster.setImageResource(film.poster)
//        details_description.text = film.description
//        details_favorite.isChecked = film.fav_state
//
//        details_favorite.setOnCheckedChangeListener { _, isChecked ->  film.fav_state = isChecked}
//
//       step_back.setOnClickListener {
//           super.finish()
//       }
//        details_fab.setOnClickListener {
//        }
//        val bundle = Bundle()
//        bundle.putParcelable("film", film)
//        val detailsFragment = DetailsFragment()
//        detailsFragment.arguments = bundle
//        supportFragmentManager
//            .beginTransaction()
//            .add(R.id.fragment_container, detailsFragment)
//            .addToBackStack(null)
//            .commit()
    }
}