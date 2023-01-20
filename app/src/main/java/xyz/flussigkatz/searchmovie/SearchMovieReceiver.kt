package xyz.flussigkatz.searchmovie

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.view.notification.NotificationHelper.createBoringKillerNotification


class SearchMovieReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        intent.getBundleExtra(DETAILS_FILM_KEY)?.let {
            createBoringKillerNotification(context, it)
        }
    }
}