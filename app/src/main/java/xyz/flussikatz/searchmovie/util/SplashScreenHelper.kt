package xyz.flussikatz.searchmovie.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import com.airbnb.lottie.LottieAnimationView

object SplashScreenHelper {

    private const val LOTTIE_ANIMATION_SPEED = 0.7F

    fun initSplashScreen(lottieView: View, revealView: View) {
            lottieView.visibility = View.VISIBLE
            val lottieAnimationView: LottieAnimationView = lottieView as LottieAnimationView
            lottieAnimationView.speed = LOTTIE_ANIMATION_SPEED
            lottieAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animation: Animator?) {
                    revealView.visibility = View.INVISIBLE
                }

                override fun onAnimationEnd(animation: Animator?) {
                    endAnimationSplashScreen()
                }

                override fun onAnimationCancel(animation: Animator?) {
                    endAnimationSplashScreen()
                }

                fun endAnimationSplashScreen() {
                    AnimationHelper.lottieCoverAnimation(lottieView, revealView)
                }
            })
            lottieView.setOnClickListener { lottieAnimationView.cancelAnimation() }
            lottieAnimationView.playAnimation()
    }
}