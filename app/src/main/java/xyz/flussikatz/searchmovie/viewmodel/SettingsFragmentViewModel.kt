package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.domain.Interactor
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val categoryPropertyLifeData: MutableLiveData<String> = MutableLiveData()
    val themePropertyLifeData: MutableLiveData<Int> = MutableLiveData()

    init {
        App.instance.dagger.inject(this)
        getCategoryProperty()
        getThemeProperty()
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
}