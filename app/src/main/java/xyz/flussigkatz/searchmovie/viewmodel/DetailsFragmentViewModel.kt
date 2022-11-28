package xyz.flussigkatz.searchmovie.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.domain.Interactor
import java.io.IOException
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DetailsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val progressBarState: BehaviorSubject<Boolean> = BehaviorSubject.create()

    init {
        App.instance.dagger.inject(this)
    }

    suspend fun loadFilmPoster(urlAddress: String): Bitmap? {
        return suspendCoroutine {
            val bitmap: Bitmap? = try {
                val url = URL(urlAddress)
                BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: IOException) {
                println(e)
                null
            }
            it.resume(bitmap)
        }
    }

    fun removeFavoriteFilmFromList(id: Int){
        interactor.removeFavoriteFilmFromList(id)
    }

    fun addFavoriteFilmToList(id: Int){
        interactor.addFavoriteFilmToList(id)
    }

    fun getFilmMarkStatusFromApi(id: Int) = interactor.getFilmMarkStatusFromApi(id)
}