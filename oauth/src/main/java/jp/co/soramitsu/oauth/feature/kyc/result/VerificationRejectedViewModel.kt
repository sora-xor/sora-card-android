package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VerificationRejectedViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val userSessionRepository: UserSessionRepository,
    private val kycRepository: KycRepository,
) : BaseViewModel() {

    var uiState by mutableStateOf(false)
        private set

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verification_rejected_title,
                visibility = true,
                navIcon = null,
            ),
        )
        viewModelScope.launch {
            val token = userSessionRepository.getAccessToken()
            kycRepository.hasFreeKycAttempt(token).onSuccess {
                uiState = it
            }
        }
    }

    fun onTryAgain() {
        viewModelScope.launch {
            userSessionRepository.logOutUser()
            mainRouter.openEnterPhoneNumber(clearStack = true)
        }
    }
}
