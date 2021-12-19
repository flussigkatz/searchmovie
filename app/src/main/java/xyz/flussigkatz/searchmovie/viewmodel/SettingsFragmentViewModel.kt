package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val categoryPropertyLifeData: MutableLiveData<String> = MutableLiveData()
    val themePropertyLifeData: MutableLiveData<Int> = MutableLiveData()
    val splashScreenPropertyLifeData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        xyz.flussigkatz.searchmovie.App.instance.dagger.inject(this)
        getCategoryProperty()
        getThemeProperty()
        getSplashScreenProperty()
    }

    private fun getCategoryProperty() {
        categoryPropertyLifeData.value = interactor.getDefaultCategoryFromPreferences()
    }

    fun putCategoryProperty(category: String) {
        interactor.saveDefaultCategoryToPreferences(category)
        getCategoryProperty()
    }

    fun getThemeProperty() {
        themePropertyLifeData.value = interactor.getDefaultThemeFromPreferences()
    }

    fun putThemeProperty(theme: Int) {
        interactor.setDefaultTheme(theme)
        getThemeProperty()
    }

    fun getSplashScreenProperty() {
        splashScreenPropertyLifeData.value = interactor.getSplashScreenStateFromPreferences()
    }

    fun putSplashScreenProperty(state: Boolean) {
        interactor.setSplashScreenState(state)
        getSplashScreenProperty()
    }
}