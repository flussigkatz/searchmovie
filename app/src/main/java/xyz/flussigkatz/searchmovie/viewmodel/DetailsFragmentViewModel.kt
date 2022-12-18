package xyz.flussigkatz.searchmovie.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.BrowsingFilm
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.domain.Interactor
import java.io.IOException
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalPagingApi
class DetailsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    suspend fun loadFilmPoster(urlAddress: String): Bitmap? {
        return suspendCoroutine {
            val bitmap: Bitmap? = try {
                BitmapFactory.decodeStream(URL(urlAddress).openConnection().getInputStream())
            } catch (e: IOException) {
                Timber.d(e)
                null
            }
            it.resume(bitmap)
        }
    }

    suspend fun changeFavoriteMark(id: Int, flag: Boolean) = interactor.changeFavoriteMark(id, flag)

    suspend fun getFilmMarkStatus(id: Int) = interactor.getFilmMarkStatus(id)

    suspend fun insertBrowsingFilm(film: BrowsingFilm) {
        interactor.insertBrowsingFilm(film)
    }

    fun postMessage(@StringRes message: Int) {
        interactor.postMessage(message)
    }
}