package xyz.flussikatz.searchmovie.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.databinding.ActivityMainBinding
import xyz.flussikatz.searchmovie.domain.Interactor
import xyz.flussikatz.searchmovie.util.Converter
import xyz.flussikatz.searchmovie.view.fragments.DetailsFragment
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var preferences: PreferenceProvider

    @Inject
    lateinit var interactor: Interactor
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification.Builder
    private val scope = CoroutineScope(Dispatchers.IO)
    private val receiver = Receiver()

    init {
        App.instance.dagger.inject(this)
    }

    lateinit var navController: NavController
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootActivityMain)

        initNotification()

        initBoringNotification()

        val filter = IntentFilter().also {
            it.addAction(BORING_KILLER_NOTIFICATION_FILM_KEY)
            it.addAction(BORING_KILLER_NOTIFICATION_KEY_OFF)
        }

        registerReceiver(receiver, filter)

        //TODO: Deal with navigation backstack
        navController = Navigation.findNavController(
            this@MainActivity,
            R.id.nav_host_fragment
        )

        //TODO: Make control it from settings
        if (preferences.getPlaySplashScreenState()) {
            initSplashScreen()
            preferences.setPlaySplashScreenState(false)
        }
    }


    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        val last = navController.backStack.last.destination.label
        if (!HOME_FRAGMENT_LABEL.equals(last)) {
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

    override fun onStart() {
        super.onStart()
        val bundle = intent.getBundleExtra(BORING_KILLER_NOTIFICATION_FILM_KEY)
        if (bundle != null) { navController.navigate(R.id.action_global_detailsFragment, bundle) }
    }

    override fun onDestroy() {
        AnimationHelper.cancelAnimScope()
        scope.cancel()
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    fun initSplashScreen() {
        binding.splashScreen.visibility = View.VISIBLE
        val lottieAnimationView: LottieAnimationView = binding.splashScreen
        lottieAnimationView.speed = LOTTIE_ANIMATION_SPEED
        lottieAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            val viewHomeFragment = this@MainActivity
                .findViewById<CoordinatorLayout>(R.id.root_fragment_home)

            override fun onAnimationStart(animation: Animator?) {
                viewHomeFragment.visibility = View.INVISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                endAnimationSplashScreen()
            }

            override fun onAnimationCancel(animation: Animator?) {
                endAnimationSplashScreen()
            }

            fun endAnimationSplashScreen() {
                AnimationHelper.lottieCoverAnimation(binding.splashScreen, viewHomeFragment)
            }
        })
        binding.splashScreen.setOnClickListener { lottieAnimationView.cancelAnimation() }
        lottieAnimationView.playAnimation()
    }

    fun initNotification() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Notification_Channel_1"
            val channelName = "BORING KILLER"
            val descriptionText = "Remember watch movie from marked list"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val nChannel = NotificationChannel(channelId, channelName, importance)
            nChannel.description = descriptionText
            notificationManager.createNotificationChannel(nChannel)
            notification = Notification.Builder(this, channelId)
                .setTimeoutAfter(60000)
        } else {
            @Suppress("DEPRECATION")
            notification = Notification.Builder(this)
        }
    }

    fun initBoringNotification() {
        if (checkBoringKillerState()) {
            scope.launch {
                var stopCallback = false
                val def = async { getMarkedFilm() }
                val markedFilm = def.await()
                if (markedFilm != null) {
                    val bundle = Bundle()
                    val intentBoringKillerInit = Intent()
                    intentBoringKillerInit.action = BORING_KILLER_NOTIFICATION_FILM_KEY
                    bundle.putParcelable(
                        DetailsFragment.DETAILS_FILM_KEY,
                        markedFilm
                    )
                    intentBoringKillerInit.putExtra(BORING_KILLER_NOTIFICATION_FILM_KEY, bundle)
                    val pendingIntentInit = PendingIntent.getBroadcast(
                        this@MainActivity,
                        0,
                        intentBoringKillerInit,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    notification.setContentTitle("Boring?")
                        .setContentText("Watch ${markedFilm.title}!")
                        .setContentIntent(pendingIntentInit)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val intentBoringKillerOff = Intent()
                        intentBoringKillerOff.action = BORING_KILLER_NOTIFICATION_KEY_OFF
                        val pendingIntentOff = PendingIntent.getBroadcast(
                            this@MainActivity,
                            0,
                            intentBoringKillerOff,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        val actionBoringNotificationOff = Notification.Action.Builder(
                            null,
                            "Off this notification",
                            pendingIntentOff
                        ).build()
                        notification.addAction(actionBoringNotificationOff)
                    }
                    while (!stopCallback) {
                    delay(BORING_KILLER_NOTIFICATION_DELAY)
                    if (RANDOM_CONST == (0..9).random()) {
                        notificationManager.notify(
                            BORING_KILLER_NOTIFICATION_ID,
                            notification.build()
                        )
                    stopCallback = true
                    }
                    }
                }
            }
        }
    }

    private suspend fun getMarkedFilm(): Film? {
        return suspendCoroutine {
            var list = listOf<Film>()
            interactor.getMarkedFilmsFromDBToList()
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    if (!list.isEmpty()) {
                        it.resume(list.get((0..list.size - 1).random()))
                    } else {
                        it.resume(null)
                    }
                }.map { Converter.convertToFilm(it) }
                .subscribe {
                    list = it
                }
        }
    }

    private fun startDetailsMarkedFilm() {
        navController.navigate(
            R.id.action_global_detailsFragment,
            intent?.getBundleExtra(
                BORING_KILLER_NOTIFICATION_FILM_KEY
            )
        )
    }

    private fun checkBoringKillerState(): Boolean {
        if (preferences.getBoringKillerNotificationState()) {
            if (
                preferences.getBoringKillerNotificationTimeInterval() <=
                System.currentTimeMillis()
            ) {
                return true
            }
        }
        return false
    }

        inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BORING_KILLER_NOTIFICATION_FILM_KEY -> {
                    val activityStartIntent = Intent(context, MainActivity::class.java)
                    val bundle = intent.getBundleExtra(
                        BORING_KILLER_NOTIFICATION_FILM_KEY)
                    this@MainActivity.intent.putExtra(BORING_KILLER_NOTIFICATION_FILM_KEY, bundle)
                    when(this@MainActivity.lifecycle.currentState) {
                        Lifecycle.State.RESUMED -> startDetailsMarkedFilm()
                        else -> startActivity(activityStartIntent)
                    }
                    preferences.setBoringKillerNotificationTimeInterval()
                }
                BORING_KILLER_NOTIFICATION_KEY_OFF -> {
                    preferences.setBoringKillerNotificationState(false)
                    notificationManager.cancel(BORING_KILLER_NOTIFICATION_ID)
                }
            }
        }
    }

    companion object {
        private const val LOTTIE_ANIMATION_SPEED = 0.7F
        private const val RANDOM_CONST = 6
        private const val HOME_FRAGMENT_LABEL = "fragment_home"
        private const val BORING_KILLER_NOTIFICATION_FILM_KEY = "boring_killer_notification_film_key"
        private const val BORING_KILLER_NOTIFICATION_KEY_OFF = "boring_killer_notification_off"
        private const val BORING_KILLER_NOTIFICATION_ID = 456
        private const val BORING_KILLER_NOTIFICATION_DELAY = 60000L
        private const val TIME_INTERVAL = 2000L
    }

}