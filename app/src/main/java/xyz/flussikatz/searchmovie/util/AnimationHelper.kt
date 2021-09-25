package xyz.flussikatz.searchmovie.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.view.MainActivity
import java.util.concurrent.Executors
import kotlin.math.hypot

private const val CIRCULAR_ANIMATION_DURATION = 700L

object AnimationHelper {

    fun revealAnimation(view: View, activity: Activity) {

        Executors.newSingleThreadExecutor().execute {
            var isAnimate = false
            while (!isAnimate) {
                if (view.isAttachedToWindow) {
                    activity.runOnUiThread {
                        val x: Int = view.width.div(2)
                        val y: Int = view.height.div(2)
                        val startRadius = 0
                        val endRadius = hypot(view.width.toDouble(), view.height.toDouble())
                        val anim = ViewAnimationUtils.createCircularReveal(
                            view,
                            x,
                            y,
                            startRadius.toFloat(),
                            endRadius.toFloat()
                        )
                        anim.duration = CIRCULAR_ANIMATION_DURATION
                        anim.interpolator = AccelerateDecelerateInterpolator()
                        anim.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationStart(animation: Animator?) {
                                view.visibility = View.VISIBLE
                            }
                        })
                        anim.start()
                        isAnimate = true
                    }
                    return@execute
                }
            }
        }
    }

    fun coverAnimation(view: View, activity: Activity, resId: Int) {

        Executors.newSingleThreadExecutor().execute {
            var isAnimate = false
            while (!isAnimate) {
                if (view.isAttachedToWindow) {
                    activity.runOnUiThread {
                        val x: Int = view.width.div(2)
                        val y: Int = view.height.div(2)
                        val startRadius = hypot(view.width.toDouble(), view.height.toDouble())
                        val endRadius = 0
                        val anim = ViewAnimationUtils.createCircularReveal(
                            view,
                            x,
                            y,
                            startRadius.toFloat(),
                            endRadius.toFloat()
                        )
                        anim.duration = CIRCULAR_ANIMATION_DURATION
                        anim.start()
                        isAnimate = true
                        anim.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                view.visibility = View.INVISIBLE
                                (activity as MainActivity).navController.navigate(resId)
                            }
                        })
                    }
                    return@execute
                }
            }
        }
    }

    fun coverAnimation(view: View, activity: Activity, resId: Int, bundle: Bundle) {

        Executors.newSingleThreadExecutor().execute {
            var isAnimate = false
            while (!isAnimate) {
                if (view.isAttachedToWindow) {
                    activity.runOnUiThread {
                        val x: Int = view.width.div(2)
                        val y: Int = view.height.div(2)
                        val startRadius = hypot(view.width.toDouble(), view.height.toDouble())
                        val endRadius = 0
                        val anim = ViewAnimationUtils.createCircularReveal(
                            view,
                            x,
                            y,
                            startRadius.toFloat(),
                            endRadius.toFloat()
                        )
                        anim.duration = CIRCULAR_ANIMATION_DURATION
                        anim.start()
                        isAnimate = true
                        anim.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                view.visibility = View.INVISIBLE
                                (activity as MainActivity).navController.navigate(resId, bundle)
                            }
                        })
                    }
                    return@execute
                }
            }
        }
    }
}
