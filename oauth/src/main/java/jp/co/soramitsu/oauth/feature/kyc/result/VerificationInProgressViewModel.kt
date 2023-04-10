package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
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
class VerificationInProgressViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : BaseViewModel() {

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.kyc_result_verification_in_progress,
                visibility = true,
                navIcon = null,
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
                    status = SoraCardCommonVerification.Pending,
                )
            )
        }
    }
}
