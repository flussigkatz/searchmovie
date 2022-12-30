package xyz.flussigkatz.searchmovie.view

import android.animation.Animator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.ExperimentalPagingApi
import com.airbnb.lottie.LottieAnimationView
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.R.id.*
import xyz.flussigkatz.searchmovie.data.ConstantsApp
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NAVIGATE_TO_DETAILS
import xyz.flussigkatz.searchmovie.databinding.ActivityMainBinding
import xyz.flussigkatz.searchmovie.util.*
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.BORING_KILLER_NOTIFICATION_ID
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.BORING_KILLER_NOTIFICATION_OFF_KEY
import xyz.flussigkatz.searchmovie.viewmodel.MainActivityViewModel
import kotlin.math.hypot

@ExperimentalPagingApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManagerCompat
    private val viewModel: MainActivityViewModel by viewModels()
    private val receiver = Receiver()
    private lateinit var navController: NavController
    private var backPressedTime = INITIAL_TIME


    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootActivityMain)
        initReceiver()
        initAnimation()
        initNavigation()
        initEventMessage()
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val onScreenFragmentId = navController.backQueue.last().destination.id
        if (homeFragment != onScreenFragmentId) super.onBackPressed()
        else {
            if (backPressedTime + TIME_INTERVAL > System.currentTimeMillis()) finish()
            else Toast.makeText(this, R.string.exit_message, Toast.LENGTH_SHORT).show()
            backPressedTime = System.currentTimeMillis()
        }
    }

    override fun onStart() {
        super.onStart()
        startDetailsMarkedFilm()
    }

    override fun onStop() {
        unregisterReceiver(receiver)
        super.onStop()
    }

    //region Animation
    private fun initAnimation() {
        viewModel.getSplashScreenStateStatus().let {
            findViewById<CoordinatorLayout>(root_fragment_home)?.let { view ->
                if (it) initSplashScreen(binding.splashScreen, view)
                else firstStartAnimation(view)
            }
        }
    }

    private fun initSplashScreen(lottieView: View, revealView: View) {
        lottieView.visibility = View.VISIBLE
        val lottieAnimationView: LottieAnimationView = lottieView as LottieAnimationView
        lottieAnimationView.speed = LOTTIE_ANIMATION_SPEED
        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                lottieCoverAnimation(lottieView, revealView)
            }

            override fun onAnimationCancel(p0: Animator) {
                lottieCoverAnimation(lottieView, revealView)
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
        lottieView.setOnClickListener { lottieAnimationView.cancelAnimation() }
        lottieAnimationView.playAnimation()
    }


    private fun firstStartAnimation(view: View) {
        view.visibility = View.INVISIBLE
        var revealAnimationIsStarted = false
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val x: Int = view.width.div(ConstantsApp.HALF_RATIO)
                val y: Int = view.height.div(ConstantsApp.HALF_RATIO)
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
        val x: Int = view.width.div(ConstantsApp.HALF_RATIO)
        val y: Int = view.height.div(ConstantsApp.HALF_RATIO)
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


    private fun lottieCoverAnimation(coverView: View, revealView: View) {
        revealView.visibility = View.INVISIBLE
        val x: Int = coverView.width.div(ConstantsApp.HALF_RATIO)
        val y: Int = coverView.height.div(ConstantsApp.HALF_RATIO)
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
    //endregion

    //region Navigation
    private fun initNavigation() {
        navController = Navigation.findNavController(this, nav_host_fragment)
        binding.mainBottomToolbar.setOnItemSelectedListener {
            navigate(it.itemId)
            true
        }
    }

    private fun navigate(menuItemId: Int) {
        with(navController) {
            when (navController.backQueue.last().destination.id) {
                homeFragment -> {
                    when (menuItemId) {
                        history -> navigate(action_homeFragment_to_historyFragment)
                        marked -> navigate(action_homeFragment_to_markedFragment)
                        settings -> navigate(action_homeFragment_to_settingsFragment)
                    }
                }
                historyFragment -> {
                    when (menuItemId) {
                        home_page -> navigate(action_historyFragment_to_homeFragment)
                        marked -> navigate(action_historyFragment_to_markedFragment)
                        settings -> navigate(action_historyFragment_to_settingsFragment)
                    }
                }
                markedFragment -> {
                    when (menuItemId) {
                        home_page -> navigate(action_markedFragment_to_homeFragment)
                        history -> navigate(action_markedFragment_to_historyFragment)
                        settings -> navigate(action_markedFragment_to_settingsFragment)
                    }
                }
                settingsFragment -> {
                    when (menuItemId) {
                        home_page -> navigate(action_settingsFragment_to_homeFragment)
                        history -> navigate(action_settingsFragment_to_historyFragment)
                        marked -> navigate(action_settingsFragment_to_markedFragment)
                    }
                }
                detailsFragment -> {
                    with(navController) {
                        when (menuItemId) {
                            home_page -> navigate(action_detailsFragment_to_homeFragment)
                            history -> navigate(action_detailsFragment_to_historyFragment)
                            marked -> navigate(action_detailsFragment_to_markedFragment)
                            settings -> navigate(action_detailsFragment_to_settingsFragment)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToDetails(bundle: Bundle?) {
        bundle?.let {
            with(navController) {
                when (navController.backQueue.last().destination.id) {
                    homeFragment -> navigate(action_homeFragment_to_detailsFragment, bundle)
                    historyFragment -> navigate(action_historyFragment_to_detailsFragment, bundle)
                    markedFragment -> navigate(action_markedFragment_to_detailsFragment, bundle)
                    settingsFragment -> navigate(action_settingsFragment_to_detailsFragment, bundle)
                }
            }
        }
    }
    //endregion

    private fun startDetailsMarkedFilm() {
        intent.getBundleExtra(BORING_KILLER_NOTIFICATION_FILM_KEY)?.let {
            navController.navigate(action_markedFragment_to_detailsFragment, it)
        }
    }

    private fun initReceiver() {
        val filter = IntentFilter().apply {
            addAction(NAVIGATE_TO_DETAILS)
            addAction(BORING_KILLER_NOTIFICATION_FILM_KEY)
            addAction(BORING_KILLER_NOTIFICATION_OFF_KEY)
        }
        registerReceiver(receiver, filter)
    }

    private fun initTheme() {
        AppCompatDelegate.setDefaultNightMode(viewModel.getNightModeStatus())
    }

    private fun initEventMessage() {
        viewModel.eventMessage.observe(this) {
            Toast.makeText(this, getText(it), Toast.LENGTH_SHORT).show()
        }
    }

    private inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BORING_KILLER_NOTIFICATION_FILM_KEY -> {
                    this@MainActivity.intent.putExtra(
                        BORING_KILLER_NOTIFICATION_FILM_KEY,
                        intent.getBundleExtra(BORING_KILLER_NOTIFICATION_FILM_KEY)
                    )
                    when (this@MainActivity.lifecycle.currentState) {
                        Lifecycle.State.RESUMED -> startDetailsMarkedFilm()
                        else -> startActivity(Intent(context, MainActivity::class.java))
                    }
                }
                BORING_KILLER_NOTIFICATION_OFF_KEY -> {
                    notificationManager.cancel(BORING_KILLER_NOTIFICATION_ID)
                }
                NAVIGATE_TO_DETAILS -> navigateToDetails(intent.getBundleExtra(DETAILS_FILM_KEY))
            }
        }
    }

    companion object {
        private const val TIME_INTERVAL = 2000L
        private const val INITIAL_TIME = 0L
        private const val LOTTIE_ANIMATION_SPEED = 0.7F
        private const val CIRCULAR_ANIMATION_DELAY = 200L
        private const val CIRCULAR_ANIMATION_DURATION = 500L
    }
}