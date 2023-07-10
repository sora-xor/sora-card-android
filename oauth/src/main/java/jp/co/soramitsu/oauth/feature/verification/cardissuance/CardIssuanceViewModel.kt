package jp.co.soramitsu.oauth.feature.verification.cardissuance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.DisposableViewModel
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.theme.views.ScreenStatus
import jp.co.soramitsu.oauth.common.interactors.prices.api.PriceInteractor
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.oauth.feature.verification.cardissuance.state.CardIssuanceScreenState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardIssuanceViewModel @Inject constructor(
    private val priceInteractor: PriceInteractor,
    private val verificationFlow: VerificationFlow,
    private val accountInteractor: AccountInteractor
): DisposableViewModel() {

    var cardIssuanceScreenState by mutableStateOf(
        CardIssuanceScreenState(
            screenStatus = ScreenStatus.LOADING,
            xorInsufficientAmount = 0.toDouble(),
            euroInsufficientAmount = 0.toDouble(),
            euroLiquidityThreshold = 0.toDouble(),
            euroIssuanceAmount = 0
        )
    )
        private set

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Medium(),
            basic = BasicToolbarState(
                title = R.string.card_issuance_screen_title,
                visibility = true,
                actionLabel = R.string.log_out,
                navIcon = R.drawable.ic_cross
            ),
        )

        fetchXorValues()
    }

    private fun fetchXorValues() =
        viewModelScope.launch {
            kotlin.runCatching {
                val xorLiquiditySufficiency =
                    priceInteractor.calculateXorLiquiditySufficiency().getOrThrow()
                val euroLiquiditySufficiency =
                    priceInteractor.calculateEuroLiquiditySufficiency().getOrThrow()
                val euroCardIssuancePrice =
                    priceInteractor.calculateCardIssuancePrice().getOrThrow()

                cardIssuanceScreenState = cardIssuanceScreenState.copy(
                    screenStatus = ScreenStatus.READY_TO_RENDER,
                    xorInsufficientAmount = xorLiquiditySufficiency.xorInsufficiency,
                    euroInsufficientAmount = euroLiquiditySufficiency.euroInsufficiency,
                    euroLiquidityThreshold = euroLiquiditySufficiency.euroLiquidityFullPrice,
                    euroIssuanceAmount = euroCardIssuancePrice.toInt()
                )
            }.onFailure {
                cardIssuanceScreenState = cardIssuanceScreenState.copy(
                    screenStatus = ScreenStatus.ERROR
                )
            }
        }

    override fun onToolbarAction() {
        super.onToolbarAction()

        viewModelScope.launch {
            accountInteractor.logOut()
        }.invokeOnCompletion { verificationFlow.onLogout() }
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        verificationFlow.onExit()
    }

    fun onGetXorClick() {
         verificationFlow.onGetMoreXor()
    }

    fun onPayIssuance() {
        verificationFlow.onPayIssuance()
    }

}