package xyz.flussikatz.searchmovie

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import xyz.flussikatz.searchmovie.fragmets.MainFragment
import xyz.flussikatz.searchmovie.fragmets.MarkedFragment

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    private var backPressedTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)




    }
    fun launchDetailsFragment (film: Film, frag: Fragment) {
        val bundle = Bundle()
        bundle.putParcelable("film", film)
        when (frag.id) {
            MainFragment.instance.id -> navController.navigate(R.id.action_mainFragment_to_detailsFragment, bundle)
            MarkedFragment.instance.id -> navController.navigate(R.id.action_markedFragment_to_detailsFragment, bundle)
        }


    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        if(navController.backStack.size > 2){
            super.onBackPressed()
        } else {
            if (backPressedTime + TIME_INTERVAL > System.currentTimeMillis()){
                finish()
            } else {
                Toast.makeText(this, R.string.exit_message, Toast.LENGTH_SHORT).show()
            }
            backPressedTime = System.currentTimeMillis()
        }


    }

    companion object {
        const val TIME_INTERVAL = 2000L
    }

}