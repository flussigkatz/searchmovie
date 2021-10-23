package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.domain.Interactor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val channelRefreshState: Channel<Boolean>
    val channelEventMessage: Channel<String>
    val filmListData: Flow<List<Film>>

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmListData = interactor.getFilmsFromDB()
        channelRefreshState = interactor.getChannelRefreshState()
        channelEventMessage = interactor.getChannelEventMessage()
        getFilms()
    }

    fun getFilms() {
        interactor.getFilmsFromApi(1)
    }
}