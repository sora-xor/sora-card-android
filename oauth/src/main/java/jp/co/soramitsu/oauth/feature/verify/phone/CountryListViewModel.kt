package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.Argument
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.model.CountryDial
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CountryListMode {
    data object SingleChoice : CountryListMode
    class MultiChoice(
        val selectedCodes: List<String>,
    ) : CountryListMode
}

data class CountryListState(
    val list: List<CountryDial>,
    val countryListMode: CountryListMode,
    val loading: Boolean,
)

@HiltViewModel
class CountryListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val kycRepository: KycRepository,
    private val mainRouter: MainRouter,
) : BaseViewModel() {

    private val _state = MutableStateFlow(
        CountryListState(
            list = emptyList(),
            countryListMode = requireNotNull(
                savedStateHandle[Argument.ADDITIONAL_DESCRIPTION.arg],
            ).let {
                if (it as Boolean) {
                    CountryListMode.SingleChoice
                } else {
                    CountryListMode.MultiChoice(
                        emptyList(),
                    )
                }
            },
            loading = true,
        ),
    )
    val state = _state.asStateFlow()

    private val all = mutableListOf<CountryDial>()

    init {
        mToolbarState.value = SoramitsuToolbarState(
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
        _state.update { cls ->
            cls.copy(
                loading = false,
                list = all.filter { cd ->
                    cd.code.lowercase().contains(value) || cd.name.lowercase()
                        .contains(value) || cd.dialCode.contains(value.replace("+", "").trim())
                },
            )
        }
    }

    override fun onToolbarSearch(value: String) {
        filterCountries(value.lowercase())
    }

    override fun onToolbarNavigation() {
        mainRouter.back()
    }

    fun onSelect(index: Int) {
        when (_state.value.countryListMode) {
            is CountryListMode.MultiChoice -> {
                val clickedCode = _state.value.list[index].code
                val mode = _state.value.countryListMode as CountryListMode.MultiChoice
                _state.update { cls ->
                    cls.copy(
                        countryListMode = CountryListMode.MultiChoice(
                            selectedCodes = if (mode.selectedCodes.contains(clickedCode)) {
                                mode.selectedCodes.filter { it != clickedCode }
                            } else {
                                buildList {
                                    addAll(mode.selectedCodes)
                                    add(clickedCode)
                                }
                            },
                        ),
                    )
                }
            }

            CountryListMode.SingleChoice -> {
                mainRouter.backWithCountry(_state.value.list[index].code)
            }
        }
    }

    fun onDone() {
        check(_state.value.countryListMode is CountryListMode.MultiChoice)
        mainRouter.backWithCountries(
            (_state.value.countryListMode as CountryListMode.MultiChoice).selectedCodes,
        )
    }
}
