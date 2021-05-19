package xyz.flussikatz.searchmovie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var backPressetTime = 0L
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
        if(supportFragmentManager.backStackEntryCount == 1){
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