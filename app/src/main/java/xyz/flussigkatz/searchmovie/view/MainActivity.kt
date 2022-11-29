package xyz.flussigkatz.searchmovie.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import xyz.flussigkatz.core_api.entity.AbstractFilmEntity
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.SearchMovieReceiver
import xyz.flussigkatz.searchmovie.databinding.ActivityMainBinding
import xyz.flussigkatz.searchmovie.domain.Interactor
import xyz.flussigkatz.searchmovie.util.AnimationHelper
import xyz.flussigkatz.searchmovie.util.NavigationHelper
import xyz.flussigkatz.searchmovie.view.fragments.DetailsFragment
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var interactor: Interactor
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManagerCompat
    private val scope = CoroutineScope(Dispatchers.IO)
    private val receiver = Receiver()
    lateinit var navController: NavController
    private var backPressedTime = 0L

    init {
        App.instance.dagger.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootActivityMain)
        initReceiver()
        initAnimationHelper()
        initNavigation()
    }

    override fun onBackPressed() {
        val onScreenFragmentId = navController.backQueue.last().destination.id
        if (R.id.homeFragment != onScreenFragmentId) {
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
        startDetailsMarkedFilm(intent)
    }

    override fun onDestroy() {
        scope.cancel()
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun initAnimationHelper() {
        val viewHomeFragment = findViewById<CoordinatorLayout>(R.id.root_fragment_home)
        if (interactor.getSplashScreenStateFromPreferences()) {
            AnimationHelper.initSplashScreen(binding.splashScreen, viewHomeFragment)
        } else AnimationHelper.firstStartAnimation(viewHomeFragment)
    }

    private fun initNavigation() {
        navController = Navigation.findNavController(
            this@MainActivity,
            R.id.nav_host_fragment
        )
        binding.mainBottomToolbar.setOnItemSelectedListener {
            val onScreenFragmentId = navController.backQueue.last().destination.id
            NavigationHelper.navigate(navController, it.itemId, onScreenFragmentId)
            true
        }
    }

    private fun initBoringNotification() {
        notificationManager = NotificationManagerCompat.from(this)
        interactor.getMarkedFilmsFromDB()
            .filter { !it.isNullOrEmpty() }
            .subscribeOn(Schedulers.io())
            .doOnError { println("initBoringNotification ${it.localizedMessage}") }
            .subscribe({ setWatchFilmReminder(this@MainActivity, it.random()) },
                { println("$TAG initBoringNotification onError: ${it.localizedMessage}") })
    }

    private fun startDetailsMarkedFilm(intent: Intent?) {
        val bundle = intent?.getBundleExtra(
            NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY
        )
        if (bundle != null) {
            navController.navigate(R.id.action_global_detailsFragment, bundle)
        }
    }

    private fun setWatchFilmReminder(context: Context, film: AbstractFilmEntity) {
        val bundle = Bundle()
        val intentBoringKillerAlarm = Intent(
            context,
            SearchMovieReceiver::class.java
        )
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
            System.currentTimeMillis() + 10,
            pendingIntentAlarm
        )
    }

    private fun initReceiver() {
        val filter = IntentFilter().also {
            it.addAction(NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY)
            it.addAction(NotificationConstants.BORING_KILLER_NOTIFICATION_OFF_KEY)
        }

        registerReceiver(receiver, filter)
    }

    private fun initTheme() {
        AppCompatDelegate.setDefaultNightMode(interactor.getNightModeFromPreferences())
    }

    private inner class Receiver : BroadcastReceiver() {
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
                        Lifecycle.State.RESUMED -> startDetailsMarkedFilm(intent)
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
        private const val TIME_INTERVAL = 2000L
        private const val TAG = "MainActivity"
    }
}