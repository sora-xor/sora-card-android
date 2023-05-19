package jp.co.soramitsu.oauth.feature.attempts

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoFreeKycAttemptsViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val mainRouter: MainRouter
) : BaseViewModel() {

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Medium(),
            basic = BasicToolbarState(
                title = R.string.no_free_kyc_attempts_title,
                visibility = true,
                navIcon = R.drawable.ic_cross,
                actionLabel = R.string.common_support
            ),
        )
    }

    private var kycCallback: KycCallback? = null

    fun setArgs(kycCallback: KycCallback) {
        this.kycCallback = kycCallback
    }

    fun onClose() {
        viewModelScope.launch {
            kycCallback?.onFinish(
                result = SoraCardResult.Success(
                    accessToken = userSessionRepository.getAccessToken(),
                    accessTokenExpirationTime = userSessionRepository.getAccessTokenExpirationTime(),
                    refreshToken = userSessionRepository.getRefreshToken(),
                    status = SoraCardCommonVerification.NoFreeAttempt,
                )
            )
        }
    }

    override fun onToolbarNavigation() {
        onClose()
    }

    override fun onToolbarAction() {
        super.onToolbarAction()
        mainRouter.openSupportChat()
    }
}
