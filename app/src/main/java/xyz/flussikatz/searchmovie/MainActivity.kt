package xyz.flussikatz.searchmovie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    private var backPressetTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)




    }
    fun launchDetailsFragment (film: Film) {
        val bundle = Bundle()
        bundle.putParcelable("film", film)

        navController.navigate(R.id.action_mainFragment_to_detailsFragment, bundle)

    }

    override fun onBackPressed() {
        if(navController.backStack.size > 2){
            super.onBackPressed()
        } else {
            if (backPressetTime + TIME_INTERVAL > System.currentTimeMillis()){
                finish()
            } else {
                Toast.makeText(this, R.string.exit_message, Toast.LENGTH_SHORT).show()
            }
            backPressetTime = System.currentTimeMillis()
        }


    }

    companion object {
        const val TIME_INTERVAL = 2000L
    }

}