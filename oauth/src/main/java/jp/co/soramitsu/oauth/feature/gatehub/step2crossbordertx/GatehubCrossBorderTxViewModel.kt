package jp.co.soramitsu.oauth.feature.gatehub.step2crossbordertx

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.Argument
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GatehubCrossBorderTxViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mainRouter: MainRouter,
    private val inMemoryRepo: InMemoryRepo,
    private val kycRepository: KycRepository,
) : BaseViewModel() {

    private val from: Boolean =
        requireNotNull(savedStateHandle[Argument.ADDITIONAL_DESCRIPTION.arg])

    private val _state = MutableStateFlow(
        CrossBorderTxState(
            countriesFrom = from,
            countries = emptyList(),
        ),
    )
    internal val state = _state.asStateFlow()

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.onboarding_questions,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )
        if (from) {
            inMemoryRepo.ghCountriesFrom = emptyList()
        } else {
            inMemoryRepo.ghCountriesTo = emptyList()
        }
    }

    fun setCountries(list: List<String>?) {
        if (list.isNullOrEmpty()) return
        viewModelScope.launch {
            _state.update {
                it.copy(
                    countries = kycRepository.getCountries().filter { cd ->
                        cd.code in list
                    },
                )
            }
        }
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        mainRouter.back()
    }

    fun onDone() {
        if (from) {
            inMemoryRepo.ghCountriesFrom = _state.value.countries.map { cd -> cd.code }
            mainRouter.openGatehubOnboardingStepCrossBorderTx(false)
        } else {
            inMemoryRepo.ghCountriesTo = _state.value.countries.map { cd -> cd.code }
            mainRouter.openGatehubOnboardingStep3()
        }
    }

    fun onAddCountry() {
        mainRouter.openCountryList(false)
    }

    fun onRemoveCountry(code: String) {
        _state.update {
            it.copy(
                countries = it.countries.filter { cd -> cd.code != code },
            )
        }
    }
}
