package xyz.flussigkatz.searchmovie.view.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.view.fragments.DetailsFragment

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
                val intentBoringKillerOff = Intent()
                intentBoringKillerOff.action =
                    NotificationConstants.BORING_KILLER_NOTIFICATION_OFF_KEY
                val pendingIntentOff = PendingIntent.getBroadcast(
                    context,
                    NotificationConstants.PENDINGINTENT_OFF_REQUEST_CODE,
                    intentBoringKillerOff,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val actionBoringNotificationOff = Notification.Action.Builder(
                    null,
                    context.getString(R.string.boring_killer_button_text),
                    pendingIntentOff
                ).build()
                notification.addAction(actionBoringNotificationOff)
            notificationManager.notify(
                NotificationConstants.BORING_KILLER_NOTIFICATION_ID,
                notification.build()
            )
        }
    }

}