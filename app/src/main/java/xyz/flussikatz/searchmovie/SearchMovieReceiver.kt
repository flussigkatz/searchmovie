package xyz.flussikatz.searchmovie

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import xyz.flussikatz.searchmovie.view.notification.NotificationConstants
import xyz.flussikatz.searchmovie.view.notification.NotificationHelper


class SearchMovieReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle =
            intent.getBundleExtra(NotificationConstants.BORING_KILLER_NOTIFICATION_FILM_KEY)
        if (bundle != null) NotificationHelper.createBoringKillerNotification(context, bundle)
    }
}