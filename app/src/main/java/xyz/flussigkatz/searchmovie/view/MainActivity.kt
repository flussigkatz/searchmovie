package xyz.flussigkatz.searchmovie.view

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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.SearchMovieReceiver
import xyz.flussigkatz.searchmovie.data.ApiConstantsApp.IMAGES_URL
import xyz.flussigkatz.searchmovie.data.ApiConstantsApp.IMAGE_FORMAT_W500
import xyz.flussigkatz.searchmovie.data.entity.Film
import xyz.flussigkatz.searchmovie.databinding.ActivityMainBinding
import xyz.flussigkatz.searchmovie.domain.Interactor
import xyz.flussigkatz.searchmovie.util.*
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
    private var film: Film? = null
    lateinit var bottomSheetPoster: BottomSheetBehavior<LinearLayout>

    init {
        App.instance.dagger.inject(this)
    }

    lateinit var navController: NavController
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootActivityMain)

//        initBoringNotification()

        initRecommendationFromRemoteConfig()

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

        initSplashScreen()

        binding.mainBottomToolbar.setOnItemSelectedListener {
            val onScreenFragmentId = navController.backStack.last.destination.id
            NavigationHelper.navigate(navController, it.itemId, onScreenFragmentId, this)
            true
        }
    }


    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        val onScreenFragmentId = navController.backStack.last.destination.id
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
        SplashScreenHelper.cancelAnimScope()
        scope.cancel()
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun initSplashScreen() {
        val viewHomeFragment = findViewById<CoordinatorLayout>(R.id.root_fragment_home)
        if (interactor.getSplashScreenStateFromPreferences()) {
            SplashScreenHelper.initSplashScreen(binding.splashScreen, viewHomeFragment)
        } else {
            viewHomeFragment.visibility = View.INVISIBLE
            SplashScreenHelper.revealAnimation(viewHomeFragment)
        }
    }

    fun initBoringNotification() {
        var film: Film? = null
        notificationManager = NotificationManagerCompat.from(this)
        scope.launch {
            val markedFilm = interactor.getMarkedFilmsFromDBToList()
            markedFilm?.subscribeOn(Schedulers.io())?.doOnComplete {
                if (film != null) {
                    setWatchFilmReminder(this@MainActivity, film!!)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "No one marked film",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }?.map { Converter.convertToFilm(it) }?.subscribe {
                film = it.get((0..it.size - 1).random())
            }
        }
    }

    fun initRecommendationFromRemoteConfig() {
        bottomSheetPoster = BottomSheetBehavior.from(binding.mainBottomSheetPoster)
        bottomSheetPoster.state = BottomSheetBehavior.STATE_HIDDEN
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val firebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder().build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(firebaseRemoteConfigSettings)
        mFirebaseRemoteConfig.fetch(0).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                mFirebaseRemoteConfig.activate()
                val filmId = mFirebaseRemoteConfig.getString("film")
                if (!filmId.isBlank()) {
                    bottomSheetPoster.addBottomSheetCallback(object :
                        BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            when (newState) {
                                BottomSheetBehavior.STATE_COLLAPSED ->
                                    binding.bottomSheetText.text =
                                        getText(R.string.bottom_sheet_text_collapsed)
                                BottomSheetBehavior.STATE_EXPANDED ->
                                    binding.bottomSheetText.text =
                                        getText(R.string.bottom_sheet_text_expanded)
                            }
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        }

                    })
                    interactor.getSpecificFilmFromApi(filmId)
                        .subscribe {
                            film = it
                            bottomSheetPoster.state = BottomSheetBehavior.STATE_COLLAPSED
                            Picasso.get()
                                .load(IMAGES_URL + IMAGE_FORMAT_W500 + it.posterId)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.wait)
                                .error(R.drawable.err)
                                .into(binding.bottomSheetImage)
                            binding.bottomSheetImage.setOnClickListener {
                                val bundle = Bundle()
                                bundle.putParcelable(DetailsFragment.DETAILS_FILM_KEY, film)
                                val onScreenFragmentId = navController.backStack.last.destination.id
                                NavigationHelper.navigateToDetailsFragment(
                                    navController,
                                    onScreenFragmentId,
                                    bundle
                                )
                                bottomSheetPoster.state = BottomSheetBehavior.STATE_HIDDEN
                            }
                        }
                }
            }
        }
    }

    private fun startDetailsMarkedFilm(intent: Intent?) {
        val bundle = intent?.getBundleExtra(
            NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY
        )
        if (bundle != null) {
            navController.navigate(R.id.action_global_detailsFragment, bundle)
        }
    }

    fun setWatchFilmReminder(context: Context, film: Film) {
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
        private const val HOME_FRAGMENT_LABEL = "fragment_home"
        private const val TIME_INTERVAL = 2000L
    }

}