package xyz.flussigkatz.searchmovie.view

import android.Manifest.permission.POST_NOTIFICATIONS
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.O
import android.os.Bundle
import android.provider.Settings
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
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.R.id.*
import xyz.flussigkatz.searchmovie.SearchMovieReceiver
import xyz.flussigkatz.searchmovie.data.ConstantsApp
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NAVIGATE_TO_DETAILS
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.databinding.ActivityMainBinding
import xyz.flussigkatz.searchmovie.util.*
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.ACTION_BORING_KILLER_NOTIFICATION_ALARM
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.ACTION_BORING_KILLER_NOTIFICATION_FILM
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.ACTION_BORING_KILLER_NOTIFICATION_OFF
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.BORING_KILLER_NOTIFICATION_ID
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.NOTIFICATION_CHANNEL_ID
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.PENDINGINTENT_ALARM_REQUEST_CODE
import xyz.flussigkatz.searchmovie.viewmodel.MainActivityViewModel
import java.util.*
import kotlin.math.hypot

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private val receiver = Receiver()
    private lateinit var navController: NavController
    private var backPressedTime = INITIAL_TIME


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(viewModel.getNightModeStatus())
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootActivityMain)
        initReceiver()
        initAnimation()
        initNavigation()
        initEventMessage()
        initBoringNotification()
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
        boringNotificationChannelSettings(intent.action)
        super.onStart()
        navigateToDetails(intent)
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        boringNotificationChannelSettings(intent?.action)
        navigateToDetails(intent)
        super.onNewIntent(intent)
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
        (lottieView as LottieAnimationView).apply {
            speed = LOTTIE_ANIMATION_SPEED
            addAnimatorListener(object : Animator.AnimatorListener {
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
            setOnClickListener { cancelAnimation() }
            playAnimation()
        }
    }


    private fun firstStartAnimation(view: View) {
        view.visibility = View.INVISIBLE
        var revealAnimationIsStarted = false
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val x: Int = view.width.div(ConstantsApp.HALF_RATIO)
                val y: Int = view.height.div(ConstantsApp.HALF_RATIO)
                val startRadius = 0.toFloat()
                val endRadius = hypot(view.width.toDouble(), view.height.toDouble()).toFloat()
                ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius).also {
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
        val x = view.width.div(ConstantsApp.HALF_RATIO)
        val y = view.height.div(ConstantsApp.HALF_RATIO)
        val startRadius = 0.toFloat()
        val endRadius = hypot(view.width.toDouble(), view.height.toDouble()).toFloat()
        ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius).apply {
            duration = CIRCULAR_ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }


    private fun lottieCoverAnimation(coverView: View, revealView: View) {
        revealView.visibility = View.INVISIBLE
        val x: Int = coverView.width.div(ConstantsApp.HALF_RATIO)
        val y: Int = coverView.height.div(ConstantsApp.HALF_RATIO)
        val startRadius = hypot(coverView.width.toDouble(), coverView.height.toDouble()).toFloat()
        val endRadius = 0.toFloat()
        ViewAnimationUtils.createCircularReveal(coverView, x, y, startRadius, endRadius).apply {
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

    private fun navigateToDetails(intent: Intent?) {
        val bundle = intent?.getBundleExtra(DETAILS_FILM_KEY)
        bundle?.let {
            with(navController) {
                when (navController.backQueue.last().destination.id) {
                    homeFragment -> navigate(action_homeFragment_to_detailsFragment, it)
                    historyFragment -> navigate(action_historyFragment_to_detailsFragment, it)
                    markedFragment -> navigate(action_markedFragment_to_detailsFragment, it)
                    settingsFragment -> navigate(action_settingsFragment_to_detailsFragment, it)
                }
            }
        }
    }
    //endregion

    //region Notification
    private fun initBoringNotification() {
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(this, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.getIdsMarkedFilms().let {
                        if (it.isNotEmpty()) setWatchFilmReminder(
                            this@MainActivity,
                            FilmUiModel(viewModel.getMarkedFilmById(it.random()))
                        )
                    }
                }
            } else if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(this, arrayOf(POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
            } else Toast.makeText(this, R.string.no_permission, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setWatchFilmReminder(context: Context, film: FilmUiModel) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, ZERO_TIME)
            set(Calendar.MINUTE, ZERO_TIME)
            set(Calendar.SECOND, ZERO_TIME)
            set(Calendar.MILLISECOND, ZERO_TIME)
        }
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        if (viewModel.getDayOfYear() < dayOfYear) {
            val intentBoringKillerAlarm = Intent(context, SearchMovieReceiver::class.java).apply {
                action = ACTION_BORING_KILLER_NOTIFICATION_ALARM
                putExtra(DETAILS_FILM_KEY, Bundle().apply { putParcelable(DETAILS_FILM_KEY, film) })
            }
            val pendingIntentAlarm = PendingIntent.getBroadcast(
                context,
                PENDINGINTENT_ALARM_REQUEST_CODE,
                intentBoringKillerAlarm,
                if (SDK_INT >= M) FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
                else FLAG_UPDATE_CURRENT
            )
            val trigger = calendar.run {
                (timeInMillis..timeInMillis + MILLISECONDS_OF_DAY).random()
            }
            context.run {
                if (SDK_INT >= M) getSystemService(AlarmManager::class.java)
                else getSystemService(ALARM_SERVICE) as AlarmManager
            }.set(AlarmManager.RTC, trigger, pendingIntentAlarm)
            viewModel.saveDayOfYear(dayOfYear)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun boringNotificationChannelSettings(action: String?) {
        if (action == ACTION_BORING_KILLER_NOTIFICATION_OFF && SDK_INT >= O) {
            startActivity(Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL_ID)
            })
            NotificationManagerCompat.from(this).cancel(BORING_KILLER_NOTIFICATION_ID)
            finish()
        } else NotificationManagerCompat.from(this).cancel(BORING_KILLER_NOTIFICATION_ID)

    }
    //endregion


    private fun initReceiver() {
        val filter = IntentFilter().apply {
            addAction(NAVIGATE_TO_DETAILS)
            addAction(ACTION_BORING_KILLER_NOTIFICATION_FILM)
        }
        registerReceiver(receiver, filter)
    }

    private fun initEventMessage() {
        viewModel.eventMessage.observe(this) {
            Toast.makeText(this, getText(it), Toast.LENGTH_SHORT).show()
        }
    }

    private inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                NAVIGATE_TO_DETAILS -> navigateToDetails(intent)
                ACTION_BORING_KILLER_NOTIFICATION_FILM -> startActivity(intent)
            }
        }
    }

    companion object {
        private const val TIME_INTERVAL = 2000L
        private const val INITIAL_TIME = 0L
        private const val ZERO_TIME = 0
        private const val MILLISECONDS_OF_DAY = 86399999L
        private const val LOTTIE_ANIMATION_SPEED = 0.7F
        private const val CIRCULAR_ANIMATION_DELAY = 200L
        private const val CIRCULAR_ANIMATION_DURATION = 500L
        private const val PERMISSION_REQUEST_CODE = 2
    }
}