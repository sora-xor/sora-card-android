package jp.co.soramitsu.oauth.feature.gatehub.step1

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.androidfoundation.format.EURO_SIGN
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.formatDouble
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class GatehubOnboardingStep1ViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val inMemoryRepo: InMemoryRepo,
) : BaseViewModel() {

    private val amounts = listOf(10000.0, 25000.0, 50000.0, 100000.0)

    private val _state = MutableStateFlow(
        GatehubOnboardingStep1State(
            buttonEnabled = false,
            amounts = buildList {
                amounts.forEach { add(TextValue.SimpleText(formatEuro(it))) }
                add(
                    TextValue.StringResWithArgs(
                        R.string.item_more_than,
                        arrayOf(formatEuro(amounts.last())),
                    ),
                )
            },
        ),
    )
    val state = _state.asStateFlow()

    private fun formatEuro(value: Double): String = "%s$EURO_SIGN".format(formatDouble(value))

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.onboarding_questions,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )
        inMemoryRepo.ghExpectedExchangeVolume = null
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        mainRouter.back()
    }

    fun onItemSelected(pos: Int) {
        check(pos in 0..amounts.size)
        inMemoryRepo.ghExpectedExchangeVolume = pos + 1
        _state.value = _state.value.copy(selectedPos = pos, buttonEnabled = true)
    }

    fun onNext() {
        mainRouter.openGatehubOnboardingStep2()
    }
}
