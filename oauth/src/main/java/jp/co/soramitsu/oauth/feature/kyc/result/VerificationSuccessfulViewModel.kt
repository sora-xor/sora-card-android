package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.sdk.contract.OutwardsScreen
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationSuccessfulViewModel @Inject constructor(
    private val setActivityResult: SetActivityResult,
    private val userSessionRepository: UserSessionRepository
) : BaseViewModel() {

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verification_successful_title,
                visibility = true,
                navIcon = R.drawable.ic_cross
            ),
        )
    }

    private var kycCallback: KycCallback? = null

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        setActivityResult.setResult(SoraCardResult.NavigateTo(OutwardsScreen.MAIN_SCREEN))
    }

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
                    status = SoraCardCommonVerification.Successful,
                )
            )
        }
    }
}
