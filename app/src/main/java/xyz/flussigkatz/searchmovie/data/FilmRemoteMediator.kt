package xyz.flussigkatz.searchmovie.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult.Error
import androidx.paging.RemoteMediator.MediatorResult.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import xyz.flussigkatz.core_api.db.FilmDao
import xyz.flussigkatz.core_api.entity.*
import xyz.flussigkatz.remote_module.TmdbApi
import xyz.flussigkatz.searchmovie.data.Api.API_KEY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NOW_PLAYING_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SEARCHED_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.UPCOMING_CATEGORY
import java.util.*

@ExperimentalPagingApi
class FilmRemoteMediator @AssistedInject constructor(
    private val filmDao: FilmDao,
    private val retrofitService: TmdbApi,
    @Assisted("category") private val category: String,
    @Assisted("query") private val query: String?,
) : RemoteMediator<Int, IFilm>() {
    private val language = Locale.getDefault().run { "$language-$country" }
    private var pageIndex = FIRST_PAGE

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, IFilm>
    ): MediatorResult {
        pageIndex = getPageIndex(loadType) ?: return Success(endOfPaginationReached = true)

        return try {
            val listIdsMarkedFilms = withContext(Dispatchers.IO) { filmDao.getIdsMarkedFilms() }
            val films = fetchFilms(pageIndex, category, query.orEmpty())
            if (loadType == LoadType.REFRESH) refreshFilms(films, category, listIdsMarkedFilms)
            else insertFilms(films, category, listIdsMarkedFilms)
            Success(endOfPaginationReached = films.size < state.config.pageSize)
        } catch (e: Exception) {
            Timber.d(e)
            Error(e)
        }
    }

    private fun getPageIndex(loadType: LoadType) = when (loadType) {
        LoadType.APPEND -> ++pageIndex
        LoadType.PREPEND -> null
        LoadType.REFRESH -> FIRST_PAGE
    }

    private suspend fun fetchFilms(page: Int, category: String, query: String) = flow {
        emit(
            when (category) {
                SEARCHED_CATEGORY -> {
                    retrofitService.getSearchedFilms(
                        API_KEY,
                        language,
                        query,
                        page
                    ).tmdbFilms
                }
                else -> retrofitService.getFilms(
                    category,
                    API_KEY,
                    language,
                    page
                ).tmdbFilms
            }
        )
    }.catch {
        Timber.d(it)
        emit(listOf())
    }.flowOn(Dispatchers.IO).singleOrNull().orEmpty()

    private suspend fun insertFilms(
        films: List<IFilm>,
        category: String,
        listIdsMarkedFilms: List<Int>
    ) {
        when (category) {
            POPULAR_CATEGORY -> filmDao.insertPopularFilms(films.map {
                PopularFilm(it, listIdsMarkedFilms.contains(it.id))
            })
            TOP_RATED_CATEGORY -> filmDao.insertTopRatedFilms(films.map {
                TopRatedFilm(it, listIdsMarkedFilms.contains(it.id))
            })
            UPCOMING_CATEGORY -> filmDao.insertUpcomingFilms(films.map {
                UpcomingFilm(it, listIdsMarkedFilms.contains(it.id))
            })
            NOW_PLAYING_CATEGORY -> filmDao.insertNowPlayingFilms(films.map {
                NowPlayingFilm(it, listIdsMarkedFilms.contains(it.id))
            })
            SEARCHED_CATEGORY -> filmDao.insertSearchedFilms(films.map {
                SearchedFilm(it, listIdsMarkedFilms.contains(it.id))
            })
        }
    }

    private suspend fun refreshFilms(
        films: List<IFilm>,
        category: String,
        listIdsMarkedFilms: List<Int>
    ) {
        when (category) {
            POPULAR_CATEGORY -> filmDao.refreshPopularFilms(films.map {
                PopularFilm(it, listIdsMarkedFilms.contains(it.id))
            })
            TOP_RATED_CATEGORY -> filmDao.refreshTopRatedFilms(films.map {
                TopRatedFilm(it, listIdsMarkedFilms.contains(it.id))
            })
            UPCOMING_CATEGORY -> filmDao.refreshUpcomingFilms(films.map {
                UpcomingFilm(it, listIdsMarkedFilms.contains(it.id))
            })
            NOW_PLAYING_CATEGORY -> filmDao.refreshNowPlayingFilms(films.map {
                NowPlayingFilm(it, listIdsMarkedFilms.contains(it.id))
            })
            SEARCHED_CATEGORY -> filmDao.refreshSearchedFilms(films.map {
                SearchedFilm(it, listIdsMarkedFilms.contains(it.id))
            })
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("category") category: String,
            @Assisted("query") query: String?
        ): FilmRemoteMediator
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}