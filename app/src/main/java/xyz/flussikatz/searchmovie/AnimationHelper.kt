package xyz.flussikatz.searchmovie

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import java.util.concurrent.Executors
import kotlin.math.hypot

class AnimationHelper {


    companion object {
        private const val animDuration = 200L

        fun reveaAnimationAppere(view: View, activity: Activity) {

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
                            view.visibility = View.VISIBLE
                            anim.start()
                        }
                        return@execute
                    }
                }
            }
        }

        fun reveaAnimationDisappere (view: View, activity: Activity, resId: Int) {

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

        fun reveaAnimationDisappere (view: View, activity: Activity, resId: Int, bundle: Bundle) {

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
}