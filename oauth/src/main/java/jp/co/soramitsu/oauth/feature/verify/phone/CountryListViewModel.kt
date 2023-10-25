package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.model.CountryDial
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CountryListState(
    val list: List<CountryDial>,
    val loading: Boolean,
)

@HiltViewModel
class CountryListViewModel @Inject constructor(
    private val kycRepository: KycRepository,
    private val mainRouter: MainRouter,
) : BaseViewModel() {

    private val _state = MutableStateFlow(CountryListState(emptyList(), true))
    val state = _state.asStateFlow()

    private val all = mutableListOf<CountryDial>()

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.select_your_country,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
                searchValue = "",
                searchEnabled = true,
            ),
        )
        viewModelScope.launch {
            all.clear()
            all.addAll(kycRepository.getCountries())
            filterCountries("")
        }
    }

    private fun filterCountries(value: String) {
        _state.value = _state.value.copy(
            loading = false,
            list = all.filter { cd ->
                cd.code.lowercase().contains(value) || cd.name.lowercase().contains(value)
            },
        )
    }

    override fun onToolbarSearch(value: String) {
        filterCountries(value.lowercase())
    }

    override fun onToolbarNavigation() {
        mainRouter.back()
    }

    fun onSelect(index: Int) {
        mainRouter.backWithCountry(all[index].code)
    }
}
