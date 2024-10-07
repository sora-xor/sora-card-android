package jp.co.soramitsu.oauth.feature.gatehub.step3

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.state.DialogAlertState
import jp.co.soramitsu.oauth.feature.gatehub.GateHubRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GatehubOnboardingStep3ViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val inMemoryRepo: InMemoryRepo,
    private val gateHubRepository: GateHubRepository,
) : BaseViewModel() {

    private val sources = listOf(
        R.string.item_salary,
        R.string.item_savings,
        R.string.item_trading_profits,
        R.string.item_other,
    )

    private val selectedItems = mutableListOf<Int>()

    private val _state = MutableStateFlow(
        GatehubOnboardingStep3State(
            buttonEnabled = false,
            sources = sources.map { TextValue.StringRes(it) },
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
        inMemoryRepo.ghSourceOfFunds = emptyList()
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        mainRouter.back()
    }

    fun onItemSelected(pos: Int) {
        check(pos in 0..sources.lastIndex)
        if (selectedItems.contains(pos)) selectedItems.remove(pos) else selectedItems.add(pos)
        inMemoryRepo.ghSourceOfFunds = selectedItems.map { it + 1 }
        _state.value = _state.value.copy(selectedPos = selectedItems.toList(), buttonEnabled = true)
    }

    fun onNext() {
        viewModelScope.launch {
            gateHubRepository.onboardUser()
                .onSuccess { (code, desc) ->
                    if (code == 0) {
                        mainRouter.openGatehubOnboardingProgress()
                    } else {
                        dialogState = DialogAlertState(
                            desc, null, true, { dialogState = null }, { dialogState = null },
                        )
                    }
                }
                .onFailure {
                    dialogState = DialogAlertState(
                        R.string.card_attention_text,
                        it.localizedMessage,
                        true,
                        { dialogState = null },
                        { dialogState = null },
                    )
                }
        }
    }
}
