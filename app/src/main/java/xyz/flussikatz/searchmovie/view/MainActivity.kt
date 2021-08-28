package xyz.flussikatz.searchmovie.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootActivityMain)

        navController = Navigation.findNavController(
            this@MainActivity,
            R.id.nav_host_fragment
        )

        val lottieAnimationView: LottieAnimationView = binding.welcomeScreen
        lottieAnimationView.speed = 0.7F
        lottieAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                endAnimationWelcomeScreen()
            }

            override fun onAnimationCancel(animation: Animator?) {
                endAnimationWelcomeScreen()
            }

            @SuppressLint("RestrictedApi")
            fun endAnimationWelcomeScreen() {
                this@MainActivity
                    .findViewById<CoordinatorLayout>(R.id.root_fragment_home)
                    .visibility = View.INVISIBLE

                navController.backStack.clear()
                AnimationHelper.coverAnimation(
                    binding.welcomeScreen,
                    this@MainActivity,
                    R.id.homeFragment
                )
            }
        })
        binding.welcomeScreen.setOnClickListener { lottieAnimationView.cancelAnimation() }
        lottieAnimationView.playAnimation()

    }


    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        if (navController.backStack.size > 2) {
            super.onBackPressed()
        } else {
            if (backPressedTime + TIME_INTERVAL > System.currentTimeMillis()) {
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