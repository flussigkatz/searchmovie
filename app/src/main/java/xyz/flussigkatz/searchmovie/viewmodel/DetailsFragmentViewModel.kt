package xyz.flussigkatz.searchmovie.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.io.IOException
import java.lang.Exception
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DetailsFragmentViewModel : ViewModel() {
    val progressBarState = BehaviorSubject.create<Boolean>()

    suspend fun loadFilmPoster(url: String): Bitmap? {
        return suspendCoroutine {
            var bitmap: Bitmap?
            try {
                val url = URL(url)
                //TODO If you press the download with the Internet disconnected
                // immediately after a successful download, then the coroutine freezes
                // in anticipation of the result.
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: IOException) {
                println(e)
                bitmap = null
            }
            it.resume(bitmap)
        }
    }
}