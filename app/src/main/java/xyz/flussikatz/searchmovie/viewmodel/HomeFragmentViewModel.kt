package xyz.flussikatz.searchmovie.viewmodel

import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.ApiCallback
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.domain.Interactor
import xyz.flussikatz.searchmovie.util.SingleLiveEvent
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    private val coroutineScope: CoroutineScope
    val loadInProgressChannel = Channel<Boolean>(Channel.CONFLATED)
    val filmListLiveData: LiveData<List<Film>>
//    val inProgress = MutableLiveData<Boolean>()
    val errorEvent = SingleLiveEvent<String>()

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmListLiveData = interactor.getFilmsFromDB()
        coroutineScope = interactor.getCoroutinesScope()
        getFilms()
    }


    fun getFilms() {
        setProgressBarState(true)
        val realTime = System.currentTimeMillis()
        val lastLoadTime = interactor.getLoadFromApiTimeIntervalFromPreferences()
        if (lastLoadTime + TIME_INTERVAL < realTime) {
            interactor.getFilmsFromApi(1, object : ApiCallback {
                override fun onSuccess() {
                    setProgressBarState(false)
                    interactor.saveLoadFromApiTimeIntervalToPreferences(System.currentTimeMillis())
                }

                override fun onFailure() {
                    setProgressBarState(false)
                    errorEvent.postValue(getText(R.string.error_upload_message))
                }

            })
        } else {
            setProgressBarState(false)
            val timeFormatted = timeFormatter(realTime - lastLoadTime)
            errorEvent.postValue(
                timeFormatted + getText(R.string.upload_time_interval_massage)
            )

        }
    }

    /*private fun setProgressBarState(state: Boolean) {
        inProgress.postValue(state)
    }*/
    private fun setProgressBarState(state: Boolean) {
            coroutineScope.launch {
                loadInProgressChannel.send(state)
            }
    }

    fun getCoroutinesScope(): CoroutineScope {
        return interactor.getCoroutinesScope()
    }

    private fun timeFormatter(time: Long): String {
        val min = DateFormat.format("mm", time)
        val sec = DateFormat.format("ss", time)
        val arr = arrayOf(min, sec).map {
            if (it[0].toString().equals("0")) {
                it[1].toString()
            } else it
        }
        var res = ""

        if (time >= ONE_MIN) {
            res = "${arr[0]} ${getText(R.string.min)} "
        }
        res += "${arr[1]} ${getText(R.string.sec)} "

        return res
    }

    private fun getText(resId: Int): String {
        return App.instance.getText(resId).toString()
    }

    override fun onCleared() {
        loadInProgressChannel.close()
        super.onCleared()
    }


    companion object {
        private const val TIME_INTERVAL = 600000L
        private const val ONE_MIN = 60000L
    }
}