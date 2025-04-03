package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VerificationFailedViewModel @Inject constructor(
    private val setActivityResult: SetActivityResult,
    private val userSessionRepository: UserSessionRepository,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
) : BaseViewModel() {

    private val _state = MutableStateFlow("")
    val state = _state.asStateFlow()

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verification_failed_title,
                visibility = true,
                navIcon = R.drawable.ic_cross,
                actionLabel = R.string.log_out,
            ),
        )
        viewModelScope.launch {
            _state.value = userSessionRepository.getPhoneNumber()
        }
    }

    override fun onToolbarAction() {
        super.onToolbarAction()
        runCatching {
            viewModelScope.launch {
                pwoAuthClientProxy.logout()
                userSessionRepository.logOutUser()
            }.invokeOnCompletion {
                setActivityResult.setResult(SoraCardResult.Logout)
            }
        }
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        onClose()
    }

    fun onClose() {
        setActivityResult.setResult(
            soraCardResult = SoraCardResult.Failure(status = SoraCardCommonVerification.Failed),
        )
    }
}
