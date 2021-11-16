package xyz.flussikatz.searchmovie.util

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import kotlinx.coroutines.*
import xyz.flussikatz.searchmovie.view.MainActivity
import java.util.*
import kotlin.math.hypot

object AnimationHelper {

    private const val CIRCULAR_ANIMATION_DURATION = 500L
    private val animScope = CoroutineScope(Dispatchers.IO)

    fun revealAnimation(view: View) {
        var startCallback = false
        animScope.launch {
            do {
                if (view.width > 0 || view.height > 0) {
                    withContext(Dispatchers.Main) {
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
                        anim.doOnStart {
                            view.visibility = View.VISIBLE
                            startCallback = true
                        }
                        anim.doOnEnd {
                            this@launch.cancel()
                        }
                        anim.duration = CIRCULAR_ANIMATION_DURATION
                        anim.interpolator = AccelerateDecelerateInterpolator()
                        if (!anim.isRunning) anim.start()
                    }
                }
            } while (!startCallback)
        }
    }

    fun coverAnimation(coverView: View, activity: Activity, resId: Int, bundle: Bundle? = null) {
        val x: Int = coverView.width.div(2)
        val y: Int = coverView.height.div(2)
        val startRadius = hypot(coverView.width.toDouble(), coverView.height.toDouble())
        val endRadius = 0
        val anim = ViewAnimationUtils.createCircularReveal(
            coverView,
            x,
            y,
            startRadius.toFloat(),
            endRadius.toFloat()
        )
        anim.duration = CIRCULAR_ANIMATION_DURATION
        anim.doOnEnd {
            coverView.visibility = View.INVISIBLE
            (activity as MainActivity).navController.navigate(resId, bundle)
        }
        anim.start()
    }

    fun lottieCoverAnimation(coverView: View, revealView: View) {
        val x: Int = coverView.width.div(2)
        val y: Int = coverView.height.div(2)
        val startRadius = hypot(coverView.width.toDouble(), coverView.height.toDouble())
        val endRadius = 0
        val anim = ViewAnimationUtils.createCircularReveal(
            coverView,
            x,
            y,
            startRadius.toFloat(),
            endRadius.toFloat()
        )
        anim.duration = CIRCULAR_ANIMATION_DURATION
        anim.doOnEnd {
            coverView.visibility = View.INVISIBLE
            revealAnimation(revealView)
        }
        anim.start()
    }

    fun cancelAnimScope() {
        animScope.cancel()
    }
}
