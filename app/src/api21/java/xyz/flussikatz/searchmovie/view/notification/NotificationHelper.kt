package xyz.flussikatz.searchmovie.view.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.SearchMovieReceiver
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.view.fragments.DetailsFragment

object NotificationHelper {
    lateinit var notification: Notification.Builder

    fun initNotification(context: Context) {
            @Suppress("DEPRECATION")
            notification = Notification.Builder(context)
    }

    fun createBoringKillerNotification(context: Context, bundle: Bundle) {
        val film =
            bundle.getParcelable<Film>(DetailsFragment.DETAILS_FILM_KEY)
        if (film != null) {
            val notificationManager = NotificationManagerCompat.from(context)
            val intentBoringKillerInit = Intent()
            intentBoringKillerInit.action =
                NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY
            intentBoringKillerInit.putExtra(
                NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY, bundle
            )
            val pendingIntentInit = PendingIntent.getBroadcast(
                context,
                NotificationConstants.PENDINGINTENT_INIT_REQUEST_CODE,
                intentBoringKillerInit,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            notification.setContentTitle(context.getString(R.string.boring_killer_title))
                .setContentText(context.getString(R.string.boring_killer_text) + film.title)
                .setContentIntent(pendingIntentInit)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
            notificationManager.notify(
                NotificationConstants.BORING_KILLER_NOTIFICATION_ID,
                notification.build()
            )
        }
    }

}