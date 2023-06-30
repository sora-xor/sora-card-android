package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.contract.OutwardsScreen
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationInProgressViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val setActivityResult: SetActivityResult,
    private val userSessionRepository: UserSessionRepository,
    private val currentActivityRetriever: CurrentActivityRetriever,
) : BaseViewModel() {

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.kyc_result_verification_in_progress,
                visibility = true,
                navIcon = R.drawable.ic_cross,
                actionLabel = R.string.log_out
            ),
        )
    }

    override fun onToolbarAction() {
        super.onToolbarAction()
        try {
            viewModelScope.launch {
                userSessionRepository.logOutUser()
            }.invokeOnCompletion {
                currentActivityRetriever.getCurrentActivity().finish()
            }
        } catch (e: Exception) {
            /* DO NOTHING */
        }
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        viewModelScope.launch {
            val accessToken = userSessionRepository.getAccessToken()
            val accessTokenExpirationTime = userSessionRepository.getAccessTokenExpirationTime()
            val refreshToken = userSessionRepository.getRefreshToken()
            setActivityResult.setResult(
                SoraCardResult.Success(
                    accessToken = accessToken,
                    accessTokenExpirationTime = accessTokenExpirationTime,
                    refreshToken = refreshToken,
                    status = SoraCardCommonVerification.Pending,
                )
            )
        }
    }

    fun openTelegramSupport() {
        mainRouter.openSupportChat()
    }
}
