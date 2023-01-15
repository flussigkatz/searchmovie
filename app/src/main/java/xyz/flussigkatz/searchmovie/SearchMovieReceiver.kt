package xyz.flussigkatz.searchmovie

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import xyz.flussigkatz.searchmovie.view.notification.NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY
import xyz.flussigkatz.searchmovie.view.notification.NotificationHelper.createBoringKillerNotification


class SearchMovieReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.getBundleExtra(BORING_KILLER_NOTIFICATION_FILM_KEY)
        if (bundle != null) createBoringKillerNotification(context, bundle)
    }
}