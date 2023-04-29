package xyz.flussigkatz.searchmovie.data

import androidx.paging.*
import kotlinx.coroutines.flow.map
import xyz.flussigkatz.core_api.db.FilmDao
import xyz.flussigkatz.core_api.entity.*
import xyz.flussigkatz.searchmovie.data.ConstantsApp.BROWSING_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.MARKED_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NOW_PLAYING_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SEARCHED_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.UPCOMING_CATEGORY
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.di.AppScope
import javax.inject.Inject

@AppScope
class MainRepository @Inject constructor(
    private val filmDao: FilmDao,
    private val remoteMediatorFactory: FilmRemoteMediator.Factory
) {
    suspend fun insertBrowsingFilm(film: BrowsingFilm) {
        filmDao.insertBrowsingFilm(film)
    }

    suspend fun insertMarkedFilms(films: List<MarkedFilm>) {
        filmDao.insertMarkedFilms(films)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getFilms(category: String, query: String?) = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE
        ),
        remoteMediator = getRemoteMediator(category, query),
        pagingSourceFactory = {
            @Suppress("UNCHECKED_CAST")
            when (category) {
                POPULAR_CATEGORY -> filmDao.getPopularFilms()
                TOP_RATED_CATEGORY -> filmDao.getTopRatedFilms()
                UPCOMING_CATEGORY -> filmDao.getUpcomingFilms()
                NOW_PLAYING_CATEGORY -> filmDao.getNowPlayingFilms()
                SEARCHED_CATEGORY -> filmDao.getSearchedFilms()
                MARKED_CATEGORY -> filmDao.getMarkedFilms(query.orEmpty())
                BROWSING_CATEGORY -> filmDao.getBrowsingFilms(query.orEmpty())
                else -> throw IllegalArgumentException(EXCEPTION_MESSAGE)
            } as PagingSource<Int, IFilm>

        }
    ).flow.map { pagingData -> pagingData.map { FilmUiModel(it) } }

    fun getIdsMarkedFilms() = filmDao.getIdsMarkedFilms()

    fun getMarkedFilmById(id: Int) = filmDao.getMarkedFilmById(id)

    private fun getRemoteMediator(category: String, query: String?) = when(category) {
        MARKED_CATEGORY, BROWSING_CATEGORY -> null
        else -> remoteMediatorFactory.create(category, query)
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 2
        private const val EXCEPTION_MESSAGE = "Wrong film category"
    }
}