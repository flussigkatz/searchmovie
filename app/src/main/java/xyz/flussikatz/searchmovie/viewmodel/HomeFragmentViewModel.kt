package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.ApiCallback
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.domain.Interactor
import xyz.flussikatz.searchmovie.util.SingleLiveEvent
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val filmListLiveData: LiveData<List<Film>>
    val inProgress = MutableLiveData<Boolean>()
    val errorEvent = SingleLiveEvent<String>()

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmListLiveData = interactor.getFilmsFromDB()
        getFilms()
    }



    fun getFilms() {
        progressBarState(true)
        val realTime = System.currentTimeMillis()
        val lastLoadTime = interactor.getLoadFromApiTimeIntervalToPreferences()
        if (lastLoadTime + TIME_INTERVAL < realTime) {
            interactor.getFilmsFromApi(1, object : ApiCallback {
                override fun onSuccess() {
                    progressBarState(false)
                    interactor.saveLoadFromApiTimeIntervalToPreferences(System.currentTimeMillis())
                }

                override fun onFailure() {
                    progressBarState(false)
                    errorUploadInit(R.string.error_upload_message.toString())
                }

            })
        } else {
            progressBarState(false)
        }
    }

    fun progressBarState(state: Boolean) {
            inProgress.postValue(state)
    }

    fun errorUploadInit(errorMassage: String) {
            errorEvent.postValue(errorMassage)
    }


    companion object {
        private const val TIME_INTERVAL = 600000L
    }
}