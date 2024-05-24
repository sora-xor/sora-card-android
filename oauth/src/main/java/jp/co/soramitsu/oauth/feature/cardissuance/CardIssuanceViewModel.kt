package jp.co.soramitsu.oauth.feature.cardissuance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.navigation.Destination
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.feature.cardissuance.state.CardIssuanceScreenState
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch

@HiltViewModel
class CardIssuanceViewModel @Inject constructor(
    private val setActivityResult: SetActivityResult,
    private val userSessionRepository: UserSessionRepository,
    private val priceInteractor: PriceInteractor,
    private val mainRouter: MainRouter,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
) : BaseViewModel() {

    var cardIssuanceScreenState by mutableStateOf(
        CardIssuanceScreenState(
            screenStatus = ScreenStatus.LOADING,
            xorInsufficientAmount = 0.toDouble(),
            euroInsufficientAmount = 0.toDouble(),
            euroLiquidityThreshold = 0.toDouble(),
            euroIssuanceAmount = "",
        ),
    )
        private set

    init {
        mToolbarState.value = SoramitsuToolbarState(
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

    private fun fetchXorValues() = viewModelScope.launch {
        runCatching {
            val xorLiquiditySufficiency =
                priceInteractor.calculateXorLiquiditySufficiency().getOrThrow()
            val euroLiquiditySufficiency =
                priceInteractor.calculateEuroLiquiditySufficiency().getOrThrow()
            val euroCardIssuancePrice =
                priceInteractor.calculateCardIssuancePrice()

            cardIssuanceScreenState = cardIssuanceScreenState.copy(
                screenStatus = ScreenStatus.READY_TO_RENDER,
                xorInsufficientAmount = xorLiquiditySufficiency.xorInsufficiency,
                euroInsufficientAmount = euroLiquiditySufficiency.euroInsufficiency,
                euroLiquidityThreshold = euroLiquiditySufficiency.euroLiquidityFullPrice,
                euroIssuanceAmount = euroCardIssuancePrice,
            )
        }.onFailure {
            cardIssuanceScreenState = cardIssuanceScreenState.copy(
                screenStatus = ScreenStatus.ERROR,
            )
        }
    }

    override fun onToolbarAction() {
        super.onToolbarAction()
        try {
            viewModelScope.launch {
                pwoAuthClientProxy.logout()
                userSessionRepository.logOutUser()
            }.invokeOnCompletion {
                setActivityResult.setResult(SoraCardResult.Logout)
            }
        } catch (e: Exception) {
            /* DO NOTHING */
        }
    }

    override fun onToolbarNavigation() {
        setActivityResult.setResult(
            SoraCardResult.Success(status = SoraCardCommonVerification.NotFound),
        )
    }

    fun onGetXorClick() {
        mainRouter.navigate(Destination.GET_MORE_XOR_DIALOG.route)
    }

    fun onPayIssuance() {
//        setActivityResult.setResult(
//            SoraCardResult.NavigateTo(OutwardsScreen.BUY)
//        )
    }
}
