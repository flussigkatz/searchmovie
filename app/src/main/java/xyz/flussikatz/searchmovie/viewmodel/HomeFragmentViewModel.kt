package xyz.flussikatz.searchmovie.viewmodel

import android.text.format.DateFormat
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
                    errorEvent.postValue(getText(R.string.error_upload_message))
                }

            })
        } else {
            progressBarState(false)
            val timeFormatted = timeFormatter(realTime - lastLoadTime)
            errorEvent.postValue(
                timeFormatted + getText(R.string.upload_time_interval_massage)
            )

        }
    }

    fun progressBarState(state: Boolean) {
        inProgress.postValue(state)
    }

    fun timeFormatter(time: Long): String {
        var min = DateFormat.format("mm", time)
        var sec = DateFormat.format("ss", time)
        var arr = arrayOf(min, sec)
        var res = ""

        for (i in arr.indices) {
            if (arr[i].get(0).toString().equals("0")) {
                arr[i] = arr[i].get(1).toString()
            }
        }

        if (time < ONE_MIN) {
            res = "${arr[1]} ${getText(R.string.sec)}"
        } else {
            res = "${arr[0]} ${getText(R.string.min)} ${arr[1]} ${getText(R.string.sec)}"
        }

        return res
    }

    fun getText(resId: Int): String {
        return App.instance.getText(resId).toString()
    }


    companion object {
        private const val TIME_INTERVAL = 600000L
        private const val ONE_MIN = 60000L
    }
}