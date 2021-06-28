package xyz.flussikatz.searchmovie

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import java.util.concurrent.Executors
import kotlin.math.hypot

object AnimationHelper {

    private const val animDuration = 250L

    fun revealAnimation(view: View, activity: Activity) {

        Executors.newSingleThreadExecutor().execute {
            while (true) {
                if (view.isAttachedToWindow) {
                    activity.runOnUiThread {
                        val x: Int = view.width / 2
                        val y: Int = view.height / 2
                        val startRadius = 0
                        val endRadius = hypot(view.width.toDouble(), view.height.toDouble())
                        val anim = ViewAnimationUtils.createCircularReveal(
                            view,
                            x,
                            y,
                            startRadius.toFloat(),
                            endRadius.toFloat()
                        )
                        anim.duration = animDuration
                        anim.interpolator = AccelerateDecelerateInterpolator()
                        view.visibility = View.VISIBLE
                        anim.start()
                    }
                    return@execute
                }
            }
        }
    }

    fun coverAnimation (view: View, activity: Activity, resId: Int) {

        Executors.newSingleThreadExecutor().execute {
            while (true) {
                if (view.isAttachedToWindow) {
                    activity.runOnUiThread {
                        val x: Int = view.width / 2
                        val y: Int = view.height / 2
                        val startRadius = hypot(view.width.toDouble(), view.height.toDouble())
                        val endRadius = 0
                        val anim = ViewAnimationUtils.createCircularReveal(
                            view,
                            x,
                            y,
                            startRadius.toFloat(),
                            endRadius.toFloat()
                        )
                        anim.duration = animDuration
                        anim.start()
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

    fun coverAnimation (view: View, activity: Activity, resId: Int, bundle: Bundle) {

        Executors.newSingleThreadExecutor().execute {
            while (true) {
                if (view.isAttachedToWindow) {
                    activity.runOnUiThread {
                        val x: Int = view.width / 2
                        val y: Int = view.height / 2
                        val startRadius = hypot(view.width.toDouble(), view.height.toDouble())
                        val endRadius = 0
                        val anim = ViewAnimationUtils.createCircularReveal(
                            view,
                            x,
                            y,
                            startRadius.toFloat(),
                            endRadius.toFloat()
                        )
                        anim.duration = animDuration
                        anim.start()
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