package xyz.flussikatz.searchmovie.domain

import xyz.flussikatz.searchmovie.viewmodel.HomeFragmentViewModel


interface Interactor {
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback)
}