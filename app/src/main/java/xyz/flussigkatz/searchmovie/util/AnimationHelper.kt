package xyz.flussigkatz.searchmovie.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.airbnb.lottie.LottieAnimationView
import xyz.flussigkatz.searchmovie.data.ConstantsApp.HALF_RATIO
import kotlin.math.hypot

private const val LOTTIE_ANIMATION_SPEED = 0.7F
private const val CIRCULAR_ANIMATION_DELAY = 200L
private const val CIRCULAR_ANIMATION_DURATION = 500L

object AnimationHelper {

    fun initSplashScreen(lottieView: View, revealView: View) {
        lottieView.visibility = View.VISIBLE
        val lottieAnimationView: LottieAnimationView = lottieView as LottieAnimationView
        lottieAnimationView.speed = LOTTIE_ANIMATION_SPEED
        lottieAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                lottieCoverAnimation(lottieView, revealView)
            }

            override fun onAnimationCancel(animation: Animator?) {
                lottieCoverAnimation(lottieView, revealView)
            }
        })
        lottieView.setOnClickListener { lottieAnimationView.cancelAnimation() }
        lottieAnimationView.playAnimation()
    }


    fun firstStartAnimation(view: View) {
        view.visibility = View.INVISIBLE
        var revealAnimationIsStarted = false
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val x: Int = view.width.div(HALF_RATIO)
                val y: Int = view.height.div(HALF_RATIO)
                val startRadius = 0
                val endRadius = hypot(view.width.toDouble(), view.height.toDouble())
                ViewAnimationUtils.createCircularReveal(
                    view,
                    x,
                    y,
                    startRadius.toFloat(),
                    endRadius.toFloat()
                ).also {
                    it.doOnStart { view.visibility = View.VISIBLE }
                    it.doOnEnd { view.viewTreeObserver.removeOnGlobalLayoutListener(this) }
                    it.startDelay = CIRCULAR_ANIMATION_DELAY
                    it.duration = CIRCULAR_ANIMATION_DURATION
                    it.interpolator = AccelerateDecelerateInterpolator()
                    if (!revealAnimationIsStarted) {
                        it.start()
                        revealAnimationIsStarted = true
                    }
                }
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    private fun revealAnimation(view: View) {
        val x: Int = view.width.div(HALF_RATIO)
        val y: Int = view.height.div(HALF_RATIO)
        val startRadius = 0
        val endRadius = hypot(view.width.toDouble(), view.height.toDouble())
        ViewAnimationUtils.createCircularReveal(
            view,
            x,
            y,
            startRadius.toFloat(),
            endRadius.toFloat()
        ).apply {
            duration = CIRCULAR_ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }


    fun lottieCoverAnimation(coverView: View, revealView: View) {
        revealView.visibility = View.INVISIBLE
        val x: Int = coverView.width.div(HALF_RATIO)
        val y: Int = coverView.height.div(HALF_RATIO)
        val startRadius = hypot(coverView.width.toDouble(), coverView.height.toDouble())
        val endRadius = 0
        ViewAnimationUtils.createCircularReveal(
            coverView,
            x,
            y,
            startRadius.toFloat(),
            endRadius.toFloat()
        ).apply {
            duration = CIRCULAR_ANIMATION_DURATION
            doOnEnd {
                revealAnimation(revealView)
                coverView.visibility = View.GONE
                revealView.visibility = View.VISIBLE
            }
            start()
        }
    }
}