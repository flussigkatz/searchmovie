package xyz.flussikatz.searchmovie.view

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

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        val lottieAnimationView: LottieAnimationView = binding.welcomeScreen
        lottieAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                binding.rootNavHost.visibility = View.INVISIBLE
            }

            @SuppressLint("RestrictedApi")
            override fun onAnimationEnd(animation: Animator?) {
                navController.backStack.clear()
                AnimationHelper.coverAnimation(
                    binding.welcomeScreen,
                    binding.rootNavHost,
                    this@MainActivity,
                    R.id.homeFragment
                )
                binding.rootNavHost.visibility = View.VISIBLE
            }

            @SuppressLint("RestrictedApi")
            override fun onAnimationCancel(animation: Animator?) {
                navController.backStack.clear()
                AnimationHelper.coverAnimation(
                    binding.welcomeScreen,
                    binding.rootNavHost,
                    this@MainActivity,
                    R.id.homeFragment
                )
                binding.rootNavHost.visibility = View.VISIBLE
            }
        })
        binding.welcomeScreen.setOnClickListener { lottieAnimationView.cancelAnimation() }
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