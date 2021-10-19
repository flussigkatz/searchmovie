package xyz.flussikatz.searchmovie.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.databinding.ActivityMainBinding
import javax.inject.Inject

private const val LOTTIE_ANIMATION_SPEED = 0.7F

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferences: PreferenceProvider

    init {
        App.instance.dagger.inject(this)
    }

    lateinit var navController: NavController
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootActivityMain)

        val viewHomeFragment = this@MainActivity
            .findViewById<CoordinatorLayout>(R.id.root_fragment_home)

        navController = Navigation.findNavController(
            this@MainActivity,
            R.id.nav_host_fragment
        )

        val lottieAnimationView: LottieAnimationView = binding.splashScreen
        lottieAnimationView.speed = LOTTIE_ANIMATION_SPEED
        lottieAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                endAnimationSplashScreen()
            }

            override fun onAnimationCancel(animation: Animator?) {
                endAnimationSplashScreen()
            }

            @SuppressLint("RestrictedApi")
            fun endAnimationSplashScreen() {
                viewHomeFragment.visibility = View.INVISIBLE

                navController.backStack.clear()
                AnimationHelper.coverAnimation(
                    binding.splashScreen,
                    this@MainActivity,
                    R.id.homeFragment
                )
            }
        })

        binding.splashScreen.setOnClickListener { lottieAnimationView.cancelAnimation() }

        if (preferences.getPlaySplashScreenState()) {
            lottieAnimationView.playAnimation()
            preferences.setPlaySplashScreenState(false)
        } else {
            viewHomeFragment.visibility = View.INVISIBLE
            binding.splashScreen.visibility = View.GONE
        }

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