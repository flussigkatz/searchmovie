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
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.databinding.ActivityMainBinding
import javax.inject.Inject

private const val LOTTIE_ANIMATION_SPEED = 0.7F
private const val HOME_FRAGMENT_LABEL = "fragment_home"

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var preferences: PreferenceProvider
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification.Builder
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

        notificationManager.notify(0, notification.build())

//        val filter = IntentFilter().also {
//            it.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
//            it.addAction(Intent.ACTION_BATTERY_LOW)
//            it.addAction(Intent.ACTION_POWER_CONNECTED)
//            it.addAction(Intent.ACTION_POWER_DISCONNECTED)
//        }
//
//        registerReceiver(receiver, filter)

        //TODO: Dal with navigation backstack
        navController = Navigation.findNavController(
            this@MainActivity,
            R.id.nav_host_fragment
        )

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

        //TODO: Make control it from settings
        if (preferences.getPlaySplashScreenState()) {
            binding.splashScreen.visibility = View.VISIBLE
            lottieAnimationView.playAnimation()
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
//        unregisterReceiver(receiver)
        super.onDestroy()
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
        } else {
            notification = Notification.Builder(this)
        }
        notification.setContentTitle("Title")
            .setContentText("Text")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
    }

    companion object {
        const val TIME_INTERVAL = 2000L
    }

}