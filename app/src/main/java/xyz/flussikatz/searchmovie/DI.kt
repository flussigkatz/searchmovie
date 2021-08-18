package xyz.flussikatz.searchmovie

import xyz.flussikatz.searchmovie.data.MainRepository
import xyz.flussikatz.searchmovie.domain.Remote
import xyz.flussikatz.searchmovie.data.TmdbApi
import xyz.flussikatz.searchmovie.domain.MainInteractor

object DI {
    /*val mainModule = module {
        single { MainRepository() }
        single<TmdbApi> { Remote.retrofitService }
        single { MainInteractor(get()) }
    }*/
}