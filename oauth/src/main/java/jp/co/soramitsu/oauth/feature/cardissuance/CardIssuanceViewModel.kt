package jp.co.soramitsu.oauth.feature.cardissuance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.OutwardsScreen
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.model.XorEuroPrice
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.common.navigation.flow.api.KycRequirementsUnfulfilledFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.feature.cardissuance.state.FreeCardIssuanceState
import jp.co.soramitsu.oauth.feature.cardissuance.state.PaidCardIssuanceState
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.round

@HiltViewModel
class CardIssuanceViewModel @Inject constructor(
    @KycRequirementsUnfulfilledFlow private val kycRequirementsUnfulfilledFlow: NavigationFlow,
    private val setActivityResult: SetActivityResult,
    private val inMemoryRepo: InMemoryRepo,
    private val userSessionRepository: UserSessionRepository,
    private val kycRepository: KycRepository
): BaseViewModel() {

    var freeCardIssuanceState by mutableStateOf(
        FreeCardIssuanceState(
            xorCurrentAmount = 0f,
            xorInsufficientAmount = -1f
        )
    )
        private set

    var paidCardIssuanceState by mutableStateOf(
        PaidCardIssuanceState(
            issuanceAmount = DEFAULT_ISSUANCE_AMOUNT
        )
    )
        private set

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Medium(),
            basic = BasicToolbarState(
                title = R.string.card_issuance_screen_title,
                visibility = true,
                navIcon = R.drawable.ic_cross
            ),
        )

        fetchXorValues()
    }

    private fun fetchXorValues() =
        viewModelScope.launch {
            val token = userSessionRepository.getAccessToken()
            val xorEuroPrice = kycRepository.getCurrentXorEuroPrice(token).getOrNull()

            val userAvailableXorAmount = inMemoryRepo.userAvailableXorAmount

            val xorInsufficientAmount = xorEuroPrice?.price?.minus(userAvailableXorAmount)
                ?: TODO("Handle null")

            freeCardIssuanceState = freeCardIssuanceState.copy(
                xorCurrentAmount = String.format("%.2f", userAvailableXorAmount).toFloat(),
                xorInsufficientAmount = String.format("%.2f", xorInsufficientAmount).toFloat()
            )
        }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        kycRequirementsUnfulfilledFlow.exit()
    }

    fun onGetXorClick() {
        kycRequirementsUnfulfilledFlow.proceed()
    }

    fun onPayIssuance() {
        setActivityResult.setResult(
            SoraCardResult.NavigateTo(OutwardsScreen.BUY)
        )
    }

    private companion object {
        const val DEFAULT_ISSUANCE_AMOUNT = 20
    }

}