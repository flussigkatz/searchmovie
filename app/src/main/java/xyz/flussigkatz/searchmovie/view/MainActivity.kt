package xyz.flussigkatz.searchmovie.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.ExperimentalPagingApi
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.databinding.ActivityMainBinding
import xyz.flussigkatz.searchmovie.util.AnimationHelper
import xyz.flussigkatz.searchmovie.util.NavigationHelper
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants
import xyz.flussigkatz.searchmovie.viewmodel.MainActivityViewModel

@ExperimentalPagingApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManagerCompat
    private val viewModel: MainActivityViewModel by viewModels()
    private val receiver = Receiver()
    lateinit var navController: NavController
    private var backPressedTime = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootActivityMain)
        initReceiver()
        initAnimationHelper()
        initNavigation()
        initEventMessage()
    }

    override fun onBackPressed() {
        val onScreenFragmentId = navController.backQueue.last().destination.id
        if (R.id.homeFragment != onScreenFragmentId) super.onBackPressed()
        else {
            if (backPressedTime + TIME_INTERVAL > System.currentTimeMillis()) finish()
            else Toast.makeText(this, R.string.exit_message, Toast.LENGTH_SHORT).show()
            backPressedTime = System.currentTimeMillis()
        }
    }

    override fun onStart() {
        super.onStart()
        startDetailsMarkedFilm(intent)
    }

    override fun onStop() {
        unregisterReceiver(receiver)
        super.onStop()
    }

    private fun initAnimationHelper() {
        viewModel.getSplashScreenStateStatus().let {
            findViewById<CoordinatorLayout>(R.id.root_fragment_home)?.let { view ->
                if (it) AnimationHelper.initSplashScreen(binding.splashScreen, view)
                else AnimationHelper.firstStartAnimation(view)
            }
        }
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

    private fun startDetailsMarkedFilm(intent: Intent?) {
        intent?.getBundleExtra(NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY)?.let {
            navController.navigate(R.id.action_markedFragment_to_detailsFragment, it)
        }
    }

    private fun initReceiver() {
        val filter = IntentFilter().apply {
            addAction(NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY)
            addAction(NotificationConstants.BORING_KILLER_NOTIFICATION_OFF_KEY)
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
                NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY -> {
                    val activityStartIntent = Intent(context, MainActivity::class.java)
                    val bundle = intent.getBundleExtra(
                        NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY
                    )
                    this@MainActivity.intent.putExtra(
                        NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY,
                        bundle
                    )
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
    }
}