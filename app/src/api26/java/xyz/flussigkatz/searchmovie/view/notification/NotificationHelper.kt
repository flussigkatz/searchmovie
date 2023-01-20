package xyz.flussigkatz.searchmovie.view.notification

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.*
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.ACTION_BORING_KILLER_NOTIFICATION_FILM
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.ACTION_BORING_KILLER_NOTIFICATION_OFF
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.BORING_KILLER_NOTIFICATION_ID
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.NOTIFICATION_CHANNEL_ID
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.PENDINGINTENT_INIT_REQUEST_CODE
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.PENDINGINTENT_OFF_REQUEST_CODE


object NotificationHelper {
    private lateinit var notification: Notification.Builder

    fun initNotification(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        val channelName = "BORING_KILLER"
        val descriptionText = "Don't forget to watch a movie from the marked list"
        val nChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, IMPORTANCE_DEFAULT)
        nChannel.description = descriptionText
        notificationManager.createNotificationChannel(nChannel)
        notification = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
        val intentBoringKillerOff = Intent(context, MainActivity::class.java).apply {
            action = ACTION_BORING_KILLER_NOTIFICATION_OFF
        }
        val pendingIntentOff = PendingIntent.getActivity(
            context,
            PENDINGINTENT_OFF_REQUEST_CODE,
            intentBoringKillerOff,
            FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        )
        val actionBoringNotificationOff = Notification.Action.Builder(
            null,
            context.getString(R.string.boring_killer_button_text),
            pendingIntentOff
        ).build()
        notification.addAction(actionBoringNotificationOff)
    }

    @Suppress("DEPRECATION")
    fun createBoringKillerNotification(context: Context, bundle: Bundle) {
        bundle.getParcelable<FilmUiModel>(DETAILS_FILM_KEY)?.let {
            if (checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
                val notificationManager = NotificationManagerCompat.from(context)
                val intentBoringKillerInit = Intent(context, MainActivity::class.java).apply {
                    action = ACTION_BORING_KILLER_NOTIFICATION_FILM
                    putExtra(DETAILS_FILM_KEY, bundle)
                }
                val pendingIntentInit = PendingIntent.getActivity(
                    context,
                    PENDINGINTENT_INIT_REQUEST_CODE,
                    intentBoringKillerInit,
                    FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
                )
                notification.setContentTitle(context.getString(R.string.boring_killer_title))
                    .setContentText(context.getString(R.string.boring_killer_text) + it.title)
                    .setContentIntent(pendingIntentInit)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                notificationManager.notify(BORING_KILLER_NOTIFICATION_ID, notification.build())
            } else Toast.makeText(context, R.string.no_permission, Toast.LENGTH_SHORT).show()
        }
    }
}