package xyz.flussikatz.searchmovie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, MainFragment())
            .addToBackStack(null)
            .commit()




    }
    fun launchDetailsFragment (film: Film) {
        val bundle = Bundle()
        bundle.putParcelable("film", film)
        val fragmentDetails = DetailsFragment()
        fragmentDetails.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragmentDetails)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 1){
            super.onBackPressed()
        } else {
            AlertDialog.Builder(this).setTitle("Exit?")
                .setPositiveButton("Yes"){_, _ ->
                    finish()
                }
                .setNegativeButton("No"){_, _ ->

                }.show()
        }


    }


}