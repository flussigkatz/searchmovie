package xyz.flussikatz.searchmovie

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    private var backPressedTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        val lottieAnimationView: LottieAnimationView = welcome_screen
        lottieAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            @SuppressLint("RestrictedApi")
            override fun onAnimationEnd(animation: Animator?) {
                root_fragment_home.visibility = View.INVISIBLE
                navController.backStack.clear()
                AnimationHelper.reveaAnimationDisappere(welcome_screen, this@MainActivity, R.id.homeFragment)
            }

            @SuppressLint("RestrictedApi")
            override fun onAnimationCancel(animation: Animator?) {
                root_fragment_home.visibility = View.INVISIBLE
                navController.backStack.clear()
                AnimationHelper.reveaAnimationDisappere(welcome_screen, this@MainActivity, R.id.homeFragment)
            }
        })
        welcome_screen.setOnClickListener { lottieAnimationView.cancelAnimation() }
        lottieAnimationView.playAnimation()


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