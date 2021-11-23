package xyz.flussikatz.searchmovie.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.data.entity.MarkedFilm
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.databinding.ActivityMainBinding
import xyz.flussikatz.searchmovie.domain.Interactor
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random

private const val LOTTIE_ANIMATION_SPEED = 0.7F
private const val HOME_FRAGMENT_LABEL = "fragment_home"

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var preferences: PreferenceProvider
    @Inject
    lateinit var interactor: Interactor
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification.Builder
    private val scope = CoroutineScope(Dispatchers.IO)
//    private val receiver = Receiver()

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

//        val filter = IntentFilter().also {
//            it.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
//            it.addAction(Intent.ACTION_BATTERY_LOW)
//            it.addAction(Intent.ACTION_POWER_CONNECTED)
//            it.addAction(Intent.ACTION_POWER_DISCONNECTED)
//        }
//
//        registerReceiver(receiver, filter)

        //TODO: Deal with navigation backstack
        navController = Navigation.findNavController(
            this@MainActivity,
            R.id.nav_host_fragment
        )

        //TODO: Make control it from settings
        if (preferences.getPlaySplashScreenState()) {
            binding.splashScreen.visibility = View.VISIBLE
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

    override fun onDestroy() {
        AnimationHelper.cancelAnimScope()
        scope.cancel()
//        unregisterReceiver(receiver)
        super.onDestroy()
    }

    fun initSplashScreen() {
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

            @SuppressLint("RestrictedApi")
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
            val inmportance = NotificationManager.IMPORTANCE_DEFAULT
            val nChannel = NotificationChannel(channelId, channelName, inmportance)
            nChannel.description = descriptionText
            notificationManager.createNotificationChannel(nChannel)
            notification = Notification.Builder(this, channelId)
                .setTimeoutAfter(60000)
        } else {
            notification = Notification.Builder(this)
        }
        var list = listOf<MarkedFilm>()
        lateinit var film: MarkedFilm
        interactor.getMarkedFilmsFromDB()
            .subscribeOn(Schedulers.io())
            .subscribe {
                list = it
            }
        while (list.isEmpty()) {
            if (!list.isEmpty()) {
                film = list.get((1..list.size).random())
            }
        }
        notification.setContentTitle("Boring?")
            .setContentText("Watch ${film.title}!")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
    }

    fun initBoringNotification() {
        if (preferences.getBoringKillerNotificationState()) {
            scope.launch {
                while (true) {
//                delay(60000)
                    if ((0..9).random() == (0..9).random()) {
                        notificationManager.notify(0, notification.build())
                    }
                }
            }
        }
    }

    //    inner class Receiver : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            Toast.makeText(context, intent?.action, Toast.LENGTH_SHORT).show()
//
//                if (intent?.action == Intent.ACTION_BATTERY_LOW) {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            }
//        }
//    }

    companion object {
        const val TIME_INTERVAL = 2000L
    }

}