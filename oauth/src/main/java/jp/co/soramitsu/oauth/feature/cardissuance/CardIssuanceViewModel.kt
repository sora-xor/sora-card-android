package jp.co.soramitsu.oauth.feature.cardissuance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.sdk.contract.OutwardsScreen
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.common.navigation.flow.api.KycRequirementsUnfulfilledFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.feature.cardissuance.state.CardIssuanceScreenState
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardIssuanceViewModel @Inject constructor(
    @KycRequirementsUnfulfilledFlow private val kycRequirementsUnfulfilledFlow: NavigationFlow,
    private val setActivityResult: SetActivityResult,
    private val userSessionRepository: UserSessionRepository,
    private val priceInteractor: PriceInteractor
): BaseViewModel() {

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
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = "",
                visibility = true,
                navIcon = R.drawable.ic_cross,
                actionLabel = R.string.log_out,
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
        try {
            viewModelScope.launch {
                userSessionRepository.logOutUser()
            }.invokeOnCompletion {
                setActivityResult.setResult(SoraCardResult.Logout)
            }
        } catch (e: Exception) {
            /* DO NOTHING */
        }
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        kycRequirementsUnfulfilledFlow.exit()
    }

    fun onGetXorClick() {
        kycRequirementsUnfulfilledFlow.proceed()
    }

    fun onPayIssuance() {
//        setActivityResult.setResult(
//            SoraCardResult.NavigateTo(OutwardsScreen.BUY)
//        )
    }

}