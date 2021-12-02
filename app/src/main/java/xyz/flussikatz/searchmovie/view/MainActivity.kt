package xyz.flussikatz.searchmovie.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.SearchMovieReceiver
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.databinding.ActivityMainBinding
import xyz.flussikatz.searchmovie.domain.Interactor
import xyz.flussikatz.searchmovie.util.Converter
import xyz.flussikatz.searchmovie.view.fragments.DetailsFragment
import xyz.flussikatz.searchmovie.view.notification.NotificationConstants
import xyz.flussikatz.searchmovie.view.notification.NotificationHelper
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var preferences: PreferenceProvider

    @Inject
    lateinit var interactor: Interactor
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManagerCompat
//    private val notification = NotificationHelper.notification
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

        initBoringNotification()

        val filter = IntentFilter().also {
            it.addAction(NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY)
            it.addAction(NotificationConstants.BORING_KILLER_NOTIFICATION_OFF_KEY)
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
        val bundle = intent.getBundleExtra(
            NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY
        )
        if (bundle != null) {
            navController.navigate(R.id.action_global_detailsFragment, bundle)
        }
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

    fun initBoringNotification() {
        var film: Film? = null
        notificationManager = NotificationManagerCompat.from(this)
        scope.launch {
            interactor.getMarkedFilmsFromDBToList()
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    if (film != null) {
                        setWatchFilmReminder(this@MainActivity, film!!)
//                        )
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "No one marked film",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }.map { Converter.convertToFilm(it) }
                .subscribe {
                    film = it.get((0..it.size - 1).random())
                }
        }
    }

    private fun startDetailsMarkedFilm() {
        navController.navigate(
            R.id.action_global_detailsFragment,
            intent?.getBundleExtra(
                NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY
            )
        )
    }

    fun setWatchFilmReminder(context: Context, film: Film) {
        val bundle = Bundle()
        val intentBoringKillerAlarm = Intent(context, SearchMovieReceiver::class.java)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        intentBoringKillerAlarm.action = NotificationConstants.BORING_KILLER_NOTIFICATION_ALARM
        bundle.putParcelable(
            DetailsFragment.DETAILS_FILM_KEY,
            film
        )
        intentBoringKillerAlarm.putExtra(
            NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY, bundle
        )
        val pendingIntentAlarm = PendingIntent.getBroadcast(
            context,
            NotificationConstants.PENDINGINTENT_ALARM_REQUEST_CODE,
            intentBoringKillerAlarm,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.set(
            AlarmManager.RTC,
            System.currentTimeMillis() + 10 * 1000,
            pendingIntentAlarm
        )
    }

    inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY -> {
                    val activityStartIntent = Intent(context, MainActivity::class.java)
                    val bundle = intent.getBundleExtra(
                        NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY)
                    this@MainActivity.intent.putExtra(
                        NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY,
                        bundle)
                    when (this@MainActivity.lifecycle.currentState) {
                        Lifecycle.State.RESUMED -> startDetailsMarkedFilm()
                        else -> startActivity(activityStartIntent)
                    }
                }
                NotificationConstants.BORING_KILLER_NOTIFICATION_OFF_KEY -> {
                    notificationManager.cancel(NotificationConstants.BORING_KILLER_NOTIFICATION_ID)
                }
            }
        }
    }

    companion object {
        private const val LOTTIE_ANIMATION_SPEED = 0.7F
        private const val HOME_FRAGMENT_LABEL = "fragment_home"
        private const val TIME_INTERVAL = 2000L
    }

}