package jp.co.soramitsu.oauth.feature.gatehub.step2

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class GatehubOnboardingStep2ViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val inMemoryRepo: InMemoryRepo,
) : BaseViewModel() {

    private val reasons = listOf(
        R.string.item_trading,
        R.string.item_sending_receiving_crypto,
        R.string.item_purchasing_crypto,
        R.string.item_holding_crypto,
        R.string.item_receiving_mining_profits,
        R.string.item_cross_border_tx,
    )

    private val selectedItems = mutableListOf<Int>()

    private val _state = MutableStateFlow(
        GatehubOnboardingStep2State(
            buttonEnabled = false,
            reasons = reasons.map { TextValue.StringRes(it) },
            selectedPos = null,
        ),
    )
    val state = _state.asStateFlow()

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.onboarding_questions,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )
        inMemoryRepo.ghExchangeReason = emptyList()
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        mainRouter.back()
    }

    fun onItemSelected(pos: Int) {
        check(pos in 0..reasons.lastIndex)
        if (selectedItems.contains(pos)) selectedItems.remove(pos) else selectedItems.add(pos)
        inMemoryRepo.ghExchangeReason = selectedItems.map { it + 1 }
        _state.update {
            it.copy(selectedPos = selectedItems.toList(), buttonEnabled = true)
        }
    }

    fun onNext() {
        val crossBorderPos = reasons.indexOf(R.string.item_cross_border_tx)
        if (_state.value.selectedPos?.contains(crossBorderPos) == true) {
            mainRouter.openGatehubOnboardingStepCrossBorderTx(true)
        } else {
            mainRouter.openGatehubOnboardingStep3()
        }
    }
}
